package com.pawar.inventory.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pawar.inventory.exceptions.CategoryNotFoundException;
import com.pawar.inventory.exceptions.ItemNotFoundException;
import com.pawar.inventory.service.ASNService;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.inject.Inject;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;

@MessageDriven(name = "AsnIncomingListenerBean", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Topic"),
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:/jms/topic/WMS.ASN.DATA.INCOMING") })
public class AsnIncomingListenerBean implements MessageListener {

	private static final Logger logger = LoggerFactory.getLogger(AsnIncomingListenerBean.class);

	@Inject
	private ASNService asnService;

	@Override
	public void onMessage(Message message) {
		String payload = extractPayload(message);
		if (payload == null) {
			return;
		}

		try {
			asnService.incomingASNListener(payload);
		} catch (ItemNotFoundException | CategoryNotFoundException | JsonProcessingException e) {
			logger.error("Failed to process incoming ASN message: {}", e.getMessage(), e);
		}
	}

	private String extractPayload(Message message) {
		if (!(message instanceof TextMessage textMessage)) {
			logger.warn("Unsupported JMS message type: {}", message.getClass().getName());
			return null;
		}

		try {
			return textMessage.getText();
		} catch (JMSException e) {
			logger.error("Unable to read JMS text payload", e);
			return null;
		}
	}
}
