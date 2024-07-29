package com.pawar.inventory.controller;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pawar.inventory.model.Category;
import com.pawar.inventory.service.CategoryService;

@RestController
@RequestMapping("/category")
//@EnableJpaRepositories
@EnableJdbcRepositories
public class CategoryController {
	
	private final static Logger logger = Logger.getLogger(CategoryController.class.getName());


	public CategoryService categoryService;
	
	 @Autowired
	    public CategoryController(CategoryService categoryService) {
	        this.categoryService = categoryService;
	    }
	
	
	@PostMapping("/add")
	public ResponseEntity<?> createCategory(@RequestBody Category category){
		logger.info("Category : "+category);
		Category newCategory = categoryService.createCategory(category);
		logger.info("New Category is now created : "+newCategory);
		return ResponseEntity.ok("Category Added Successfully : "+newCategory.getCategory_name());
		
	}
	
	@GetMapping("/list")
	public Iterable<Category> getCategories(){
		Iterable<Category> categories = categoryService.getfindAllCategories();
		return categories;
	}
	
	@GetMapping("/list/by-name/{category_name}")
	public Category getCategoryByName(@PathVariable  String category_name){
		Category category = categoryService.getCategoryByName(category_name);
		return category;
	}
	
	@GetMapping("/list/by-id/{category_id}")
	public Category getCategoryByCategoryId(@PathVariable int category_id){
		Category category = categoryService.getCategoryById(category_id);
		return category;
	}
	
	@PutMapping("/update/by-id/{category_id}")
	public Category updateCategoryByCategoryId(@PathVariable int category_id,@RequestBody Category category){
		logger.info("Update this category : "+category);
		category = categoryService.updateCategoryByCategoryId(category_id,category);
		return category;
	}
	
	@PutMapping("/update/by-name/{category_name}")
	public ResponseEntity<?> updateCategoryByCategoryName(@PathVariable String category_name,@RequestBody Category category){
		logger.info("Update this category : "+category);
		category = categoryService.updateCategoryByCategoryName(category_name,category);
		return ResponseEntity.ok("Category Updated Successfully");
	}
	
	@DeleteMapping("/delete/by-id/{category_id}")
	public Category deleteCategoryByCategoryId(@PathVariable int category_id){
		Category category = categoryService.deleteCategoryByCategoryId(category_id);
		return category;
	}
	
	@DeleteMapping("/delete/by-name/{category_name}")
	public Category deleteCategoryByCategoryName(@PathVariable String category_name){
		Category category = categoryService.deleteCategoryByCategoryName(category_name);
		return category;
	}
	
}
