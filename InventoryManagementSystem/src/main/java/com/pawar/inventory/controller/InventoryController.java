package com.pawar.inventory.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.http.ParseException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pawar.inventory.exceptions.CategoryNotFoundException;
import com.pawar.inventory.exceptions.InventoryNotFoundException;
import com.pawar.inventory.exceptions.ItemNotFoundException;
import com.pawar.inventory.model.Inventory;
import com.pawar.inventory.model.Item;
import com.pawar.inventory.model.Location;
import com.pawar.inventory.model.Lpn;
import com.pawar.inventory.service.InventoryService;

import jakarta.persistence.NoResultException;

@RestController
@RequestMapping("/inventory")
@EnableJpaRepositories
public class InventoryController {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(InventoryController.class);

	@Autowired
	InventoryService inventoryService;

	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@PostMapping(value = "/createReserve", consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> createReserveInventory(@RequestBody Map<String, Object> payload) {
		String response = "";
		logger.info("Payload : " + payload);

		@SuppressWarnings("unchecked")
		Map<String, Object> jsonMap = (Map<String, Object>) payload.get("inventory");
		logger.info("Lpn : " + jsonMap);
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());

		Lpn lpn = mapper.convertValue(jsonMap.get("lpn"), Lpn.class);
		logger.info("payload inventory : " + lpn);
		Location location = mapper.convertValue(jsonMap.get("location"), Location.class);

		logger.info("" + location);

		try {
			response = inventoryService.createReserveInventory(lpn, location);
			logger.info("Response : " + response);
			return ResponseEntity.ok(response);

		} catch (ParseException e) {
			e.printStackTrace();
			return new ResponseEntity<String>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<String>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@PostMapping(value = "/createActive", consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> createActiveInventory(@RequestBody Map<String, Object> payload) {
		String response = "";
		logger.info("Payload : " + payload);

		@SuppressWarnings("unchecked")
		Map<String, Object> jsonMap = (Map<String, Object>) payload.get("inventory");
		logger.info("Lpn : " + jsonMap);
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());

		Lpn lpn = mapper.convertValue(jsonMap.get("lpn"), Lpn.class);
		logger.info("payload inventory : " + lpn);
		Location location = mapper.convertValue(jsonMap.get("location"), Location.class);

		logger.info("" + location);

		try {
			response = inventoryService.createActiveInventory(lpn, location);
			logger.info("Response : " + response);
			return ResponseEntity.ok(response);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResponseEntity.internalServerError().build();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResponseEntity.internalServerError().build();

		}

	}

	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@PostMapping(value = "/createActSop", consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> createActiveInventoryFromSop(@RequestBody Map<String, Object> payload) {
		String response = "";
		logger.info("Payload : " + payload);

//		@SuppressWarnings("unchecked")
		Map<String, Object> jsonMap = (Map<String, Object>) payload.get("inventory");
//		logger.info("LocationMap : " + jsonMap);
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());

		Item item = mapper.convertValue(jsonMap.get("item"), Item.class);
		logger.info("Item : " + item);
		String locnBrcd = (String) jsonMap.get("location");
		logger.info("locnBrcd : {}",locnBrcd);
		Location location = new Location(locnBrcd);
		// Location location = mapper.convertValue(payload.get("location"),
		// Location.class);
		logger.info("Location : " + location);
		logger.info("Creating Assignment for location : {} with item : {}", location, item);

		try {
			response = inventoryService.createActiveInventoryFromSop(item, location);
			logger.info("Response : " + response);
			return ResponseEntity.ok(response);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<String>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<String>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<String>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@PostMapping(value = "/checkActiveInventory", consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> checkActiveInventory(@RequestBody Map<String, Object> payload) {
		logger.info("Payload : " + payload);

		@SuppressWarnings("unchecked")
		Map<String, Object> jsonMap = (Map<String, Object>) payload.get("inventory");

		if (jsonMap.containsKey("lpn")) {
			logger.info("Lpn : " + jsonMap);
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());

			Lpn lpn = mapper.convertValue(jsonMap.get("lpn"), Lpn.class);

			try {
				Location existingActiveLocation = (Location) inventoryService.checkActiveInventory(lpn);
				logger.info("Response : " + existingActiveLocation);
				 return new ResponseEntity<String>(HttpStatus.OK);
			} catch (InventoryNotFoundException | NoResultException e ) {
				e.printStackTrace();
				 return new ResponseEntity<String>(HttpStatus.NOT_FOUND);		
			} 
			catch (ParseException e) {
				e.printStackTrace();
				 return new ResponseEntity<String>("Invalid payload",HttpStatus.UNPROCESSABLE_ENTITY);		
			} catch (Exception e) {
				e.printStackTrace();
				 return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);		
			}
		} else if (jsonMap.containsKey("item")) {
			logger.info("Item : " + jsonMap);
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());

			Item item = mapper.convertValue(jsonMap.get("item"), Item.class);
			Inventory existingActiveInventory = new Inventory();
			try {
				 existingActiveInventory = (Inventory) inventoryService.checkActiveInventory(item);
				logger.info("Response : " + existingActiveInventory);
				 return new ResponseEntity<String>(HttpStatus.OK);
			} catch (InventoryNotFoundException | NoResultException e ) {
				logger.warn("Response : " + existingActiveInventory);
				e.printStackTrace();
				 return new ResponseEntity<String>(HttpStatus.NOT_FOUND);		
				 } 
			catch (ParseException e) {
				e.printStackTrace();
				 return new ResponseEntity<String>("Invalid payload",HttpStatus.UNPROCESSABLE_ENTITY);		
			} catch (Exception e) {
				e.printStackTrace();
				 return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);		

			}
		} else {
			return new ResponseEntity<>("Invalid payload", HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

	@GetMapping("/list")
	public Iterable<Inventory> getfindAllInventories() {
		Iterable<Inventory> inventories = inventoryService.getfindAllInventories();
		return inventories;
	}

	@GetMapping("/list/by-name/{item_name}")
	public ResponseEntity<List<Inventory>> getInventorybyItem(@PathVariable String item_name) {
		try {
			List<Inventory> inventories = inventoryService.getInventorybyItem(item_name);
			return new ResponseEntity<>(inventories, HttpStatus.OK);
		} catch (ItemNotFoundException | CategoryNotFoundException e) {
			// Log the exception and return a user-friendly message
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/list/by-lpn/{lpn_name}")
	public Inventory getInventoryByLpn(@PathVariable String lpn_name) {
		logger.info("LPN :" + lpn_name);

		return inventoryService.getInventoryByLpn(lpn_name);
	}

	@GetMapping("/list/by-loc/{locn_brcd}")
	public List<Inventory> getInventoryByLocation(String locn_brcd) {
		return inventoryService.getInventoryByLocation(locn_brcd);
	}

	@DeleteMapping("/delete/{lpn_name}")
	public void deleteByInventoryLpn(@PathVariable String lpn_name) {
		inventoryService.deleteByInventoryLpn(lpn_name);
		logger.info("Inventory deleted with lpn ");
	}
	
	@DeleteMapping("/delete/location/{locnBrcd}/locnClass/{locnClass}")
	public void deleteActiveInventoryByLocation(@PathVariable String locnBrcd,@PathVariable String locnClass) {
		inventoryService.deleteActiveInventoryByLocation(locnBrcd,locnClass);
		logger.info("Inventory deleted with locnBrcd ");
	}
	
	@GetMapping("/locnBrcd/{locnBrcd}/locnClass/{locnClass}")
	public ResponseEntity<List<Inventory>> getExistingInventories(@PathVariable String locnBrcd,@PathVariable String locnClass) {
		try {
			List<Inventory> inventories = inventoryService.getExistingInventories(locnBrcd,locnClass);
			return new ResponseEntity<>(inventories, HttpStatus.OK);
		} catch (Exception e) {
			// Log the exception and return a user-friendly message
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
}
