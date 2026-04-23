package com.pawar.inventory.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pawar.inventory.api.dto.ItemRequest;
import com.pawar.inventory.exceptions.CategoryNotFoundException;
import com.pawar.inventory.exceptions.ItemNotFoundException;
import com.pawar.inventory.model.Item;
import com.pawar.inventory.model.Category;
import com.pawar.inventory.service.CategoryService;
import com.pawar.inventory.service.ItemService;

@RestController
@RequestMapping("/items")
public class ItemController {

    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);

    private final ItemService itemService;
    private final CategoryService categoryService;

    public ItemController(ItemService itemService, CategoryService categoryService) {
        this.itemService = itemService;
        this.categoryService = categoryService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addItem(@RequestBody ItemRequest request) {
        logger.info("Payload : {}", request);
        if (request == null || request.getItem() == null) {
            return ResponseEntity.badRequest().body("Invalid item payload");
        }

        Item item = request.getItem();
        Category category = item.getCategory();
        try {
            boolean isCategoryPresent = categoryService.validateCategory(category);
            if (isCategoryPresent) {
                itemService.addItem(item, category);
                return ResponseEntity.ok("Item Added Successfully : " + item.getDescription());
            }
        } catch (CategoryNotFoundException e) {
            logger.warn("Category validation failed: {}", e.getMessage());
        }
        return ResponseEntity.status(404).build();
    }

    @GetMapping("/list")
    public ResponseEntity<Iterable<Item>> getItems() {
        return ResponseEntity.ok(itemService.getfindAllItems());
    }

    @GetMapping("/list/by-id/{itemId}")
    public ResponseEntity<Item> findItemById(@PathVariable("itemId") int itemId) {
        return ResponseEntity.ok(itemService.findItemById(itemId));
    }

    @GetMapping("/list/by-desc/{itemDesc}")
    public ResponseEntity<?> findItemByDesc(@PathVariable("itemDesc") String itemDesc) {
        try {
            Item item = itemService.findItemByDesc(itemDesc);
            return ResponseEntity.ok(item);
        } catch (ItemNotFoundException | CategoryNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("/list/by-name/{itemName}")
    public ResponseEntity<?> findItemByName(@PathVariable("itemName") String itemName) {
        try {
            Item item = itemService.findItemByName(itemName);
            return ResponseEntity.ok(item);
        } catch (ItemNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @PutMapping("/update/by-id/{item_id}")
    public ResponseEntity<Item> updateItemByItemId(@PathVariable("item_id") int itemId, @RequestBody Item item) {
        logger.info("Update this item : {}", item);
        return ResponseEntity.ok(itemService.updateItemByItemId(itemId, item));
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateItemByItemName(@RequestBody ItemRequest request) {
        if (request == null || request.getItem() == null) {
            return ResponseEntity.badRequest().body("Invalid item payload");
        }
        Item item = request.getItem();
        try {
            item = itemService.updateItemByItemName(item);
            return ResponseEntity.ok("Item Edited Successfully : " + item.getDescription());
        } catch (ItemNotFoundException | CategoryNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @DeleteMapping("/delete/by-id/{itemId}")
    public ResponseEntity<Item> deleteItemByItemId(@PathVariable("itemId") int itemId) {
        return ResponseEntity.ok(itemService.deleteItemByItemId(itemId));
    }

    @DeleteMapping("/delete/by-name/{itemName}")
    public ResponseEntity<?> deleteItemByItemName(@PathVariable("itemName") String itemName) {
        try {
            Item item = itemService.deleteItemByItemName(itemName);
            return ResponseEntity.ok(item);
        } catch (ItemNotFoundException | CategoryNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }
}
