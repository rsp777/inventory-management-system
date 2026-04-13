package com.pawar.inventory.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pawar.inventory.api.dto.AsnReceiveRequest;
import com.pawar.inventory.exceptions.ASNNotFoundException;
import com.pawar.inventory.exceptions.LpnNotFoundException;
import com.pawar.inventory.model.ASN;
import com.pawar.inventory.service.ASNService;

import jakarta.inject.Inject;
import jakarta.persistence.NoResultException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/asn")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AsnResource {

	private static final Logger logger = LoggerFactory.getLogger(AsnResource.class);
	@Inject
	private ASNService asnService;

	@POST
	@Path("/receive")
	public Response receiveManualAsn(AsnReceiveRequest request) {
		logger.info("Payload : {}", request);
		if (request == null || request.getAsn() == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Invalid request payload").build();
		}

		ASN asn = request.getAsn();
		List<com.pawar.inventory.model.Lpn> lpns = asn.getLpns();
		try {
			String response = asnService.receiveAsn(asn, lpns);
			return Response.ok(response).build();
		} catch (ASNNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND).entity("ASN Not Found: " + e.getMessage()).build();
		} catch (LpnNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND).entity("Lpn Not Found: " + e.getMessage()).build();
		}
	}

	@GET
	@Path("/list/by-name/{asn_name}")
	public Response getAsnByName(@PathParam("asn_name") String asnName) {
		try {
			ASN asn = asnService.getASNByName(asnName);
			return Response.ok(asn).build();
		} catch (NoResultException | ASNNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}

	@GET
	@Path("/list/category/{category}")
	public Response getAsnByCategory(@PathParam("category") String category) {
		try {
			List<ASN> asn = asnService.getAsnByCategory(category);
			return Response.ok(asn).build();
		} catch (NoResultException e) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
}
