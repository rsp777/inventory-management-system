package com.pawar.inventory.kafka;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pawar.inventory.service.ASNService;
import com.pawar.inventory.service.ItemService;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.inject.Inject;
import jakarta.jms.Destination;
import jakarta.jms.JMSContext;
import jakarta.jms.ConnectionFactory;

import javax.naming.InitialContext;
import javax.naming.NamingException;

@Singleton
@Startup
public class KafkaTopicConsumer {

	private static final Logger logger = LoggerFactory.getLogger(KafkaTopicConsumer.class);

	@Inject
	@ConfigProperty(name = "kafka.bootstrap.servers")
	private String bootstrapServers;

	@Inject
	@ConfigProperty(name = "kafka.consumer.group.id")
	private String groupId;

	@Inject
	@ConfigProperty(name = "kafka.consumer.auto.offset.reset", defaultValue = "earliest")
	private String autoOffsetReset;

	@Inject
	@ConfigProperty(name = "kafka.consumer.enable.auto.commit", defaultValue = "false")
	private boolean enableAutoCommit;

	@Inject
	@ConfigProperty(name = "kafka.consumer.delivery.mode", defaultValue = "jms")
	private String deliveryMode;

	@Inject
	@ConfigProperty(name = "wms.item.data.incoming")
	private String itemIncomingTopic;

	@Inject
	@ConfigProperty(name = "cubiscan.to.wms.topic")
	private String cubiscanTopic;

	@Inject
	@ConfigProperty(name = "kafka.topic.asn.incoming", defaultValue = "WMS.ASN.DATA.INCOMING")
	private String asnIncomingTopic;

	@Inject
	@ConfigProperty(name = "jms.topic.lookup.prefix", defaultValue = "java:/jms/topic/")
	private String jmsTopicLookupPrefix;

	@Inject
	private ItemService itemService;

	@Inject
	private ASNService asnService;

	@Resource(lookup = "java:jboss/ee/concurrency/executor/default")
	private ManagedExecutorService managedExecutorService;

	private volatile boolean running;
	private volatile KafkaConsumer<String, String> consumer;
	private volatile Future<?> consumerTask;

	@PostConstruct
	void start() {
		try {
			running = true;
			logger.info("Submitting Kafka consumer task in {} mode for topics: {}, {}, {}", deliveryMode,
					itemIncomingTopic, cubiscanTopic, asnIncomingTopic);
			consumerTask = managedExecutorService.submit(this::runConsumerLoop);
			logger.info("Kafka consumer task submitted successfully");
		} catch (Exception e) {
			logger.error("Failed to submit Kafka consumer task — consumer will NOT run", e);
		}
	}

	@PreDestroy
	void stop() {
		running = false;
		KafkaConsumer<String, String> currentConsumer = consumer;
		if (currentConsumer != null) {
			currentConsumer.wakeup();
		}
		Future<?> task = consumerTask;
		if (task != null) {
			task.cancel(true);
		}
		logger.info("Kafka consumer stopped");
	}

	private void runConsumerLoop() {
		logger.info("Kafka consumer thread started — brokers: {}", bootstrapServers);

		while (running) {
			Properties props = new Properties();
			props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
			props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
			props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
			props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
			props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
			props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, Boolean.toString(enableAutoCommit));

			try (KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(props)) {
				consumer = kafkaConsumer;
				kafkaConsumer.subscribe(List.of(itemIncomingTopic, cubiscanTopic, asnIncomingTopic));
				logger.info("Kafka consumer subscribed to topics: {}", kafkaConsumer.subscription());

				while (running) {
					ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofSeconds(1));
					if (!records.isEmpty()) {
						logger.info("Polled {} record(s)", records.count());
					}
					for (ConsumerRecord<String, String> record : records) {
						handleRecord(record);
					}
					if (!enableAutoCommit && !records.isEmpty()) {
						kafkaConsumer.commitSync();
					}
				}
			} catch (WakeupException e) {
				if (running) {
					logger.error("Kafka consumer wakeup — retrying in 5s", e);
					sleepBeforeRetry();
				}
			} catch (Exception e) {
				if (running) {
					logger.error("Kafka consumer failed — retrying in 5s", e);
					sleepBeforeRetry();
				}
			}
		}

		logger.info("Kafka consumer loop exited cleanly");
	}

	private void sleepBeforeRetry() {
		try {
			Thread.sleep(5_000);
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
		}
	}

	private void handleRecord(ConsumerRecord<String, String> record) {
		String topic = record.topic();
		String payload = record.value();

		try {
			if (isJmsDeliveryMode()) {
				forwardToJms(topic, payload);
				return;
			}
			forwardToService(topic, payload);
		} catch (Exception e) {
			logger.error("Failed to process Kafka message from topic {} using {} delivery mode", topic,
					deliveryMode, e);
		}
	}

	private boolean isJmsDeliveryMode() {
		return "jms".equalsIgnoreCase(deliveryMode) || "bridge".equalsIgnoreCase(deliveryMode);
	}

	private void forwardToService(String topic, String payload) throws Exception {
		if (itemIncomingTopic.equals(topic)) {
			itemService.incomingItemListener(payload);
			return;
		}
		if (cubiscanTopic.equals(topic)) {
			itemService.incomingItemCubicanListener(payload);
			return;
		}
		if (asnIncomingTopic.equals(topic)) {
			asnService.incomingASNListener(payload);
			return;
		}
		logger.warn("Received message for unhandled topic: {}", topic);
	}

	private void forwardToJms(String topic, String payload) throws NamingException {
		String destinationLookup = resolveDestinationLookup(topic);
		if (destinationLookup == null) {
			logger.warn("No JMS destination mapping found for Kafka topic {}", topic);
			return;
		}

		ConnectionFactory cf = InitialContext.doLookup("java:/JmsXA");
		Destination destination = InitialContext.doLookup(destinationLookup);
		try (JMSContext context = cf.createContext(JMSContext.AUTO_ACKNOWLEDGE)) {
			context.createProducer().send(destination, payload);
		}

		logger.info("Forwarded Kafka topic {} payload to JMS destination {}", topic, destinationLookup);
	}

	private String resolveDestinationLookup(String topic) {
		if (itemIncomingTopic.equals(topic) || cubiscanTopic.equals(topic) || asnIncomingTopic.equals(topic)) {
			return jmsTopicLookupPrefix + topic;
		}
		return null;
	}
}
