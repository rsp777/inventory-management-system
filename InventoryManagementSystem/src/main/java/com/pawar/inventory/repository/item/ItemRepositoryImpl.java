package com.pawar.inventory.repository.item;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.pawar.inventory.exceptions.CategoryNotFoundException;
import com.pawar.inventory.exceptions.ItemNotFoundException;
import com.pawar.inventory.model.Category;
import com.pawar.inventory.model.Item;
import com.pawar.inventory.repository.category.CategoryRepository;
import com.pawar.inventory.service.CategoryService;
import com.pawar.inventory.service.ItemService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

	private final static Logger logger = Logger.getLogger(ItemRepositoryImpl.class.getName());
	private EntityManager entityManager;

	@Autowired
	CategoryRepository categoryRepository;

	public ItemRepositoryImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public Item addItem(Item item, Category category) {
		Category fetchedCategory = categoryRepository.getCategoryByName(category.getCategory_name());
		logger.info("" + fetchedCategory);
		logger.info("" + item);
		boolean validateItemData = validateItemData(item);
		
		if (validateItemData) {
			Session currentSession = entityManager.unwrap(Session.class);
			Query<Item> query = currentSession.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1", Item.class);
			query.executeUpdate();
//			// Check to see if the Category object already exists in the database.
//		    Category existingCategory = currentSession.get(Category.class, category.getId());
	//
//		    // If the Category object does not exist in the database, save it to the database.
//			if (fetchedCategory == null) {
//				logger.info("Category is null");
//				return;
//			}
			item.setItem_name(createItemName(item.getDescription()));
			item.setUnit_volume(item.getUnit_length() * item.getUnit_width() * item.getUnit_height());
			item.setCategory(fetchedCategory);
			item.setCreated_dttm(LocalDateTime.now());
			item.setLast_updated_dttm(LocalDateTime.now());
//			System.out.println("item.getLast_updated_source() != null : "+item.getLast_updated_source() != null);
			if (item.getLast_updated_source() != null && item.getCreated_source() != null) {
				item.setCreated_source(item.getCreated_source());
				item.setLast_updated_source(item.getLast_updated_source());
			} else {
				item.setCreated_source("Item Management");
				item.setLast_updated_source("Item Management");
			}
			currentSession.saveOrUpdate(item);

			logger.info("Item successfully added : " + item);
			return item;
		}
		else {
			logger.info("Item addition failed : " + item);
			return item;
		}
	}

	public boolean validateItemData(Item item) {

		boolean attr = true;
		boolean dims = true;
		boolean result = true;
		if (item.getDescription() == null || item.getCategory() == null) {
			logger.info("Item description and category cannot be null.");
			attr = false;
		}

		if (item.getUnit_length() <= 0 || item.getUnit_width() <= 0 || item.getUnit_height() <= 0) {
			logger.info("Item dimensions and volume must be greater than zero.");
			dims = false;

		}
		result = attr && dims;
		return result;
	}

	@Override
	public String createItemName(String raw_item__name_description) {

		String brand = null;
		String model = null;
		String variant = null;
		String brandCode = null;
		String modelCode = null;
		String digits = null;

		if (raw_item__name_description.contains(" ")) {
			String[] parts = raw_item__name_description.split(" ", 3);
			brand = parts[0];
			model = parts[1];
			variant = (parts.length > 2) ? parts[2] : "";
			brandCode = brand.substring(0, Math.min(brand.length(), 4)).toUpperCase();

			// Keep only the first character of each word in the model name
			modelCode = model.replaceAll("(\\p{Alnum})\\p{Alnum}*", "$1");
			digits = raw_item__name_description.replaceAll("\\D", "");
			if (!digits.isEmpty()) {

				modelCode += digits;// .substring(0, 0);
			}
			// If a variant exists, append its first character to the model code
			if (!variant.isEmpty()) {
				modelCode += variant.substring(0, 1);
			}

			// Append the first digit in the input to the model code
			String item_name = brandCode + "-" + modelCode;
			logger.info("Item Name  : " + item_name);
			return item_name;
		}
		return raw_item__name_description.toUpperCase();

	}

	@Override
	public Iterable<Item> getfindAllItems() {
		Session currentSession = entityManager.unwrap(Session.class);
		Query<Item> query = currentSession.createQuery("from Item", Item.class);
		logger.info("Query : " + query.toString());

		List<Item> listItems = query.getResultList();
		for (Iterator<Item> iterator = listItems.iterator(); iterator.hasNext();) {
			Item item = (Item) iterator.next();
			logger.info("Item Data : " + item);
		}
		return listItems;
	}

	@Override
	public Item findItemByname(String itemName) throws ItemNotFoundException, CategoryNotFoundException {
		logger.info("" + itemName);
		if (itemName.contains("%20")) {
			itemName = itemName.replaceAll("%20", " ");
			logger.info("Item without %20: " + itemName);
		}

		Session currentSession = entityManager.unwrap(Session.class);
		Query<Item> query = currentSession.createQuery("from Item where description = :description", Item.class);
		query.setParameter("description", itemName);

		List<Item> items = query.getResultList();
		if (!items.isEmpty()) {
			Item item = items.get(0);
			logger.info("Item : " + item);
			currentSession.close();
			return item;

		} else {
			currentSession.close();
			throw new ItemNotFoundException("Item Not Found : " + itemName);
		}

	}

	@Override
	public Item findItemById(int itemId) {
		Session currentSession = entityManager.unwrap(Session.class);
		Query<Item> query = currentSession.createQuery("from Item where item_id = :item_id", Item.class);
		query.setParameter("item_id", itemId);

		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			// Handle the exception here
			return null;
		}
	}

	@Override
	public Item updateItemByItemId(int item_id, Item item) {
		Session currentSession = entityManager.unwrap(Session.class);
		Item existingItem = findItemById(item_id);
		Category existingCategory = categoryRepository.getCategoryByName(item.getCategory().getCategory_name());
		logger.info("" + existingCategory);
		existingItem.setItem_name(item.getItem_name());
		existingItem.setUnit_length(item.getUnit_length());
		existingItem.setUnit_width(item.getUnit_width());
		existingItem.setUnit_height(item.getUnit_height());
		existingItem.setDescription(item.getDescription());
		existingItem.setItem_name(item.getItem_name());
		existingItem.setCategory(existingCategory);
		existingItem.setLast_updated_dttm(LocalDateTime.now());
		existingItem.setLast_updated_source("IMS");
		currentSession.saveOrUpdate(existingItem);
		logger.info("Item updated : " + existingItem);
		return existingItem;
	}

	@Override
	public Item updateItemByItemName(String item_name, Item item)
			throws ItemNotFoundException, CategoryNotFoundException {
		Session currentSession = entityManager.unwrap(Session.class);
		logger.info("item : " + item);
		Item existingItem = findItemByname(item_name);
		logger.info("existingItem : " + existingItem);

		logger.info("unit length : " + item.getUnit_length());
		logger.info("unit width : " + item.getUnit_width());
		logger.info("unit height : " + item.getUnit_height());
//		logger.info("item name : "+item.getItem_name());
		logger.info("description : " + item.getDescription());
		logger.info("category : " + item.getCategory());
		Category existingCategory = categoryRepository.getCategoryByName(item.getCategory().getCategory_name());
		existingItem.setUnit_length(item.getUnit_length());
		existingItem.setUnit_width(item.getUnit_width());
		existingItem.setUnit_height(item.getUnit_height());
		existingItem.setDescription(item.getDescription());
//		existingItem.setItem_name(item.getItem_name());
		existingItem.setCategory(existingCategory);
		existingItem.setLast_updated_dttm(LocalDateTime.now());
		existingItem.setLast_updated_source("IMS");
		currentSession.update(existingItem);
		logger.info("Item updated : " + existingItem);
		return existingItem;
	}

	@Override
	public Item deleteItemByItemId(int itemId) {
		Item item = findItemById(itemId);
		logger.info("Item to delete for : " + item);
		Session currentSession = entityManager.unwrap(Session.class);
		currentSession.delete(item);
		return item;
	}

	@Override
	public Item deleteItemByItemName(String itemName) throws ItemNotFoundException, CategoryNotFoundException {
		Item item = findItemByname(itemName);
		logger.info("Item to delete for : " + item);
		Session currentSession = entityManager.unwrap(Session.class);
		currentSession.delete(item);
		return item;
	}

}