package com.pawar.inventory.api;

import java.util.logging.Logger;

import com.pawar.inventory.model.Category;
import com.pawar.inventory.service.CategoryService;

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

@Path("/category")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CategoryResource {

	private static final Logger logger = Logger.getLogger(CategoryResource.class.getName());
	@Inject
	private CategoryService categoryService;

	@POST
	@Path("/add")
	public Response createCategory(Category category) {
		logger.info("Category : " + category);
		Category newCategory = categoryService.createCategory(category);
		return Response.ok("Category Added Successfully : " + newCategory.getCategory_name()).build();
	}

	@GET
	@Path("/list")
	public Iterable<Category> getCategories() {
		return categoryService.getfindAllCategories();
	}

	@GET
	@Path("/list/by-name/{category_name}")
	public Category getCategoryByName(@PathParam("category_name") String categoryName) {
		return categoryService.getCategoryByName(categoryName);
	}

	@GET
	@Path("/list/by-id/{category_id}")
	public Category getCategoryById(@PathParam("category_id") int categoryId) {
		return categoryService.getCategoryById(categoryId);
	}

	@PUT
	@Path("/update/by-id/{category_id}")
	public Category updateCategoryById(@PathParam("category_id") int categoryId, Category category) {
		logger.info("Update this category : " + category);
		return categoryService.updateCategoryByCategoryId(categoryId, category);
	}

	@PUT
	@Path("/update/by-name/{category_name}")
	public Response updateCategoryByName(@PathParam("category_name") String categoryName, Category category) {
		logger.info("Update this category : " + category);
		categoryService.updateCategoryByCategoryName(categoryName, category);
		return Response.ok("Category Updated Successfully").build();
	}

	@DELETE
	@Path("/delete/by-id/{category_id}")
	public Category deleteCategoryById(@PathParam("category_id") int categoryId) {
		return categoryService.deleteCategoryByCategoryId(categoryId);
	}

	@DELETE
	@Path("/delete/by-name/{category_name}")
	public Category deleteCategoryByName(@PathParam("category_name") String categoryName) {
		return categoryService.deleteCategoryByCategoryName(categoryName);
	}
}
