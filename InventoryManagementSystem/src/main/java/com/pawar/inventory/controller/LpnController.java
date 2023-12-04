package com.pawar.inventory.controller;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpStatus;
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
import com.pawar.inventory.exceptions.ItemNotFoundException;
import com.pawar.inventory.model.Item;
import com.pawar.inventory.model.Lpn;
import com.pawar.inventory.model.Lpn;
import com.pawar.inventory.service.LpnService;

@RestController
@RequestMapping("/lpns")
@EnableJpaRepositories
public class LpnController {
	
	private final static Logger logger = Logger.getLogger(LpnController.class.getName());

	@Autowired
	LpnService lpnService;
	
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> createLpn(@RequestBody Map<String, Object> payload) {
		logger.info("Payload : " + payload);	

		@SuppressWarnings("unchecked")
		Map<String, Object> jsonMap = (Map<String, Object>)payload.get("lpn");
		logger.info("Lpn : "+jsonMap);
		ObjectMapper mapper = new ObjectMapper();
	    mapper.registerModule(new JavaTimeModule());

	    Lpn lpn = mapper.convertValue(jsonMap, Lpn.class);
	    logger.info("payload lpn : "+lpn);
		Item item = mapper.convertValue(jsonMap.get("item"), Item.class);
		
		logger.info(""+item);
		
		try {
			Lpn lpn2 = lpnService.createLpn(lpn,item);
			return new ResponseEntity<Lpn>(lpn2, HttpStatus.OK);
		} 
		catch (ItemNotFoundException e) {
			 logger.log(Level.SEVERE, "ItemNotFoundException occurred: ", e);    
			 return new ResponseEntity<String>("Item Not Found: " + e.getMessage(), HttpStatus.NOT_FOUND);
        } 
		catch (Exception e) {
        	logger.log(Level.SEVERE, "Exception occurred: ", e);
        	return new ResponseEntity<String>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
		

	}
	
	@GetMapping("/list")
	public Iterable<Lpn> getfindAllLpns() {
		Iterable<Lpn> lpns = lpnService.getfindAllLpns();
		return lpns;
	}
	
	@GetMapping("/list/by-name/{lpn_name}")
	public Lpn getLpnByName(@PathVariable String lpn_name) {
		Lpn lpn = lpnService.getLpnByName(lpn_name);
		return lpn;
	}
	
	@GetMapping("/list/by-id/{lpn_id}")
	public Lpn findLpnById(@PathVariable int lpn_id) {
		logger.info("Input Lpn Id : "+lpn_id);
		return lpnService.findLpnById(lpn_id);
	}
	
	@PutMapping("/update/by-id/{lpn_id}")
	public Lpn updateLpnByLpnId(@PathVariable int lpn_id,@RequestBody Lpn lpn){
		logger.info("Update this lpn : "+lpn);
		try {
			lpn = lpnService.updateLpnByLpnId(lpn_id,lpn);
		} catch (ItemNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lpn;
	}
	
	@PutMapping("/update/by-name/{lpn_name}")
	public Lpn updateLpnByLpnBarcode(@PathVariable String lpn_name,@RequestBody Lpn lpn){
		logger.info("Update this lpn : "+lpn);
		try {
			lpn = lpnService.updateLpnByLpnBarcode(lpn_name,lpn);
		} catch (ItemNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lpn;
	}
	
	@DeleteMapping("/delete/by-id/{lpn_id}")
	public Lpn deleteLpnByLpnId(@PathVariable int lpn_id){
		Lpn lpn = lpnService.deleteLpnByLpnId(lpn_id);
		return lpn;
	}
	
	@DeleteMapping("/delete/by-name/{lpn_name}")
	public Lpn deleteLpnByLpnBarcode(@PathVariable String lpn_name){
		Lpn lpn = lpnService.deleteLpnByLpnBarcode(lpn_name);
		return lpn;
	}
}
