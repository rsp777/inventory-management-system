package com.pawar.inventory.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pawar.inventory.config.CubiscanToWmsConfig;
import com.pawar.inventory.entity.ASNDto;
import com.pawar.inventory.entity.AssignmentModel;
import com.pawar.inventory.entity.SopEligibleItemsDto;
import com.pawar.inventory.exceptions.CategoryNotFoundException;
import com.pawar.inventory.exceptions.ItemNotFoundException;
import com.pawar.inventory.exceptions.LpnNotFoundException;
import com.pawar.inventory.model.ASN;
import com.pawar.inventory.model.Category;
import com.pawar.inventory.model.Item;
import com.pawar.inventory.model.Lpn;
import com.pawar.inventory.repository.item.ItemRepository;

import jakarta.annotation.PostConstruct;

@Service
public class ItemService {

	private final static Logger logger = LoggerFactory.getLogger(ItemService.class.getName());
	public static String WMS_ITEM_DATA_CUBISCAN;
	public static String WMS_ITEM_CUBISCAN_REALTIME_UNASSIGNMENT;
//	public static String WMS_ITEM_DIMS_SOP_UPDATE;

	private final ObjectMapper objectMapper;

	@Autowired
	private CubiscanToWmsConfig cubiscanToWmsConfig;

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@Autowired
	private ItemRepository itemRepository;
	
	public ItemService() {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
	}

	@PostConstruct
	public void init() {
		ItemService.WMS_ITEM_DATA_CUBISCAN = cubiscanToWmsConfig.getCubiscanToWmsTopic();
		ItemService.WMS_ITEM_CUBISCAN_REALTIME_UNASSIGNMENT = cubiscanToWmsConfig
				.getCubiscanToWmsRealtimeUnAssignmentTopic();
//		ItemService.WMS_ITEM_DIMS_SOP_UPDATE = cubiscanToWmsConfig.getWmsToSopItemDimsUpdate();
	}

