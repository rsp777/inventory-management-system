package com.pawar.inventory.service;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pawar.inventory.exceptions.CategoryNotFoundException;
import com.pawar.inventory.exceptions.InventoryNotFoundException;
import com.pawar.inventory.exceptions.ItemNotFoundException;
import com.pawar.inventory.model.Inventory;
import com.pawar.inventory.model.Item;
import com.pawar.inventory.model.Location;
import com.pawar.inventory.model.Lpn;
import com.pawar.inventory.repository.inventory.InventoryRepository;

import jakarta.persistence.NoResultException;

@Service
public class InventoryService {

	private final static Logger logger = Logger.getLogger(InventoryService.class.getName());

	@Autowired
	InventoryRepository inventoryRepository;

	@Autowired
	ItemService itemService;

	@Autowired
	LocationService locationService;
	
	@Autowired
	ASNService asnService;
	
	@Autowired
	LpnService lpnService;
	
	@Transactional
	public Inventory createInventory(Lpn lpn,Session currentSession) {
		logger.info("Inventory needed to be created for LPN : " + lpn);

		return inventoryRepository.createInventory(lpn,currentSession);

	}

	@Transactional
	public String createReserveInventory(Lpn lpn, Location locn) throws ParseException, IOException {
		logger.info("Locating LPN to a Reserve Location : " + lpn.getLpn_name());
		String response = inventoryRepository.createReserveInventory(lpn, locn);
		return response;

	}

	@Transactional
	public String createActiveInventory(Lpn lpn, Location locn) throws ClientProtocolException, IOException {
		logger.info("Replenishment of Active Location : " + lpn.getLpn_name());
		return inventoryRepository.createActiveInventory(lpn, locn);
	}

	@Transactional
	public Iterable<Inventory> getfindAllInventories() {

		return inventoryRepository.viewAllInventory();
	}

	@Transactional
	public List<Inventory> getInventorybyItem(String item_name) throws ItemNotFoundException, CategoryNotFoundException  {
		// TODO Auto-generated method stub
		Item item = itemService.findItemByName(item_name);
		return inventoryRepository.getInventorybyItem(item);
	}

	public List<Inventory> getInventoryByLocation(String locn_brcd) {

		Location location = locationService.findLocationByBarcode(locn_brcd);
		return inventoryRepository.getInventoryByLocation(location);
	}

	@Transactional
	public Inventory getInventoryByLpn(String lpn_name) {

		return inventoryRepository.getInventoryByLpn(lpn_name);

	}

	@Transactional
	public void deleteByInventoryLpn(String lpn_name) {

		inventoryRepository.deleteByInventoryLpn(lpn_name);
	}

	@Transactional
	public Inventory updateInventoryQty(Inventory inventory,int adjustQty){
		return inventoryRepository.updateInventoryQty(inventory, adjustQty);
	}

	@Transactional
	public Object checkActiveInventory(Object object) throws InventoryNotFoundException,NoResultException {
		// TODO Auto-generated method stub
		return inventoryRepository.checkActiveInventory(object);
	}

	public String createActiveInventoryFromSop(Item item, Location location) throws ClientProtocolException, IOException {
		// TODO Auto-generated method stub
		return inventoryRepository.createActiveInventoryFromSop(item,location);
	}
	
	public List<Inventory> getExistingInventories(String locnBrcd, String locnClass){
		return inventoryRepository.getExistingInventories(locnBrcd, locnClass);
	}

	public void deleteActiveInventoryByLocation(String locnBrcd, String locnClass) {
		inventoryRepository.deleteActiveInventoryByLocation(locnBrcd, locnClass);
		
	}
}
