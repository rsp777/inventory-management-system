package com.pawar.inventory.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pawar.inventory.exceptions.GrpAlreadyExistsException;
import com.pawar.inventory.model.Grp;
import com.pawar.inventory.service.GrpService;

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

@Path("/grp")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GrpResource {

	private static final Logger logger = LoggerFactory.getLogger(GrpResource.class);
	@Inject
	private GrpService grpService;

	@POST
	@Path("/add")
	public Response createGrp(Grp grp) {
		logger.info("Grp : {}", grp.getGrpDesc());
		try {
			Grp newGrp = grpService.createGrp(grp);
			return Response.ok("Grp Added Successfully : " + newGrp.getGrpName()).build();
		} catch (GrpAlreadyExistsException e) {
			return Response.status(Response.Status.CONFLICT)
					.entity("Grp Already Exists: " + grp.getGrpDesc())
					.build();
		}
	}

	@GET
	@Path("/list")
	public Iterable<Grp> getGrps() {
		return grpService.getfindAllGrps();
	}

	@GET
	@Path("/list/by-name/{grp_name}")
	public Grp getGrpByName(@PathParam("grp_name") String grpName) {
		return grpService.getGrpByName(grpName);
	}

	@GET
	@Path("/list/by-id/{grp_id}")
	public Grp getGrpById(@PathParam("grp_id") int grpId) {
		return grpService.getGrpById(grpId);
	}

	@PUT
	@Path("/update/by-id/{grp_id}")
	public Grp updateGrpById(@PathParam("grp_id") int grpId, Grp grp) {
		logger.info("Update this grp : {}", grp);
		return grpService.updateGrpById(grpId, grp);
	}

	@PUT
	@Path("/update/by-name/{grp_name}")
	public Response updateGrpByName(@PathParam("grp_name") String grpName, Grp grp) {
		logger.info("Update this grp : {}", grp);
		grpService.updateGrpByName(grpName, grp);
		return Response.ok("Grp Updated Successfully").build();
	}

	@DELETE
	@Path("/delete/by-id/{grp_id}")
	public Grp deleteGrpById(@PathParam("grp_id") int grpId) {
		return grpService.deleteGrpById(grpId);
	}

	@DELETE
	@Path("/delete/by-name/{grp_name}")
	public Grp deleteGrpByName(@PathParam("grp_name") String grpName) {
		return grpService.deleteGrpByName(grpName);
	}
}
