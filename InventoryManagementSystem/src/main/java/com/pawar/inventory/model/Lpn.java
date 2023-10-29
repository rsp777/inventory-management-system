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
import jakarta.persistence.OneToMany;

@Entity
@Table(name = "lpn")
public class Lpn {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int lpn_id;
	
	@Column(name = "lpn_name")
	private String lpn_name;
	
	@ManyToOne
	@JoinColumn(name = "item_id")
	private Item item;
	
	@Column(name = "quantity")
	private int quantity;
	
	@Column(name = "length")
	private double length;
	
	@Column(name = "width")
	private double width;
	
	@Column(name = "height")
	private double height;
	
	@Column(name = "weight")
	private double weight;
	
	@Column(name = "volume")
	private double volume;
	
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

	public Lpn() {
		// TODO Auto-generated constructor stub
	}

	public Lpn(int lpn_id, String lpn_name, Item item, int quantity, double length, double width, double height,
			double weight, double volume, LocalDateTime created_dttm, LocalDateTime last_updated_dttm, String created_source,
			String last_updated_source) {
		super();
		this.lpn_id = lpn_id;
		this.lpn_name = lpn_name;
		this.item = item;
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
	
	
	

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
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
		return "Lpn [lpn_id=" + lpn_id + ", lpn_name=" + lpn_name + ", item=" + item + ", quantity="
				+ quantity + ", length=" + length + ", width=" + width + ", height=" + height + ", weight=" + weight
				+ ", volume=" + volume + ", created_dttm=" + created_dttm + ", last_updated_dttm=" + last_updated_dttm
				+ ", created_source=" + created_source + ", last_updated_source=" + last_updated_source + "]";
	}

}
