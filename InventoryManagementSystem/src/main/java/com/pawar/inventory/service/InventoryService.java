package com.pawar.inventory.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.pawar.inventory.model.Inventory;
import com.pawar.inventory.model.Location;
import com.pawar.inventory.model.Lpn;

public class InventoryService {

	private final static Logger logger = Logger.getLogger(InventoryService.class.getName());

	static List<Inventory> inventories;
	Inventory inventory;
	LpnService lpnService;
	LocationService locationService;
	ItemService itemService;

	public InventoryService() {
		// TODO Auto-generated constructor stub
	}

	public InventoryService(int initialCapacity, LpnService lpnService, LocationService locationService,
			ItemService itemService) {
		this.inventories = new ArrayList<>(initialCapacity);
		this.inventory = new Inventory();
		this.lpnService = lpnService;
		this.locationService = locationService;
		this.itemService = itemService;
	}

	public void createReserveLocationInventory(String lpn_name,String locn_brcd) {
		logger.info("Inventory needed to be created for LPN : "+lpn_name);
		
		if (lpn_name != null && locn_brcd !=null) {
			logger.info("Location Service :"+locationService);
			logger.info("Lpn Service : "+lpnService);
			String lpnFetchedFromSystem = lpnService.findLpnByName(lpn_name);
			logger.info("LPN fetched from System : " + lpnFetchedFromSystem);
			
			int inventory_id = (int) (Math.random() * 1000);
			
			while (getInventoryById(inventory_id) != null) {
				inventory_id = (int) (Math.random() * 1000);
			}
			
			Location locn = locationService.viewLocation(locn_brcd);
					
			double locn_max_qty = locn.getMax_qty();
			double locn_max_vol = locn.getMax_volume();
			double locn_length  = locn.getLength();
			double locn_width   = locn.getWidth();
			double locn_height  = locn.getHeight();
			
			Lpn lpn = lpnService.viewLpn(lpn_name);
			
			double lpn_qty = lpn.getQuantity();
			double lpn_vol = lpn.getVolume();
			double lpn_length  = lpn.getLength();
			double lpn_width   = lpn.getWidth();
			double lpn_height  = lpn.getHeight();
			
			if (lpn_length < locn_length && lpn_width < locn_width && lpn_height < locn_height
					&& lpn_qty < locn_max_qty && lpn_vol < locn_max_vol) {

				inventory.setInventory_id(inventory_id);
				inventory.setLpn(lpnFetchedFromSystem);
				inventory.setItem_name(lpnService.viewLpn(lpnFetchedFromSystem).getItem_name());
				inventory.setOn_hand_qty(lpnService.viewLpn(lpnFetchedFromSystem).getQuantity());
				inventory.setLocn_brcd(locationService.findLocationByBarcode(locn_brcd));
				inventory.setLocn_class(locationService.viewLocation(locn_brcd).getLocn_class());
				inventory.setCreated_dttm(LocalTime.now().toString());
				inventory.setLast_updated_dttm(LocalTime.now().toString());
				inventory.setCreated_source(System.getProperty("user.name"));
				inventory.setLast_updated_source(System.getProperty("user.name"));
				
				inventories.add(inventory);
				logger.info("Reserve Inventory Created : "+inventory);
				
				locationService.updateLocationProps(inventory.getLocn_brcd(),inventory.getLocn_class(), inventory.getOn_hand_qty(),
						itemService.viewItem(inventory.getItem_name()).getUnit_volume());
				logger.info("The "+lpn.getLpn_name()+"  got fit in the "+locn.getLocn_brcd());
			}
			else {
				logger.info("The "+lpn.getLpn_name()+"  is too big to fit in the "+locn.getLocn_brcd());
			}
			
			
		}

	}
	
	public void createActiveLocationInventory(String lpn_name, String locn_brcd) {
		
		// Check if the LPN exists.
	    Lpn lpn = lpnService.viewLpn(lpn_name);
	    if (lpn == null) {
	        logger.info("LPN does not exist: " + lpn_name);
	        return;
	    }

	    // Check if the location exists.
	    Location location = locationService.viewLocation(locn_brcd);
	    if (location == null) {
	        logger.info("Location does not exist: " + locn_brcd);
	        return;
	    }

	    // Create an active location inventory record.
	    inventory.setLpn(null);
	    inventory.setLocn_brcd(locn_brcd);
	    inventory.setOn_hand_qty(lpn.getQuantity());
	    inventory.setLocn_class(location.getLocn_class());
	    inventory.setCreated_dttm(LocalTime.now().toString());
	    inventory.setLast_updated_dttm(LocalTime.now().toString());
	    inventory.setCreated_source(System.getProperty("user.name"));
	    inventory.setLast_updated_source(System.getProperty("user.name"));

	    // Save the active location inventory record.
	    inventories.add(inventory);
	    locationService.updateLocationProps(inventory.getLocn_brcd(),inventory.getLocn_class(), inventory.getOn_hand_qty(),
				itemService.viewItem(inventory.getItem_name()).getUnit_volume());
	    logger.info("asdsad : "+inventories);
	    logger.info("Active Inventory Created :" + inventory);
		
	}
	private Inventory getInventoryById(int id) {
		for (Inventory inventory : inventories) {
			if (inventory.getInventory_id() == id) {
				return inventory;
			}
		}
		return null;
	}

	public Inventory viewReserveInventory(String lpn_name) {
		for (Inventory inventory : inventories) {
			if (inventory.getLpn() == lpn_name) {
				return inventory;
			}
		}
		return null;
	}
	
	public Inventory viewActiveInventory(String item_name) {
		for (Inventory inventory : inventories) {
			if (inventory.getItem_name() == item_name) {
				return inventory;
			}
		}
		return null;
	}

	

}
