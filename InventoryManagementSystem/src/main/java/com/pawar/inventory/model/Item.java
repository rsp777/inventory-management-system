package com.pawar.inventory.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.relational.core.mapping.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@Table(name = "item")
public class Item {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int item_id;

	@Column(name = "item_name")
	private String itemName;

	@Column(name = "unit_length")
	private float unit_length;

	@Column(name = "unit_width")
	private float unit_width;

	@Column(name = "unit_height")
	private float unit_height;

	@Column(name = "unit_volume")
	private float unit_volume;

	@Column(name = "description")
	private String description;

	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
	@JsonProperty("created_dttm")
	@Column(name = "created_dttm")
//	@JdbcTypeCode (SqlTypes.DATE)
	private LocalDateTime created_dttm;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
	@JsonProperty("last_updated_dttm")
	@Column(name = "last_updated_dttm")
//	@JdbcTypeCode (SqlTypes.DATE)
	private LocalDateTime last_updated_dttm;

	@Column(name = "created_source")
	private String created_source;

	@Column(name = "last_updated_source")
	private String last_updated_source;

	public Item() {
	}

	public Item(int item_id, String itemName, float unit_length, float unit_width, float unit_height,
			float unit_volume, String description, Category category, LocalDateTime created_dttm,
			LocalDateTime last_updated_dttm, String created_source, String last_updated_source) {
		super();
		this.item_id = item_id;
		this.itemName = itemName;
		this.unit_length = unit_length;
		this.unit_width = unit_width;
		this.unit_height = unit_height;
		this.unit_volume = unit_volume;
		this.description = description;
		this.category = category;
		this.created_dttm = created_dttm;
		this.last_updated_dttm = last_updated_dttm;
		this.created_source = created_source;
		this.last_updated_source = last_updated_source;
	}

	public Item(String itemName) {
		this.itemName = itemName;
	}

	public int getItem_id() {
		return item_id;
	}

	public void setItem_id(int item_id) {
		this.item_id = item_id;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public float getUnit_length() {
		return unit_length;
	}

	public void setUnit_length(float unit_length) {
		this.unit_length = unit_length;
	}

	public float getUnit_width() {
		return unit_width;
	}

	public void setUnit_width(float unit_width) {
		this.unit_width = unit_width;
	}

	public float getUnit_height() {
		return unit_height;
	}

	public void setUnit_height(float unit_height) {
		this.unit_height = unit_height;
	}

	public float getUnit_volume() {
		return unit_volume;
	}

	public void setUnit_volume(float unit_volume) {
		this.unit_volume = unit_volume;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public LocalDateTime getCreated_dttm() {
		return created_dttm;
	}

	public void setCreated_dttm(LocalDateTime created_dttm) {
		this.created_dttm = created_dttm;
	}

	public LocalDateTime getLast_updated_dttm() {
		return last_updated_dttm;
	}

	public void setLast_updated_dttm(LocalDateTime last_updated_dttm) {
		this.last_updated_dttm = last_updated_dttm;
	}

	public String getCreated_source() {
		return created_source;
	}

	public void setCreated_source(String created_source) {
		this.created_source = created_source;
	}

	public String getLast_updated_source() {
		return last_updated_source;
	}

	public void setLast_updated_source(String last_updated_source) {
		this.last_updated_source = last_updated_source;
	}

	@Override
	public String toString() {
		return "Item [item_id=" + item_id + ", itemName=" + itemName + ", unit_length=" + unit_length + ", unit_width="
				+ unit_width + ", unit_height=" + unit_height + ", unit_volume=" + unit_volume + ", description="
				+ description + ", category=" + category + ", created_dttm=" + created_dttm + ", last_updated_dttm="
				+ last_updated_dttm + ", created_source=" + created_source + ", last_updated_source="
				+ last_updated_source + "]";
	}

	
}
