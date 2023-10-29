package com.pawar.inventory.model;

import java.time.LocalDateTime;

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
@Table(name = "inventory")
public class Inventory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int inventory_id;
	
	@ManyToOne
	@JoinColumn(name = "lpn_id")
	private Lpn lpn;
	
	@ManyToOne
	@JoinColumn(name = "item_id")
	private Item item;
	private double on_hand_qty;
	
	@ManyToOne
	@JoinColumn(name = "locn_id")
	private Location location;
	
	@Column(name = "locn_class")
	private String locn_class;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
	@JsonProperty("created_dttm")
	@Column(name = "created_dttm")
	private LocalDateTime created_dttm;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
	@JsonProperty("last_updated_dttm")
	@Column(name = "last_updated_dttm")
	private LocalDateTime last_updated_dttm;
	
	@Column(name = "created_source")
	private String created_source;
	
	@Column(name = "last_updated_source")
	private String last_updated_source;

	public Inventory() {
		// TODO Auto-generated constructor stub
	}

	public Inventory(int inventory_id, Lpn lpn, Item item, double on_hand_qty, Location location,
			String locn_class, LocalDateTime created_dttm, LocalDateTime last_updated_dttm, String created_source,
			String last_updated_source) {
		this.inventory_id = inventory_id;
		this.lpn = lpn;
		this.item = item;
		this.on_hand_qty = on_hand_qty;
		this.location = location;
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

	public Lpn getLpn() {
		return lpn;
	}

	public void setLpn(Lpn lpn) {
		this.lpn = lpn;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public double getOn_hand_qty() {
		return on_hand_qty;
	}

	public void setOn_hand_qty(double on_hand_qty) {
		this.on_hand_qty = on_hand_qty;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getLocn_class() {
		return locn_class;
	}

	public void setLocn_class(String locn_class) {
		this.locn_class = locn_class;
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
		return "Inventory [inventory_id=" + inventory_id + ", lpn=" + lpn + ", item=" + item
				+ ", created_dttm=" + created_dttm + ", last_updated_dttm=" + last_updated_dttm + ", created_source="
				+ created_source + ", last_updated_source=" + last_updated_source + "]";
	}
	
	

	
}
