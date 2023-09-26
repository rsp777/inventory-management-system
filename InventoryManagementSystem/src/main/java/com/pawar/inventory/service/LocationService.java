package com.pawar.inventory.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.pawar.inventory.model.Location;

public class LocationService {

	private final static Logger logger = Logger.getLogger(ItemService.class.getName());
	List<Location> locations;
//	Location location;

	public LocationService() {
		this.locations = new ArrayList<Location>();
//		this.location = new Location();
	}

	public void createLocation(String locn_brcd, String locn_class, double length, double width, double height,
			double maxWeight, double maxVolume, double maxQty, double curr_vol, double curr_weight) {

		if (locn_brcd == null || locn_class == null) {
			logger.info("Location barcode and class cannot be null.");
			return;
		}

		if (length <= 0 || width <= 0 || height <= 0 || maxVolume <= 0 || maxWeight <= 0) {
			logger.info("Location dimensions, volume and weight must be greater than zero.");
			return;
		}

		int locn_id = (int) (Math.random() * 1000);
		while (getLocationById(locn_id) != null) {
			locn_id = (int) (Math.random() * 1000);
		}

		Location location = new Location();
		location.setLocn_id(locn_id);
		location.setLocn_brcd(locn_brcd);
		location.setLocn_class(locn_class);
		location.setLength(length);
		location.setWidth(width);
		location.setHeight(height);
		location.setMax_weight(curr_weight);
		location.setMax_volume(maxVolume);
		location.setMax_qty(locn_id);
		location.setCurr_vol(curr_vol);
		location.setCurr_weight(curr_weight);
		location.setCreated_dttm(LocalTime.now().toString());
		location.setLast_updated_dttm(LocalTime.now().toString());
		location.setCreated_source(System.getProperty("user.name"));
		location.setLast_updated_source(System.getProperty("user.name"));

		locations.add(location);

		logger.info("Location successfully created : " + location);
	}

	private Location getLocationById(int id) {
		for (Location location : locations) {
			if (location.getLocn_id() == id) {
				return location;
			}
		}
		return null;
	}

	public Location viewLocation(String locn_brcd) {
//		logger.info("Location List :" + locations);
		for (Location location : locations) {
			if (location.getLocn_brcd() == locn_brcd) {
				return location;
			}
		}
		return null;
	}

	public void deleteLocation(int id) {
		locations.removeIf(location -> location.getLocn_id() == id);
	}

	public String findLocationByBarcode(String locn_brcd) {

		if (locn_brcd != null) {
			for (Location location : locations) {
//				logger.info("Locationss : " + locn_brcd);
				if (location.getLocn_brcd().equals(locn_brcd)) {
					logger.info("Location found : " + location.getLocn_brcd());
					return locn_brcd;
				}
			}
		}
		return null;
	}

	public void updateLocationProps(String locn_brcd,String locn_class, double lpnQuantity, double lpnUnitVolume) {

		if (lpnQuantity != 0 && lpnUnitVolume != 0) {
			
			Location location = viewLocation(locn_brcd);
			location.setLocn_class(locn_class);
			location.setOccupied_qty(lpnQuantity);
			location.setCurr_vol(lpnUnitVolume);
			
		}	

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

	@Override
	public String toString() {
		return "LocationService [locations=" + locations + "]";
	}

}
