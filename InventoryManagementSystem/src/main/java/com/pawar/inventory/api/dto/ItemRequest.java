package com.pawar.inventory.api.dto;

import com.pawar.inventory.model.Item;

public class ItemRequest {

	private Item item;

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}
}
