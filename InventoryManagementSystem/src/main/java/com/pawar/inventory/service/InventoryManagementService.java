package com.pawar.inventory.service;

public class InventoryManagementService {
	
//	CategoryService categoryService = new CategoryService();
//	ItemService itemService = new ItemService(100, categoryService);
//	LpnService lpnService = new LpnService(100, itemService);
//	LocationService locationService = new LocationService();
//	InventoryService inventoryService = new InventoryService(100,lpnService,locationService,itemService);
	CategoryService categoryService;
	ItemService itemService ;
	LpnService lpnService ;
	LocationService locationService ;
	InventoryService inventoryService ;
	
	public InventoryManagementService() {
	}
	
	public InventoryManagementService(CategoryService categoryService, ItemService itemService, LpnService lpnService,
			LocationService locationService,InventoryService inventoryService) {

		this.categoryService = categoryService;
		this.itemService = itemService;
		this.lpnService = lpnService;
		this.locationService = locationService;
		this.inventoryService = inventoryService;
	}
	
	 
	
	
	
	

}
