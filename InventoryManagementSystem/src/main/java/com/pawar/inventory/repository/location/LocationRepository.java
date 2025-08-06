package com.pawar.inventory.repository.location;

import com.pawar.inventory.model.Location;

public interface LocationRepository {
	
	public Location addLocation(Location location);
	public Iterable<Location> getfindAlllocations();
	public Location findLocationByBarcode(String location_name);
	public Location findLocationById(int locn_id);
	public Location updateLocationByLocationId(int locn_id, Location location);
	public Location updateLocationByLocationBarcode(String locn_brcd, Location location);
	public Location deleteLocationByLocationId(int locn_id);
	public Location deleteLocationByLocationBarcode(String locn_brcd);
	public Location updateOccupiedQty(Location location,int adjustQty);
	public Iterable<Location> findLocationsByRange(String fromLocation, String toLocation);
	
}
