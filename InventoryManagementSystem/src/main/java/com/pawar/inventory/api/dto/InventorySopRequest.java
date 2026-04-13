package com.pawar.inventory.api.dto;

public class InventorySopRequest {

	private InventorySopPayload inventory;

	public InventorySopPayload getInventory() {
		return inventory;
	}

	public void setInventory(InventorySopPayload inventory) {
		this.inventory = inventory;
	}
}
