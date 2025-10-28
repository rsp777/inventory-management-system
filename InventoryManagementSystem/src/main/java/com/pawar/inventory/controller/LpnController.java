package com.pawar.inventory.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pawar.inventory.exceptions.CategoryNotFoundException;
import com.pawar.inventory.exceptions.ItemNotFoundException;
import com.pawar.inventory.exceptions.LpnNotFoundException;
import com.pawar.inventory.model.Item;
import com.pawar.inventory.model.Lpn;
import com.pawar.inventory.service.LpnService;

import ch.qos.logback.classic.Level;

@RestController
@RequestMapping("/lpns")
@EnableJpaRepositories
public class LpnController {

	private final static Logger logger = LoggerFactory.getLogger(LpnController.class);

	@Autowired
	LpnService lpnService;

	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> createLpn(@RequestBody Map<String, Object> payload) {
		logger.info("Payload : " + payload);

		@SuppressWarnings("unchecked")
		Map<String, Object> jsonMap = (Map<String, Object>) payload.get("lpn");
		logger.info("Lpn : " + jsonMap);
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());

		Lpn lpn = mapper.convertValue(jsonMap, Lpn.class);
		logger.info("payload lpn : " + lpn);
		Item item = mapper.convertValue(jsonMap.get("item"), Item.class);

		logger.info("" + item);

		try {
			Lpn lpn2 = lpnService.createLpn(lpn, item);
			return new ResponseEntity<Lpn>(lpn2, HttpStatus.OK);
		} catch (LpnNotFoundException e) {
			logger.error("LpnNotFoundException occurred: ", e);
			return new ResponseEntity<String>("LPN Not Found: " + e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (ItemNotFoundException e) {
			logger.error("ItemNotFoundException occurred: ", e);
			return new ResponseEntity<String>("Item Not Found: " + e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			logger.error("Exception occurred: ", e);
			return new ResponseEntity<String>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping("/list")
	public Iterable<Lpn> getfindAllLpns() {
		Iterable<Lpn> lpns = lpnService.getfindAllLpns();
		return lpns;
	}

	@GetMapping("/list/by-name/{lpn_name}")
	public ResponseEntity<Lpn> getLpnByName(@PathVariable String lpn_name) {
		try {
			Lpn lpn = lpnService.getLpnByName(lpn_name);
			return ResponseEntity.ok(lpn); // Return 200 OK with the LPN
		} catch (LpnNotFoundException e) {
			// LPN not found, return 404 Not Found
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/list/by-id/{lpn_id}")
	public Lpn findLpnById(@PathVariable int lpn_id) {
		logger.info("Input Lpn Id : " + lpn_id);
		return lpnService.findLpnById(lpn_id);
	}
	
	@GetMapping("/list/category/{category}")
	public ResponseEntity<List<Lpn>> findLpnByCategory(@PathVariable String category) {
	    logger.info("Input category: " + category);
	    List<Lpn> lpn;
	    try {
	        lpn = lpnService.findLpnByCategory(category);
	        logger.info("Lpn by Category : {}",lpn);
	        return ResponseEntity.ok(lpn); // Return the found LPN with HTTP 200 status
	    } catch (LpnNotFoundException e) {
	        logger.error("LPN not found for category: " + category, e);
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Return HTTP 404 status
	    } catch (Exception e) {
	        logger.error("An unexpected error occurred", e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Return HTTP 500 status
	    }
	}

	@PutMapping("/update/by-id/{lpn_id}")
	public Lpn updateLpnByLpnId(@PathVariable int lpn_id, @RequestBody Lpn lpn) {
		logger.info("Update this lpn : " + lpn);
		try {
			lpn = lpnService.updateLpnByLpnId(lpn_id, lpn);
		} catch (ItemNotFoundException | CategoryNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lpn;
	}

	@PutMapping("/update/by-name/{lpn_name}/{adjustQty}")
	public ResponseEntity<String> updateLpnByLpnBarcode(@PathVariable String lpn_name, @RequestBody Lpn lpn,
			@PathVariable int adjustQty) {
		try {
			Lpn updatedLpn = lpnService.updateLpnByLpnBarcode(lpn_name, lpn, adjustQty);
			return ResponseEntity.ok("Lpn updated successfully :"+updatedLpn); // Return 200 OK with the updated LPN
		} 
		catch (ItemNotFoundException | CategoryNotFoundException e) {
			return ResponseEntity.notFound().build(); // Return 404 Not Found
		} 
		catch (LpnNotFoundException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Handle LPN not found
		}
	}

//	@CrossOrigin(origins = "*", allowedHeaders = "*")
//	@PutMapping(value = "/deallocate", consumes = "application/json", produces = "application/json")
//	public ResponseEntity<?> deallocateLpn(@PathVariable String lpnName) {
//		logger.info("Lpn : " + lpnName);	
//			   		
//		try {
//			Lpn lpn = lpnService.deallocateLpn(lpnName);
//			return ResponseEntity.ok("Lpn deallocated successfully :"+lpn.getLpn_name());
//		} 
//		catch (ItemNotFoundException e) {
//			 logger.log(Level.SEVERE, "ItemNotFoundException occurred: ", e);    
//			 return new ResponseEntity<String>("Item Not Found: " + e.getMessage(), HttpStatus.NOT_FOUND);
//        } 
//		catch (Exception e) {
//        	logger.log(Level.SEVERE, "Exception occurred: ", e);
//        	return new ResponseEntity<String>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//		
//
//	} 

	@DeleteMapping("/delete/by-id/{lpn_id}")
	public Lpn deleteLpnByLpnId(@PathVariable int lpn_id) {
		Lpn lpn = lpnService.deleteLpnByLpnId(lpn_id);
		return lpn;
	}

	@DeleteMapping("/delete/by-name/{lpn_name}")
	public ResponseEntity<String> deleteLpnByLpnBarcode(@PathVariable String lpn_name) {
		
		try {
			lpnService.deleteLpnByLpnBarcode(lpn_name);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} catch (LpnNotFoundException e) {
			// TODO Auto-generated catch block
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Handle LPN not found
		}
		
	}
}
