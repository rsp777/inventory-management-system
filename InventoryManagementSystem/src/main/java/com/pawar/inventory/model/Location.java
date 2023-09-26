package com.pawar.inventory.model;

public class Location {

	private int locn_id;
	private String locn_brcd;
	private String locn_class;
	private double length;
	private double width;
	private double height;
	private double max_weight;
	private double max_volume;
	private double max_qty;
	private double occupied_qty;
	private double curr_vol;
	private double curr_weight;
	private String created_dttm;
	private String last_updated_dttm;
	private String created_source;
	private String last_updated_source;

	public Location() {
		// TODO Auto-generated constructor stub
	}

	public Location(int locn_id, String locn_brcd, String locn_class, double length, double width, double height,
			double max_weight, double max_volume, double max_qty,double occupied_qty, double curr_vol, double curr_weight,
			String created_dttm, String last_updated_dttm, String created_source, String last_updated_source) {
		super();
		this.locn_id = locn_id;
		this.locn_brcd = locn_brcd;
		this.locn_class = locn_class;
		this.length = length;
		this.width = width;
		this.height = height;
		this.max_weight = max_weight;
		this.max_volume = max_volume;
		this.max_qty = max_qty;
		this.occupied_qty = occupied_qty;
		this.curr_vol = curr_vol;
		this.curr_weight = curr_weight;
		this.created_dttm = created_dttm;
		this.last_updated_dttm = last_updated_dttm;
		this.created_source = created_source;
		this.last_updated_source = last_updated_source;
	}

	public int getLocn_id() {
		return locn_id;
	}

	public void setLocn_id(int locn_id) {
		this.locn_id = locn_id;
	}

	public String getLocn_brcd() {
		return locn_brcd;
	}

	public void setLocn_brcd(String locn_brcd) {
		this.locn_brcd = locn_brcd;
	}

	public String getLocn_class() {
		return locn_class;
	}

	public void setLocn_class(String locn_class) {
		this.locn_class = locn_class;
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

	public double getMax_weight() {
		return max_weight;
	}

	public void setMax_weight(double max_weight) {
		this.max_weight = max_weight;
	}

	public double getMax_volume() {
		return max_volume;
	}

	public void setMax_volume(double max_volume) {
		this.max_volume = max_volume;
	}

	public double getMax_qty() {
		return max_qty;
	}

	public void setMax_qty(double max_qty) {
		this.max_qty = max_qty;
	}

	public double getOccupied_qty() {
		return occupied_qty;
	}

	public void setOccupied_qty(double occupied_qty) {
		this.occupied_qty = occupied_qty;
	}
	public double getCurr_vol() {
		return curr_vol;
	}

	public void setCurr_vol(double curr_vol) {
		this.curr_vol = curr_vol;
	}

	public double getCurr_weight() {
		return curr_weight;
	}

	public void setCurr_weight(double curr_weight) {
		this.curr_weight = curr_weight;
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
		return "Location [locn_id=" + locn_id + ", locn_brcd=" + locn_brcd + ", locn_class=" + locn_class + ", length="
				+ length + ", width=" + width + ", height=" + height + ", max_weight=" + max_weight + ", max_volume="
				+ max_volume + ", max_qty=" + max_qty + ", occupied_qty=" + occupied_qty + ", curr_vol=" + curr_vol
				+ ", curr_weight=" + curr_weight + ", created_dttm=" + created_dttm + ", last_updated_dttm="
				+ last_updated_dttm + ", created_source=" + created_source + ", last_updated_source="
				+ last_updated_source + "]";
	}

	


}
