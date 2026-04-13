package com.pawar.inventory.api.dto;

import com.pawar.inventory.model.Item;
import com.pawar.inventory.model.Lpn;

public class InventoryCheckPayload {

	private Lpn lpn;
	private Item item;

	public Lpn getLpn() {
		return lpn;
	}

	public void setLpn(Lpn lpn) {
		this.lpn = lpn;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}
}
