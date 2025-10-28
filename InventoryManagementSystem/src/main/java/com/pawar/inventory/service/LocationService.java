package com.pawar.inventory.service;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.pawar.inventory.model.Location;
import com.pawar.inventory.repository.location.LocationRepository;

@Service
public class LocationService {

	private final static Logger logger = Logger.getLogger(LocationService.class.getName());

	@Autowired
	private LocationRepository locationRepository;

	@Transactional
	public Location createLocation(Location location) {
		return locationRepository.addLocation(location);
	}
	
	@Transactional
	public Iterable<Location> getfindAlllocations() {
		return locationRepository.getfindAlllocations();
	}
	
	@Transactional
	public Location findLocationByBarcode(String locn_brcd) {
		
		return locationRepository.findLocationByBarcode(locn_brcd);
	}

	@Transactional
	public Location findLocationById(int locn_id) {
		// TODO Auto-generated method stub
		return locationRepository.findLocationById(locn_id);
	}

	@Transactional
	public Location updateLocationByLocationId(int locn_id, Location location) {
		// TODO Auto-generated method stub
		return locationRepository.updateLocationByLocationId(locn_id,location);
	}

	@Transactional
	public Location updateLocationByLocationBarcode(String locn_brcd, Location location) {
		// TODO Auto-generated method stub
		return locationRepository.updateLocationByLocationBarcode(locn_brcd,location);
	}

	@Transactional
	public Location deleteLocationByLocationId(int locn_id) {
		// TODO Auto-generated method stub
		return locationRepository.deleteLocationByLocationId(locn_id);
	}

	@Transactional
	public Location deleteLocationByLocationBarcode(String locn_brcd) {
		// TODO Auto-generated method stub
		return locationRepository.deleteLocationByLocationBarcode(locn_brcd);
	}
	
	public void checkLocationAttributes(Location location) {
		if (location == null) {
			logger.info("Location is null.");
			return;
		}

		if (location.getLocn_brcd() == null) {
			logger.info("Location barcode is null.");
		}

		if (location.getLength() <= 0) {
			logger.info("Location length must be greater than zero.");
		}

		if (location.getWidth() <= 0) {
			logger.info("Location width must be greater than zero.");
		}

		if (location.getHeight() <= 0) {
			logger.info("Location height must be greater than zero.");
		}

		if (location.getMax_qty() <= 0) {
			logger.info("Location maximum quantity must be greater than zero.");
		}

		if (location.getMax_volume() <= 0) {
			logger.info("Location maximum volume must be greater than zero.");
		}

		if (location.getMax_weight() <= 0) {
			logger.info("Location maximum weight must be greater than zero.");
		}
	}

	@Transactional
	public Location updateOccupiedQty(Location location,int adjustQty){
		return locationRepository.updateOccupiedQty(location, adjustQty);
	}

	public Iterable<Location> findLocationsByRange(String fromLocation, String toLocation) {
		// TODO Auto-generated method stub
		return locationRepository.findLocationsByRange(fromLocation,toLocation);
	}
}
