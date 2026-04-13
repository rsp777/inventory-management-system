package com.pawar.inventory.api.dto;

public class InventoryMoveRequest {

	private InventoryMovePayload inventory;

	public InventoryMovePayload getInventory() {
		return inventory;
	}

	public void setInventory(InventoryMovePayload inventory) {
		this.inventory = inventory;
	}
}
