package com.pawar.inventory.exceptions;

public class InventoryNotFoundException extends Exception {

	private String string;
	
	public InventoryNotFoundException() {
		// TODO Auto-generated constructor stub
	}
	public InventoryNotFoundException(String string) {
		this.string=string;
	}

}
