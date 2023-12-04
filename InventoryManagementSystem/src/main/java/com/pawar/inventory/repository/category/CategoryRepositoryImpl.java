package com.pawar.inventory.repository.category;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.pawar.inventory.model.Category;
import com.pawar.inventory.model.Inventory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository {

	private EntityManager entityManager;
	private final Logger logger = Logger.getLogger(CategoryRepositoryImpl.class.getName());

	public CategoryRepositoryImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public Category addCategory(Category category) {

		Session currentSession = entityManager.unwrap(Session.class);
		Query<Category> query = currentSession.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1", Category.class);
		query.executeUpdate();
		currentSession.saveOrUpdate(category);
		logger.info("Category added to databasee : " + category);
		return category;
	}

	@Override
	public Iterable<Category> getfindAllCategories() {
		Session currentSession = entityManager.unwrap(Session.class);
		Query<Category> query = currentSession.createQuery("from Category", Category.class);
		logger.info("Query : " + query.toString());

		List<Category> listCategories = query.getResultList();
		for (Iterator<Category> iterator = listCategories.iterator(); iterator.hasNext();) {
			Category category = (Category) iterator.next();
			logger.info("Category Data : " + category);
		}
		return listCategories;
	}

	@Override
	public Category getCategoryByName(String category_name) {
	    Session currentSession = entityManager.unwrap(Session.class);
	    Query<Category> query = currentSession.createQuery("from Category where categoryName = :categoryName", Category.class);
	    query.setParameter("categoryName", category_name);

	    try {
	        return query.getSingleResult();
	    } catch (NoResultException e) {
	        // Handle the exception here
	        return null;
	    }
	}

	@Override
	public Category getCategoryById(int category_id) {
		Session currentSession = entityManager.unwrap(Session.class);
	    Query<Category> query = currentSession.createQuery("from Category where categoryId = :categoryId", Category.class);
	    query.setParameter("categoryId", category_id);

	    try {
	        return query.getSingleResult();
	    } catch (NoResultException e) {
	        // Handle the exception here
	        return null;
	    }
	}

	@Override
	public Category updateCategoryByCategoryId(int category_id,Category category) {
		Session currentSession = entityManager.unwrap(Session.class);
		Category existingCategory = getCategoryById(category_id);
		existingCategory.setCategory_name(category.getCategory_name());
		currentSession.update(existingCategory);
		logger.info("Category updated : " + existingCategory);
		return existingCategory;
	}

	@Override
	public Category updateCategoryByCategoryName(String category_name,Category category) {
		Session currentSession = entityManager.unwrap(Session.class);
		Category existingcategory = getCategoryByName(category_name);
		existingcategory.setCategory_name(category.getCategory_name());
		currentSession.update(existingcategory);
		logger.info("Category updated : " + existingcategory);
		return existingcategory;
	}

	@Override
	public Category deleteCategoryByCategoryId(int category_id) {
		Category category = getCategoryById(category_id);
		logger.info("Category to delete for : " + category);
		Session currentSession = entityManager.unwrap(Session.class);
		currentSession.delete(category);
		return category;
	}

	@Override
	public Category deleteCategoryByCategoryName(String category_name) {
		Category category = getCategoryByName(category_name);
		logger.info("Category to delete for : " + category);
		Session currentSession = entityManager.unwrap(Session.class);
		currentSession.delete(category);
		return category;
	}

}
