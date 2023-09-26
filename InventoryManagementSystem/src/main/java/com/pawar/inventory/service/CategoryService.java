package com.pawar.inventory.service;


import java.util.List;
import java.util.logging.Logger;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import com.pawar.inventory.model.Category;
import com.pawar.inventory.repository.CategoryRepository;

@Service
public class CategoryService {

	private final static Logger logger = Logger.getLogger(CategoryService.class.getName());
//	List<Category> categories;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	public CategoryService(CategoryRepository categoryRepository) {

		this.categoryRepository = categoryRepository;

		// TODO Auto-generated constructor stub
	}
	
	public Category createCategory(Category category) {
		
		    Category newCategory = new Category();
		    newCategory.setCategory_name(category.getCategory_name());
			return categoryRepository.save(newCategory);
	
	}
	
	public Iterable<Category> getfindAllCategories(){
		return categoryRepository.findAll();
	}


}
