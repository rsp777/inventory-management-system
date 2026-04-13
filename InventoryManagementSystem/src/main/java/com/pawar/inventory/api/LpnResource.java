package com.pawar.inventory.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pawar.inventory.api.dto.LpnRequest;
import com.pawar.inventory.exceptions.CategoryNotFoundException;
import com.pawar.inventory.exceptions.ItemNotFoundException;
import com.pawar.inventory.exceptions.LpnNotFoundException;
import com.pawar.inventory.model.Item;
import com.pawar.inventory.model.Lpn;
import com.pawar.inventory.service.LpnService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/lpns")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LpnResource {

	private static final Logger logger = LoggerFactory.getLogger(LpnResource.class);
	@Inject
	private LpnService lpnService;

	@POST
	@Path("/create")
	public Response createLpn(LpnRequest request) {
		logger.info("Payload : {}", request);
		if (request == null || request.getLpn() == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Invalid lpn payload").build();
		}

		Lpn lpn = request.getLpn();
		Item item = lpn.getItem();
		try {
			Lpn created = lpnService.createLpn(lpn, item);
			return Response.ok(created).build();
		} catch (LpnNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND).entity("LPN Not Found: " + e.getMessage()).build();
		} catch (ItemNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND).entity("Item Not Found: " + e.getMessage()).build();
		} catch (CategoryNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND).entity("Category Not Found: " + e.getMessage()).build();
		}
	}

	@GET
	@Path("/list")
	public Iterable<Lpn> getfindAllLpns() {
		return lpnService.getfindAllLpns();
	}

	@GET
	@Path("/list/by-name/{lpn_name}")
	public Response getLpnByName(@PathParam("lpn_name") String lpnName) {
		try {
			Lpn lpn = lpnService.getLpnByName(lpnName);
			return Response.ok(lpn).build();
		} catch (LpnNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}

	@GET
	@Path("/list/by-id/{lpn_id}")
	public Lpn findLpnById(@PathParam("lpn_id") int lpnId) {
		return lpnService.findLpnById(lpnId);
	}

	@GET
	@Path("/list/category/{category}")
	public Response findLpnByCategory(@PathParam("category") String category) {
		try {
			List<Lpn> lpn = lpnService.findLpnByCategory(category);
			return Response.ok(lpn).build();
		} catch (LpnNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}

	@PUT
	@Path("/update/by-id/{lpn_id}")
	public Lpn updateLpnByLpnId(@PathParam("lpn_id") int lpnId, Lpn lpn) {
		try {
			return lpnService.updateLpnByLpnId(lpnId, lpn);
		} catch (ItemNotFoundException | CategoryNotFoundException e) {
			return lpn;
		}
	}

	@PUT
	@Path("/update/by-name/{lpn_name}/{adjustQty}")
	public Response updateLpnByLpnBarcode(@PathParam("lpn_name") String lpnName, Lpn lpn,
			@PathParam("adjustQty") int adjustQty) {
		try {
			Lpn updatedLpn = lpnService.updateLpnByLpnBarcode(lpnName, lpn, adjustQty);
			return Response.ok("Lpn updated successfully :" + updatedLpn).build();
		} catch (ItemNotFoundException | CategoryNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND).build();
		} catch (LpnNotFoundException e) {
			return Response.serverError().build();
		}
	}

	@DELETE
	@Path("/delete/by-id/{lpn_id}")
	public Lpn deleteLpnByLpnId(@PathParam("lpn_id") int lpnId) {
		return lpnService.deleteLpnByLpnId(lpnId);
	}

	@DELETE
	@Path("/delete/by-name/{lpn_name}")
	public Response deleteLpnByLpnBarcode(@PathParam("lpn_name") String lpnName) {
		try {
			lpnService.deleteLpnByLpnBarcode(lpnName);
			return Response.status(Response.Status.NO_CONTENT).build();
		} catch (LpnNotFoundException e) {
			return Response.serverError().build();
		}
	}
}
