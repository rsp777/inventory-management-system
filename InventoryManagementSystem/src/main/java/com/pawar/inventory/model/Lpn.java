package com.pawar.inventory.model;

public class Lpn {

	private int lpn_id;
	private String lpn_name;
	private String item_name;
	private int quantity;
	private double length;
	private double width;
	private double height;
	private double weight;
	private double volume;
	private String created_dttm;
	private String last_updated_dttm;
	private String created_source;
	private String last_updated_source;

	public Lpn() {
		// TODO Auto-generated constructor stub
	}

	public Lpn(int lpn_id, String lpn_name, String item_name, int quantity, double length, double width, double height,
			double weight, double volume, String created_dttm, String last_updated_dttm, String created_source,
			String last_updated_source) {
		super();
		this.lpn_id = lpn_id;
		this.lpn_name = lpn_name;
		this.item_name = item_name;
		this.quantity = quantity;
		this.length = length;
		this.width = width;
		this.height = height;
		this.weight = weight;
		this.volume = volume;
		this.created_dttm = created_dttm;
		this.last_updated_dttm = last_updated_dttm;
		this.created_source = created_source;
		this.last_updated_source = last_updated_source;
	}

	public int getLpn_id() {
		return lpn_id;
	}

	public void setLpn_id(int lpn_id) {
		this.lpn_id = lpn_id;
	}

	public String getLpn_name() {
		return lpn_name;
	}

	public void setLpn_name(String lpn_name) {
		this.lpn_name = lpn_name;
	}

	public String getItem_name() {
		return item_name;
	}

	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
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
		return "Lpn [lpn_id=" + lpn_id + ", lpn_name=" + lpn_name + ", item_name=" + item_name + ", quantity="
				+ quantity + ", length=" + length + ", width=" + width + ", height=" + height + ", weight=" + weight
				+ ", volume=" + volume + ", created_dttm=" + created_dttm + ", last_updated_dttm=" + last_updated_dttm
				+ ", created_source=" + created_source + ", last_updated_source=" + last_updated_source + "]";
	}

}
