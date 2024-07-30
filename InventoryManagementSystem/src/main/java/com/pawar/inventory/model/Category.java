package com.pawar.inventory.model;

import jakarta.persistence.Column;
//import org.hibernate.annotations.Table;
//import org.springframework.data.annotation.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

//import jakarta.persistence.*;
import org.springframework.data.relational.core.mapping.Table;

//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;

@Entity
@Table(name = "category")
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer categoryId;

	@Column(name = "category_name")
	private String categoryName;

	public Category() {

	}

	public Category(Integer categoryId, String categoryName) {
		this.categoryId = categoryId;
		this.categoryName = categoryName;
	}

	public Integer getCategory_id() {
		return categoryId;
	}

	public void setCategory_id(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategory_name() {
		return categoryName;
	}

	public void setCategory_name(String categoryName) {
		this.categoryName = categoryName;
	}

	@Override
	public String toString() {
		return "Category [categoryId=" + categoryId + ", categoryName=" + categoryName + "]";
	}

}
