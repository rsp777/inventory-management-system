package com.pawar.inventory.model;

public class Inventory {

	private int inventory_id;
	private String lpn;
	private String item_name;
	private double on_hand_qty;
	private String locn_brcd;
	private String locn_class;
	private String created_dttm;
	private String last_updated_dttm;
	private String created_source;
	private String last_updated_source;

	public Inventory() {
		// TODO Auto-generated constructor stub
	}

	public Inventory(int inventory_id, String lpn, String item_name, double on_hand_qty, String locn_brcd,
			String locn_class, String created_dttm, String last_updated_dttm, String created_source,
			String last_updated_source) {
		this.inventory_id = inventory_id;
		this.lpn = lpn;
		this.item_name = item_name;
		this.on_hand_qty = on_hand_qty;
		this.locn_brcd = locn_brcd;
		this.locn_class = locn_class;
		this.created_dttm = created_dttm;
		this.last_updated_dttm = last_updated_dttm;
		this.created_source = created_source;
		this.last_updated_source = last_updated_source;
	}

	public int getInventory_id() {
		return inventory_id;
	}

	public void setInventory_id(int inventory_id) {
		this.inventory_id = inventory_id;
	}

	public String getLpn() {
		return lpn;
	}

	public void setLpn(String lpn) {
		this.lpn = lpn;
	}

	public String getItem_name() {
		return item_name;
	}

	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}

	public double getOn_hand_qty() {
		return on_hand_qty;
	}

	public void setOn_hand_qty(double on_hand_qty) {
		this.on_hand_qty = on_hand_qty;
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
		return "Inventory [inventory_id=" + inventory_id + ", lpn=" + lpn + ", item_name=" + item_name
				+ ", on_hand_qty=" + on_hand_qty + ", locn_brcd=" + locn_brcd + ", locn_class=" + locn_class
				+ ", created_dttm=" + created_dttm + ", last_updated_dttm=" + last_updated_dttm + ", created_source="
				+ created_source + ", last_updated_source=" + last_updated_source + "]";
	}
	
	

	
}
