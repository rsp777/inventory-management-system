package com.pawar.inventory.api.dto;

import com.pawar.inventory.model.Location;
import com.pawar.inventory.model.Lpn;

public class InventoryMovePayload {

	private Lpn lpn;
	private Location location;

	public Lpn getLpn() {
		return lpn;
	}

	public void setLpn(Lpn lpn) {
		this.lpn = lpn;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
}
