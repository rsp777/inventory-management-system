package com.pawar.inventory.controller;

import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
import com.pawar.inventory.model.Item;
import com.pawar.inventory.model.Location;
import com.pawar.inventory.service.LocationService;

@RestController
@RequestMapping("/locations")
@EnableJpaRepositories
public class LocationController {

	private final static Logger logger = Logger.getLogger(LocationController.class.getName());

	@Autowired
	public LocationService locationService;
	
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@PostMapping(value = "/add", consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> createLocation(@RequestBody Map<String, Object> payload) {
		logger.info("Payload : " + payload);

		@SuppressWarnings("unchecked")
		Map<String, Object> jsonMap = (Map<String, Object>)payload.get("location");
		logger.info("Location : "+jsonMap);
		ObjectMapper mapper = new ObjectMapper();
	    mapper.registerModule(new JavaTimeModule());

		Location location = mapper.convertValue(jsonMap, Location.class);
		
		logger.info(""+location);
		
		locationService.createLocation(location);
		return ResponseEntity.ok("Location Added Successfully : ");

	}
	@GetMapping("/list")
	public Iterable<Location> getfindAlllocations() {
		Iterable<Location> locations = locationService.getfindAlllocations();
		return locations;
	}
	
	@GetMapping("/list/by-id/{locn_id}")
	public Location findLocationById(@PathVariable int locn_id) {
		logger.info("Input Location Id : "+locn_id);
		return locationService.findLocationById(locn_id);
	}
	
	@GetMapping("/list/by-name/{locn_brcd}")
	public Location findLocationByBarcode(@PathVariable String locn_brcd) {
		logger.info("Input Location Barcode : "+locn_brcd);
		return locationService.findLocationByBarcode(locn_brcd);
	}
	
	@PutMapping("/update/by-id/{locn_id}")
	public Location updateLocationByLocationId(@PathVariable int locn_id,@RequestBody Location location){
		logger.info("Update this location : "+location);
		location = locationService.updateLocationByLocationId(locn_id,location);
		return location;
	}
	
	@PutMapping("/update/by-name/{locn_brcd}")
	public Location updateLocationByLocationBarcode(@PathVariable String locn_brcd,@RequestBody Location location){
		logger.info("Update this location : "+location);
		location = locationService.updateLocationByLocationBarcode(locn_brcd,location);
		return location;
	}
	
	@DeleteMapping("/delete/by-id/{locn_id}")
	public Location deleteLocationByLocationId(@PathVariable int locn_id){
		Location location = locationService.deleteLocationByLocationId(locn_id);
		return location;
	}
	
	@DeleteMapping("/delete/by-name/{locn_brcd}")
	public Location deleteLocationByLocationBarcode(@PathVariable String locn_brcd){
		Location location = locationService.deleteLocationByLocationBarcode(locn_brcd);
		return location;
	}
}
