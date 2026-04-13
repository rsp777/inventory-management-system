package com.pawar.inventory.api;

import java.util.logging.Logger;

import com.pawar.inventory.api.dto.ItemRequest;
import com.pawar.inventory.exceptions.CategoryNotFoundException;
import com.pawar.inventory.exceptions.ItemNotFoundException;
import com.pawar.inventory.model.Category;
import com.pawar.inventory.model.Item;
import com.pawar.inventory.service.CategoryService;
import com.pawar.inventory.service.ItemService;

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

@Path("/items")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ItemResource {

	private static final Logger logger = Logger.getLogger(ItemResource.class.getName());
	@Inject
	private ItemService itemService;
	@Inject
	private CategoryService categoryService;

	@POST
	@Path("/add")
	public Response addItem(ItemRequest request) {
		logger.info("Payload : " + request);
		if (request == null || request.getItem() == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Invalid item payload").build();
		}

		Item item = request.getItem();
		Category category = item.getCategory();
		try {
			boolean isCategoryPresent = categoryService.validateCategory(category);
			if (isCategoryPresent) {
				itemService.addItem(item, category);
				return Response.ok("Item Added Successfully : " + item.getDescription()).build();
			}
		} catch (CategoryNotFoundException e) {
			logger.warning("Category validation failed: " + e.getMessage());
		}
		return Response.status(Response.Status.NOT_FOUND).build();
	}

	@GET
	@Path("/list")
	public Iterable<Item> getItems() {
		return itemService.getfindAllItems();
	}

	@GET
	@Path("/list/by-id/{itemId}")
	public Item findItemById(@PathParam("itemId") int itemId) {
		return itemService.findItemById(itemId);
	}

	@GET
	@Path("/list/by-desc/{itemDesc}")
	public Response findItemByDesc(@PathParam("itemDesc") String itemDesc) {
		try {
			Item item = itemService.findItemByDesc(itemDesc);
			return Response.ok(item).build();
		} catch (ItemNotFoundException | CategoryNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}

	@GET
	@Path("/list/by-name/{itemName}")
	public Response findItemByName(@PathParam("itemName") String itemName) {
		try {
			Item item = itemService.findItemByName(itemName);
			return Response.ok(item).build();
		} catch (ItemNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}

	@PUT
	@Path("/update/by-id/{item_id}")
	public Item updateItemByItemId(@PathParam("item_id") int itemId, Item item) {
		logger.info("Update this item : " + item);
		return itemService.updateItemByItemId(itemId, item);
	}

	@PUT
	@Path("/update")
	public Response updateItemByItemName(ItemRequest request) {
		if (request == null || request.getItem() == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Invalid item payload").build();
		}
		Item item = request.getItem();
		try {
			item = itemService.updateItemByItemName(item);
			return Response.ok("Item Edited Successfully : " + item.getDescription()).build();
		} catch (ItemNotFoundException | CategoryNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}

	@DELETE
	@Path("/delete/by-id/{itemId}")
	public Item deleteItemByItemId(@PathParam("itemId") int itemId) {
		return itemService.deleteItemByItemId(itemId);
	}

	@DELETE
	@Path("/delete/by-name/{itemName}")
	public Response deleteItemByItemName(@PathParam("itemName") String itemName) {
		try {
			Item item = itemService.deleteItemByItemName(itemName);
			return Response.ok(item).build();
		} catch (ItemNotFoundException | CategoryNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
}
