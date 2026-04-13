package com.pawar.inventory.api.dto;

import com.pawar.inventory.model.Item;

public class InventorySopPayload {

	private Item item;
	private String location;

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}
