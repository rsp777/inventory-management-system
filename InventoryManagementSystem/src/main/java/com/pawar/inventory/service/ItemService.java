package com.pawar.inventory.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.pawar.inventory.model.Item;

public class ItemService {

	private final static Logger logger = Logger.getLogger(ItemService.class.getName());

	List<Item> items;
	Item item;
	CategoryService categoryService;

	public ItemService() {
		// TODO Auto-generated constructor stub
	}

	public ItemService(int initialCapacity, CategoryService categoryService) {
		this.items = new ArrayList<>(initialCapacity);
		this.item = new Item();
		this.categoryService = categoryService;
		// rest of your code...
	}

	public void addItem(String item_name, double unit_length, double unit_width, double unit_height, double unit_volume,
			String description, String category) {
		logger.info("" + categoryService);
//		String scannedCategory = categoryService.findCategorybyName(category);
//		logger.info("" + category);
//		logger.info("" + categoryService.categories.isEmpty());
		if (item_name == null || description == null || category == null) {
			logger.info("Item name, description and category cannot be null.");
			return;
		}

		if (unit_length <= 0 || unit_width <= 0 || unit_height <= 0 || unit_volume <= 0) {
			logger.info("Item dimensions and volume must be greater than zero.");
			return;
		}

		int item_id = (int) (Math.random() * 1000);
		while (getItemById(item_id) != null) {
			item_id = (int) (Math.random() * 1000);
		}

		item.setItem_id(item_id);
		item.setItem_name(item_name);
		item.setUnit_length(unit_length);
		item.setUnit_width(unit_width);
		item.setUnit_height(unit_height);
		item.setUnit_volume(unit_volume);
		item.setDescription(description);
//		item.setCategory(scannedCategory);
		item.setCreated_dttm(LocalTime.now() + "");
		item.setLast_updated_dttm(LocalTime.now() + "");
		item.setCreated_source(System.getProperty("user.name"));
		item.setLast_updated_source(System.getProperty("user.name"));
		items.add(item);
		logger.info("Item successfully added : " + items);

	}

	public Item viewItem(String item_name) {
		if (item_name != null) {
			for (Item item : items) {
				if (item.getItem_name() == item_name) {
					return item;
				}
			}
		}
		return null;
	}

	public void deleteItem(int id) {
		items.removeIf(item -> item.getItem_id() == id);
	}

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

	private Item getItemById(int id) {
		for (Item item : items) {
			if (item.getItem_id() == id) {
				return item;
			}
		}
		return null;
	}

	public String findItemByName(String item_name) {
//		logger.info("Items is Empty : " + (items == null));
		if (item_name != null) {
			for (Item item : items) {
				if (item.getItem_name().equals(item_name)) {
					return item_name;
				}
			}
		}
		return "Item Not Found, Please add this item: " + item_name;
	}

	@Override
	public String toString() {
		return "ItemService [items=" + items + ", item=" + item + ", categoryService=" + categoryService + "]";
	}

}
