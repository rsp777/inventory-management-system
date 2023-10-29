package com.pawar.inventory.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pawar.inventory.model.Inventory;
import com.pawar.inventory.model.Item;
import com.pawar.inventory.model.Location;
import com.pawar.inventory.model.Lpn;
import com.pawar.inventory.service.InventoryService;

@RestController
@RequestMapping("/inventory")
@EnableJpaRepositories
public class InventoryController {
	
	private final static Logger logger = Logger.getLogger(InventoryController.class.getName());

	@Autowired
	InventoryService inventoryService;
	
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@PostMapping(value = "/createReserve", consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> createReserveInventory(@RequestBody Map<String, Object> payload) {
		logger.info("Payload : " + payload);

		@SuppressWarnings("unchecked")
		Map<String, Object> jsonMap = (Map<String, Object>)payload.get("inventory");
		logger.info("Lpn : "+jsonMap);
		ObjectMapper mapper = new ObjectMapper();
	    mapper.registerModule(new JavaTimeModule());

	    Lpn lpn = mapper.convertValue(jsonMap.get("lpn"), Lpn.class);
	    logger.info("payload inventory : "+lpn);
		Location location = mapper.convertValue(jsonMap.get("location"), Location.class);
		
		logger.info(""+location);
		
		try {
			inventoryService.createReserveInventory(lpn,location);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ResponseEntity.ok("Lpn Located to Reserve Location Successfully : ");

	}	
	
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@PostMapping(value = "/createActive", consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> createActiveInventory(@RequestBody Map<String, Object> payload) {
		logger.info("Payload : " + payload);

		@SuppressWarnings("unchecked")
		Map<String, Object> jsonMap = (Map<String, Object>)payload.get("inventory");
		logger.info("Lpn : "+jsonMap);
		ObjectMapper mapper = new ObjectMapper();
	    mapper.registerModule(new JavaTimeModule());

	    Lpn lpn = mapper.convertValue(jsonMap.get("lpn"), Lpn.class);
	    logger.info("payload inventory : "+lpn);
		Location location = mapper.convertValue(jsonMap.get("location"), Location.class);
		
		logger.info(""+location);
		
		try {
			inventoryService.createActiveInventory(lpn,location);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ResponseEntity.ok("Inventory Located to Active Location Successfully : ");

	}	
	
	@GetMapping("/list")
	public Iterable<Inventory> getfindAllInventories() {
		Iterable<Inventory> inventories = inventoryService.getfindAllInventories();
		return inventories;
	}
	
	@GetMapping("/list/by-name/{item_name}")
	public List<Inventory> getInventorybyItem(@PathVariable String item_name) {
		List<Inventory> inventories = inventoryService.getInventorybyItem(item_name);
		return inventories;
	}
	
	@GetMapping("/list/by-lpn/{lpn_name}")
	public Inventory getInventoryByLpn(String lpn_name) {
		return inventoryService.getInventoryByLpn(lpn_name);
	}
	
	@GetMapping("/list/by-lpn/{locn_brcd}")
	public List<Inventory> getInventoryByLocation(String locn_brcd) {
		return inventoryService.getInventoryByLocation(locn_brcd);
	}
	
	@DeleteMapping("/delete/{lpn_name}")
	public void deleteByInventoryLpn(@PathVariable String lpn_name) {
		 inventoryService.deleteByInventoryLpn(lpn_name);
		 logger.info("Inventory deleted with lpn ");
	}
	
}
