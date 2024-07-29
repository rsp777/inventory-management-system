package com.pawar.inventory.controller;

import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pawar.inventory.exceptions.CategoryNotFoundException;
import com.pawar.inventory.exceptions.ItemNotFoundException;
import com.pawar.inventory.model.Category;
import com.pawar.inventory.model.Item;
import com.pawar.inventory.service.CategoryService;
import com.pawar.inventory.service.ItemService;

@RestController
@RequestMapping("/items")
@EnableJpaRepositories
public class ItemController {

	private final static Logger logger = Logger.getLogger(ItemController.class.getName());

	@Autowired
	public ItemService itemService;

	@Autowired
	private CategoryService categoryService;

	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@PostMapping(value = "/add", consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> addItem(@RequestBody Map<String, Object> payload) {
		logger.info("Payload : " + payload);

		@SuppressWarnings("unchecked")
		Map<String, Object> jsonMap = (Map<String, Object>) payload.get("item");
		logger.info("Item : " + jsonMap);
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		Item item = mapper.convertValue(jsonMap, Item.class);
		Category category = mapper.convertValue(jsonMap.get("category"), Category.class);
		boolean isCategoryPresent=false;
		try {
//			logger.info("isCategoryPresent : "+categoryService.validateCategory(category));
			isCategoryPresent = categoryService.validateCategory(category);
			logger.info("" + item);
			logger.info("isCategoryPresent : "+isCategoryPresent);

			if (isCategoryPresent) {

				itemService.addItem(item, category);
				return ResponseEntity.ok("Item Added Successfully : "+item.getItem_name());

			}
		} catch (CategoryNotFoundException e) {
			// TODO Auto-generated catch block
			e.getMessage();
		}
		
		return (ResponseEntity<?>) ResponseEntity.notFound();
	}

	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@GetMapping("/list")
	public Iterable<Item> getItems() {
		Iterable<Item> items = itemService.getfindAllItems();
		return items;
	}

	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@GetMapping("/list/by-id/{itemId}")
	public Item findItemById(@PathVariable int itemId) {
		return itemService.findItemById(itemId);
	}

	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@GetMapping("/list/by-name/{itemName}")
	public ResponseEntity<Item> findItemByName(@PathVariable String itemName) {
		try {
			Item item = itemService.findItemByname(itemName);
			return new ResponseEntity<>(item, HttpStatus.OK);
		} catch (ItemNotFoundException | CategoryNotFoundException e) {
			// Log the exception and return a user-friendly message
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@PutMapping("/update/by-id/{item_id}")
	public Item updateItemByItemId(@PathVariable int item_id, @RequestBody Item item) {
		logger.info("Update this item : " + item);
		item = itemService.updateItemByItemId(item_id, item);
		return item;
	}

	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@PutMapping("/update/by-name/{itemName}")
	public ResponseEntity<?> updateItemByItemName(@PathVariable String itemName, @RequestBody Map<String, Object> itemPayload) {
		logger.info("Update this item : " + itemName);
		Map<String, Object> jsonMap = (Map<String, Object>) itemPayload.get("item");
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		Item item = mapper.convertValue(jsonMap, Item.class);
		try {
			
			item = itemService.updateItemByItemName(itemName, item);
		} catch (ItemNotFoundException | CategoryNotFoundException e) {

			e.printStackTrace();
		}
		return ResponseEntity.ok("Item Edited Successfully : "+item.getDescription());
	}

	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@DeleteMapping("/delete/by-id/{itemId}")
	public Item deleteItemByItemId(@PathVariable int itemId) {
		Item item = itemService.deleteItemByItemId(itemId);
		return item;
	}

	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@DeleteMapping("/delete/by-name/{itemName}")
	public Item deleteItemByItemName(@PathVariable String itemName) {
		Item item;
		try {
			item = itemService.deleteItemByItemName(itemName);
			return item;
		} catch (ItemNotFoundException | CategoryNotFoundException e) {

			e.printStackTrace();
			return null;
		}

	}
}
