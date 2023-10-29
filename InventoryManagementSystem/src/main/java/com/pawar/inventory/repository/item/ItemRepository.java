package com.pawar.inventory.repository.item;

import com.pawar.inventory.model.Category;
import com.pawar.inventory.model.Item;

public interface ItemRepository {
	
	public Item addItem(Item item,Category category);
	public Iterable<Item> getfindAllItems();
	public Item findItemByname(String itemName);
	public Item findItemById(int itemId);
	public Item updateItemByItemId(int itemId, Item item);
	public Item updateItemByItemName(String item_name, Item item);
	public Item deleteItemByItemId(int itemId);
	public Item deleteItemByItemName(String itemName);
}
