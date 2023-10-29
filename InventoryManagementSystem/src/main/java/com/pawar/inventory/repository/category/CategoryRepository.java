package com.pawar.inventory.repository.category;


import com.pawar.inventory.model.Category;

//@Repository
public interface CategoryRepository {//extends CrudRepository<Category, Integer>{
	
	public Category addCategory(Category category);
	public Iterable<Category> getfindAllCategories();
	public Category getCategoryByName(String category_name);
	public Category getCategoryById(int category_id);
	public Category updateCategoryByCategoryId(int category_id, Category category);
	public Category updateCategoryByCategoryName(String category_name, Category category);
	public Category deleteCategoryByCategoryId(int category_id);
	public Category deleteCategoryByCategoryName(String category_name);
	
}
