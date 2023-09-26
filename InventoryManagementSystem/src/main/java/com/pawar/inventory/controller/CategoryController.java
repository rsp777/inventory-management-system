package com.pawar.inventory.controller;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pawar.inventory.model.Category;
import com.pawar.inventory.service.CategoryService;

@RestController
@RequestMapping("/categories")
@EnableJpaRepositories
public class CategoryController {
	
	private final static Logger logger = Logger.getLogger(CategoryController.class.getName());

	@Autowired
	public CategoryService categoryService;
	
	@PostMapping("/add")
	public ResponseEntity<?> createCategory(@RequestBody Category category){
		logger.info("Category : "+category);
		Category newCategory = categoryService.createCategory(category);
		logger.info("New Category is now created : "+newCategory);
		return ResponseEntity.ok("Category Added Successfully : "+newCategory);
		
	}
	
	@GetMapping("/list")
	public Iterable<Category> getCategories(){
		Iterable<Category> categories = categoryService.getfindAllCategories();
		return categories;
	}
	
}
