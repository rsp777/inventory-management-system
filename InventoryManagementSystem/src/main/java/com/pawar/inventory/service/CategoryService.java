package com.pawar.inventory.service;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pawar.inventory.exceptions.CategoryNotFoundException;
import com.pawar.inventory.model.Category;
import com.pawar.inventory.repository.category.CategoryRepository;

@Service
public class CategoryService {

	private final static Logger logger = Logger.getLogger(CategoryService.class.getName());

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	public CategoryService(CategoryRepository categoryRepository) {

		this.categoryRepository = categoryRepository;

		// TODO Auto-generated constructor stub
	}

	@Transactional
	public Category createCategory(Category category) {

		Category newCategory = new Category();
		newCategory.setCategory_name(category.getCategory_name());
		return categoryRepository.addCategory(newCategory);

	}

	@Transactional
	public Iterable<Category> getfindAllCategories() {
		return categoryRepository.getfindAllCategories();
	}

	@Transactional
	public Category getCategoryByName(String category_name) {
		// TODO Auto-generated method stub
		return categoryRepository.getCategoryByName(category_name);
	}

	@Transactional
	public Category getCategoryById(int category_id) {
		// TODO Auto-generated method stub
		return categoryRepository.getCategoryById(category_id);
	}

	@Transactional
	public Category updateCategoryByCategoryId(int category_id, Category category) {
		// TODO Auto-generated method stub
		return categoryRepository.updateCategoryByCategoryId(category_id, category);
	}

	@Transactional
	public Category updateCategoryByCategoryName(String category_name, Category category) {
		// TODO Auto-generated method stub
		return categoryRepository.updateCategoryByCategoryName(category_name, category);
	}

	@Transactional
	public Category deleteCategoryByCategoryId(int category_id) {
		// TODO Auto-generated method stub
		return categoryRepository.deleteCategoryByCategoryId(category_id);
	}

	@Transactional
	public Category deleteCategoryByCategoryName(String category_name) {
		// TODO Auto-generated method stub
		return categoryRepository.deleteCategoryByCategoryName(category_name);
	}

	public boolean validateCategory(Category category) throws CategoryNotFoundException{

		logger.info("Category to validate : " + category.getCategory_name());
		logger.info("category.getCategory_name() == null "+(category.getCategory_name() == null));
		if (category.getCategory_name() != null) {
			return true;
		} else {
			return false;
		}

	}

}
