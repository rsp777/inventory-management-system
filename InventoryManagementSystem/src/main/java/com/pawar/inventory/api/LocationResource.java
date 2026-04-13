package com.pawar.inventory.api;

import java.util.logging.Logger;

import com.pawar.inventory.api.dto.LocationRequest;
import com.pawar.inventory.model.Location;
import com.pawar.inventory.service.LocationService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/locations")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LocationResource {

	private static final Logger logger = Logger.getLogger(LocationResource.class.getName());
	@Inject
	private LocationService locationService;

	@POST
	@Path("/add")
	public Response createLocation(LocationRequest request) {
		logger.info("Payload : " + request);
		if (request == null || request.getLocation() == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Invalid location payload").build();
		}

		Location location = request.getLocation();
		locationService.createLocation(location);
		return Response.ok("Location Added Successfully : " + location.getLocn_brcd()).build();
	}

	@GET
	@Path("/list/by-range")
	public Iterable<Location> findLocationsByRange(@QueryParam("fromLocation") String fromLocation,
			@QueryParam("toLocation") String toLocation) {
		logger.info("From Location: " + fromLocation);
		logger.info("To Location: " + toLocation);
		return locationService.findLocationsByRange(fromLocation, toLocation);
	}

	@GET
	@Path("/list")
	public Iterable<Location> getFindAllLocations() {
		return locationService.getfindAlllocations();
	}

	@GET
	@Path("/list/by-id/{locn_id}")
	public Location findLocationById(@PathParam("locn_id") int locationId) {
		return locationService.findLocationById(locationId);
	}

	@GET
	@Path("/list/by-name/{locn_brcd}")
	public Location findLocationByBarcode(@PathParam("locn_brcd") String locationBarcode) {
		return locationService.findLocationByBarcode(locationBarcode);
	}

	@PUT
	@Path("/update/by-id/{locn_id}")
	public Response updateLocationById(@PathParam("locn_id") int locationId, Location location) {
		location = locationService.updateLocationByLocationId(locationId, location);
		return Response.ok("Location Edited Successfully : " + location.getLocn_brcd()).build();
	}

	@PUT
	@Path("/update/by-name/{locn_brcd}")
	public Response updateLocationByBarcode(@PathParam("locn_brcd") String locationBarcode, Location location) {
		location = locationService.updateLocationByLocationBarcode(locationBarcode, location);
		return Response.ok("Location Edited Successfully : " + location.getLocn_brcd()).build();
	}

	@DELETE
	@Path("/delete/by-id/{locn_id}")
	public Location deleteLocationById(@PathParam("locn_id") int locationId) {
		return locationService.deleteLocationByLocationId(locationId);
	}

	@DELETE
	@Path("/delete/by-name/{locn_brcd}")
	public Location deleteLocationByBarcode(@PathParam("locn_brcd") String locationBarcode) {
		return locationService.deleteLocationByLocationBarcode(locationBarcode);
	}
}
