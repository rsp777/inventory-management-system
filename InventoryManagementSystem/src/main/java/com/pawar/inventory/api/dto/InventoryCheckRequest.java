package com.pawar.inventory.api.dto;

public class InventoryCheckRequest {

	private InventoryCheckPayload inventory;

	public InventoryCheckPayload getInventory() {
		return inventory;
	}

	public void setInventory(InventoryCheckPayload inventory) {
		this.inventory = inventory;
	}
}