	@Transactional
	@KafkaListener(topics = "#{@itemService.WMS_ITEM_DATA_CUBISCAN}", groupId = "consumer_group5")
	public void incomingItemListener(ConsumerRecord<String, String> consumerRecord, Acknowledgment ack)
			throws JsonMappingException, JsonProcessingException, ItemNotFoundException, CategoryNotFoundException {
//		String key = consumerRecord.key();
		String value = consumerRecord.value();
//		int partition = consumerRecord.partition();
//		logger.info("Ack : {}", ack);
		logger.info("Incoming payload : {}", value);
		Item item = objectMapper.readValue(value, Item.class);
//		logger.debug("value : {}", value);
//		logger.debug("Consumed message : " + item + " with key : " + key + " from partition : " + partition);
		logger.info("Incoming Item values : {}", item);
		try {
			Item existingItem = findItemByName(item.getItemName());
			Item itemWithNewDims = setItemDims(existingItem, item);
			
			logger.info("Cubiscan Dims Set for Item : {}", itemWithNewDims);
			
			String item_name = itemWithNewDims.getItemName();
			logger.info("item_name : {}", item_name);
			itemRepository.updateItemByItemName(itemWithNewDims);

			AssignmentModel realTimeAssignModel = convertToAssignmentModel("UNASSIGN","REALTIMEUNASSIGN",existingItem.getItemName());
			kafkaTemplate.send(WMS_ITEM_CUBISCAN_REALTIME_UNASSIGNMENT, objectMapper.writeValueAsString(realTimeAssignModel));
			logger.info("Data sent to Topic : {} for sop eligible item dims update",
					WMS_ITEM_CUBISCAN_REALTIME_UNASSIGNMENT);
			

		} catch (CategoryNotFoundException e) {
			logger.error("Category Not Found : {}", e.getMessage());

		} catch (ItemNotFoundException e) {
			logger.error("Item Not Found : {}", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error processing Kafka message: {}", e.getMessage());
		}
	}
	
	private AssignmentModel convertToAssignmentModel(String sopActionType,String batchType,String itemName) {
		AssignmentModel realTimeAssignModel = new AssignmentModel(sopActionType,batchType, itemName);
		realTimeAssignModel.setSopActionType(sopActionType);
		realTimeAssignModel.setBatchType(batchType);
		realTimeAssignModel.setItemName(itemName);
		return realTimeAssignModel;
		
	}
	
//	private SopEligibleItemsDto convertItemToSopEligbleItemsDto(Item item) {
//		SopEligibleItemsDto sopEligibleItemsDto = new SopEligibleItemsDto();
//		sopEligibleItemsDto.setItem_id(item.getItem_id());
//		sopEligibleItemsDto.setItem_brcd(item.getItemName());
//		sopEligibleItemsDto.setLength(item.getUnit_length());
//		sopEligibleItemsDto.setWidth(item.getUnit_width());
//		sopEligibleItemsDto.setHeight(item.getUnit_height());
//		sopEligibleItemsDto.setLastUpdatedDttm(LocalDateTime.now());
//		sopEligibleItemsDto.setLastUpdatedSource(item.getLast_updated_source());
//		return sopEligibleItemsDto;
//	}
	
	
	private Item setItemDims(Item existingItem, Item item) {
		if (existingItem != null && item != null) {
			existingItem.setItemName(item.getItemName());
			existingItem.setUnit_length(item.getUnit_length());
			existingItem.setUnit_width(item.getUnit_width());
			existingItem.setUnit_height(item.getUnit_height());
			existingItem.setLast_updated_dttm(item.getLast_updated_dttm());
			existingItem.setLast_updated_source("CUBISCAN");
			return existingItem;
		} else {
			return null;
		}
	}
	@Transactional
	public Item addItem(Item item, Category category) {

		return itemRepository.addItem(item, category);

	}

	@Transactional
	public Iterable<Item> getfindAllItems() {
		return itemRepository.getfindAllItems();
	}

	@Transactional
	public Item findItemByDesc(String itemDesc) throws ItemNotFoundException, CategoryNotFoundException {
		Item item = itemRepository.findItemByDesc(itemDesc);

//		if (item == null) {
//			throw new ItemNotFoundException("Item Not Found : "+itemName);
//		} 
//		else {
//			return item;
//		}
		return item;
	}

	@Transactional
	public Item findItemById(int itemId) {
		// TODO Auto-generated method stub
		return itemRepository.findItemById(itemId);
	}

	@Transactional
	public Item updateItemByItemId(int item_id, Item item) {
		// TODO Auto-generated method stub
		return itemRepository.updateItemByItemId(item_id, item);
	}

	@Transactional
	public Item updateItemByItemName(Item item)
			throws ItemNotFoundException, CategoryNotFoundException {
		// TODO Auto-generated method stub
		return itemRepository.updateItemByItemName(item);
	}

	@Transactional
	public Item deleteItemByItemId(int itemId) {
		// TODO Auto-generated method stub
		return itemRepository.deleteItemByItemId(itemId);
	}

	@Transactional
	public Item deleteItemByItemName(String itemName) throws ItemNotFoundException, CategoryNotFoundException {
		// TODO Auto-generated method stub
		return itemRepository.deleteItemByItemName(itemName);
	}

	@Transactional
	public void checkItemAttributes(Item item) {

		if (item == null) {
			logger.info("Item is null.");
			return;
		}

		if (item.getItemName() == null) {
			logger.info("Item name is null.");
		}

		if (item.getDescription() == null) {
			logger.info("Item description is null.");
		}

		if (item.getUnit_length() <= 0) {
			logger.info("Item length must be greater than zero.");
		}

		if (item.getUnit_width() <= 0) {
			logger.info("Item width must be greater than zero.");
		}

		if (item.getUnit_height() <= 0) {
			logger.info("Item height must be greater than zero.");
		}

		if (item.getUnit_volume() <= 0) {
			logger.info("Item volume must be greater than zero.");
		}
	}

	public Item findItemByName(String itemName) throws ItemNotFoundException {
		Item item = itemRepository.findItemByName(itemName);

		if (item == null) {
			throw new ItemNotFoundException("Item Not Found : "+itemName);
		} 
		else {
			return item;
		}
	}

}
