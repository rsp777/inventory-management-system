package com.pawar.inventory.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.pawar.inventory.model.Lpn;

public class LpnService {

	private final static Logger logger = Logger.getLogger(LpnService.class.getName());

	List<Lpn> lpns;
	Lpn lpn;

	ItemService itemService;
	InventoryService inventoryService;
	

	public LpnService(int initialCapacity, ItemService itemService) {
		this.lpns = new ArrayList<>(initialCapacity);
		this.lpn = new Lpn();
		this.itemService = itemService;
	}

	public void createLpn(String lpn_name, String item_name, int quantity) {
		logger.info("Item Service : " + itemService);
		logger.info("Item before validation : " + item_name);
		String scannedItem = itemService.findItemByName(item_name);
		logger.info("Item after validation : " + scannedItem);

		if (scannedItem == null || scannedItem.equals("Item Not Found, Please add this item: " + item_name)) {
			logger.info(scannedItem);
			return;
		}

		else if (lpn_name == null || item_name == null) {
			logger.info("LPN name and item cannot be null.");
			return;
		}

		int lpn_id = (int) (Math.random() * 1000);

		while (getLpnById(lpn_id) != null) {
			lpn_id = (int) (Math.random() * 1000);
		}

		lpn.setLpn_id(lpn_id);
		lpn.setLpn_name(lpn_name);
		lpn.setItem_name(scannedItem);
		lpn.setQuantity(quantity);
		lpn.setLength(itemService.viewItem(item_name).getUnit_length());
		lpn.setWidth(itemService.viewItem(item_name).getUnit_width());
		lpn.setHeight(itemService.viewItem(item_name).getUnit_height());
		lpn.setCreated_dttm(LocalTime.now().toString());
		lpn.setLast_updated_dttm(LocalTime.now().toString());
		lpn.setCreated_source(System.getProperty("user.name"));
		lpn.setLast_updated_source(System.getProperty("user.name"));

		lpns.add(lpn);

		logger.info("LPN successfully created : " + lpn);
		
		
		
		
	}

	private Lpn getLpnById(int id) {
		for (Lpn lpn : lpns) {
			if (lpn.getLpn_id() == id) {
				return lpn;
			}
		}
		return null;
	}

	public String findLpnByName(String lpn_name) {
//		logger.info(""+lpns);
		if (lpn_name != null) {
			for (Lpn lpn : lpns) {

				if (lpn.getLpn_name().equals(lpn_name)) {
					return lpn_name;
				}

			}
		}

		return null;

	}

	public Lpn viewLpn(String lpn_name) {
		for (Lpn lpn : lpns) {
			if (lpn.getLpn_name() == lpn_name) {
				return lpn;
			}
		}
		return null;
	}

	public void deletLpn(int id) {
		lpns.removeIf(lpn -> lpn.getLpn_id() == id);
	}

	@Override
	public String toString() {
		return "LpnService [lpns=" + lpns + ", lpn=" + lpn + ", itemService=" + itemService + "]";
	}

}
