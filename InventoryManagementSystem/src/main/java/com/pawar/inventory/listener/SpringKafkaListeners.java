package com.pawar.inventory.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pawar.inventory.exceptions.CategoryNotFoundException;
import com.pawar.inventory.exceptions.ItemNotFoundException;
import com.pawar.inventory.service.ASNService;
import com.pawar.inventory.service.ItemService;

@Component
public class SpringKafkaListeners {

    private static final Logger logger = LoggerFactory.getLogger(SpringKafkaListeners.class);

    private final ItemService itemService;
    private final ASNService asnService;

    @Value("${wms.item.data.incoming:WMS.ITEM.DATA.INCOMING}")
    private String itemIncomingTopic;

    @Value("${cubiscan.to.wms.topic:WMS.ITEM.DATA.CUBISCAN}")
    private String cubiscanTopic;

    @Value("${kafka.topic.asn.incoming:WMS.ASN.DATA.INCOMING}")
    private String asnIncomingTopic;

    @Autowired
    public SpringKafkaListeners(ItemService itemService, ASNService asnService) {
        this.itemService = itemService;
        this.asnService = asnService;
    }

    @KafkaListener(topics = "${wms.item.data.incoming}")
    public void listenItemIncoming(String payload) {
        try {
            itemService.incomingItemListener(payload);
        } catch (ItemNotFoundException | CategoryNotFoundException | JsonProcessingException e) {
            logger.error("Failed to process incoming item message: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "${cubiscan.to.wms.topic}")
    public void listenCubiscan(String payload) {
        try {
            itemService.incomingItemCubicanListener(payload);
        } catch (ItemNotFoundException | CategoryNotFoundException | JsonProcessingException e) {
            logger.error("Failed to process cubiscan item message: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "${kafka.topic.asn.incoming}")
    public void listenAsnIncoming(String payload) {
        try {
            asnService.incomingASNListener(payload);
        } catch (ItemNotFoundException | CategoryNotFoundException | JsonProcessingException e) {
            logger.error("Failed to process incoming ASN message: {}", e.getMessage(), e);
        }
    }
}
