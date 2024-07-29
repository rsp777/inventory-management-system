package com.pawar.inventory.service;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pawar.inventory.exceptions.CategoryNotFoundException;
import com.pawar.inventory.exceptions.ItemNotFoundException;
import com.pawar.inventory.model.Category;
import com.pawar.inventory.model.Item;
import com.pawar.inventory.repository.item.ItemRepository;

@Service
public class ItemService {

	private final static Logger logger = Logger.getLogger(ItemService.class.getName());

	@Autowired
	private ItemRepository itemRepository;

	@Transactional
	public Item addItem(Item item, Category category) {

		return itemRepository.addItem(item, category);

	}

	@Transactional
	public Iterable<Item> getfindAllItems() {
		return itemRepository.getfindAllItems();
	}

	@Transactional
	public Item findItemByname(String itemName) throws ItemNotFoundException, CategoryNotFoundException  {
		Item item = itemRepository.findItemByname(itemName);
		
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
		return itemRepository.updateItemByItemId(item_id,item);
	}

	@Transactional
	public Item updateItemByItemName(String item_name, Item item) throws ItemNotFoundException, CategoryNotFoundException {
		// TODO Auto-generated method stub
		return itemRepository.updateItemByItemName(item_name,item);
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

		if (item.getItem_name() == null) {
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

	

}
