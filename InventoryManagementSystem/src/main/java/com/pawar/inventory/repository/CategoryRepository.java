package com.pawar.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.pawar.inventory.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer>{

}
