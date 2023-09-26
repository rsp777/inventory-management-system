package com.pawar.inventory.model;

public class Item {

	private int item_id;
	private String item_name;
	private double unit_length;
	private double unit_width;
	private double unit_height;
	private double unit_volume;
	private String description;
	private String category;
	private String created_dttm;
	private String last_updated_dttm;
	private String created_source;
	private String last_updated_source;

	public Item() {
	}

	public Item(int item_id, String item_name, double unit_length, double unit_width, double unit_height,
			double unit_volume, String description, String category, String created_dttm, String last_updated_dttm,
			String created_source, String last_updated_source) {
		super();
		this.item_id = item_id;
		this.item_name = item_name;
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

	public int getItem_id() {
		return item_id;
	}

	public void setItem_id(int item_id) {
		this.item_id = item_id;
	}

	public String getItem_name() {
		return item_name;
	}

	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}

	public double getUnit_length() {
		return unit_length;
	}

	public void setUnit_length(double unit_length) {
		this.unit_length = unit_length;
	}

	public double getUnit_width() {
		return unit_width;
	}

	public void setUnit_width(double unit_width) {
		this.unit_width = unit_width;
	}

	public double getUnit_height() {
		return unit_height;
	}

	public void setUnit_height(double unit_height) {
		this.unit_height = unit_height;
	}

	public double getUnit_volume() {
		return unit_volume;
	}

	public void setUnit_volume(double unit_volume) {
		this.unit_volume = unit_volume;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCreated_dttm() {
		return created_dttm;
	}

	public void setCreated_dttm(String created_dttm) {
		this.created_dttm = created_dttm;
	}

	public String getLast_updated_dttm() {
		return last_updated_dttm;
	}

	public void setLast_updated_dttm(String last_updated_dttm) {
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
		return "Item [item_id=" + item_id + ", item_name=" + item_name + ", unit_length=" + unit_length
				+ ", unit_width=" + unit_width + ", unit_height=" + unit_height + ", unit_volume=" + unit_volume
				+ ", description=" + description + ", category=" + category + ", created_dttm=" + created_dttm
				+ ", last_updated_dttm=" + last_updated_dttm + ", created_source=" + created_source
				+ ", last_updated_source=" + last_updated_source + "]";
	}

}
