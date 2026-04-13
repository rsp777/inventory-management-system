package com.pawar.inventory.api;

import java.io.IOException;

import org.apache.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pawar.inventory.api.dto.InventoryMovePayload;
import com.pawar.inventory.api.dto.InventoryMoveRequest;
import com.pawar.inventory.model.Location;
import com.pawar.inventory.model.Lpn;
import com.pawar.inventory.service.InventoryService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/inventory")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class InventoryResource {

	private static final Logger logger = LoggerFactory.getLogger(InventoryResource.class);
	@Inject
	private InventoryService inventoryService;

	@POST
	@Path("/createReserve")
	public Response createReserveInventory(InventoryMoveRequest request) {
		InventoryMovePayload payload = request.getInventory();
		Lpn lpn = payload.getLpn();
		Location location = payload.getLocation();
		try {
			String response = inventoryService.createReserveInventory(lpn, location);
			return Response.ok(response).build();
		} catch (ParseException | IOException e) {
			logger.error("Error in createReserveInventory", e);
			return Response.serverError().entity("An error occurred: " + e.getMessage()).build();
		}
	}

	@POST
	@Path("/createActive")
	public Response createActiveInventory(InventoryMoveRequest request) {
		InventoryMovePayload payload = request.getInventory();
		Lpn lpn = payload.getLpn();
		Location location = payload.getLocation();
		try {
			String response = inventoryService.createActiveInventory(lpn, location);
			return Response.ok(response).build();
		} catch (IOException e) {
			logger.error("Error in createActiveInventory", e);
			return Response.serverError().entity("An error occurred: " + e.getMessage()).build();
		}
	}
}
