package com.pawar.inventory.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pawar.inventory.model.Category;
import com.pawar.inventory.service.CategoryService;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> createCategory(@RequestBody Category category) {
        logger.info("Category : {}", category);
        Category newCategory = categoryService.createCategory(category);
        return ResponseEntity.ok("Category Added Successfully : " + newCategory.getCategory_name());
    }

    @GetMapping("/list")
    public ResponseEntity<Iterable<Category>> getCategories() {
        return ResponseEntity.ok(categoryService.getfindAllCategories());
    }

    @GetMapping("/list/by-name/{category_name}")
    public ResponseEntity<Category> getCategoryByName(@PathVariable("category_name") String categoryName) {
        return ResponseEntity.ok(categoryService.getCategoryByName(categoryName));
    }

    @GetMapping("/list/by-id/{category_id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable("category_id") int categoryId) {
        return ResponseEntity.ok(categoryService.getCategoryById(categoryId));
    }

    @PutMapping("/update/by-id/{category_id}")
    public ResponseEntity<Category> updateCategoryById(@PathVariable("category_id") int categoryId,
            @RequestBody Category category) {
        logger.info("Update this category : {}", category);
        return ResponseEntity.ok(categoryService.updateCategoryByCategoryId(categoryId, category));
    }

    @PutMapping("/update/by-name/{category_name}")
    public ResponseEntity<String> updateCategoryByName(@PathVariable("category_name") String categoryName,
            @RequestBody Category category) {
        logger.info("Update this category : {}", category);
        categoryService.updateCategoryByCategoryName(categoryName, category);
        return ResponseEntity.ok("Category Updated Successfully");
    }

    @DeleteMapping("/delete/by-id/{category_id}")
    public ResponseEntity<Category> deleteCategoryById(@PathVariable("category_id") int categoryId) {
        return ResponseEntity.ok(categoryService.deleteCategoryByCategoryId(categoryId));
    }

    @DeleteMapping("/delete/by-name/{category_name}")
    public ResponseEntity<Category> deleteCategoryByName(@PathVariable("category_name") String categoryName) {
        return ResponseEntity.ok(categoryService.deleteCategoryByCategoryName(categoryName));
    }
}
