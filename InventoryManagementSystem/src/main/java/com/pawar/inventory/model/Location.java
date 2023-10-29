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

@Entity
@Table(name = "location")
public class Location {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int locn_id;
	
	@Column(name = "locn_brcd")
	private String locnBrcd;
	
	@Column(name = "locn_class")
	private String locnClass;
	
	@Column(name = "length")
	private double length;
	
	@Column(name = "width")
	private double width;
	
	@Column(name = "height")
	private double height;
	
	@Column(name = "max_weight")
	private double maxWeight;
	
	@Column(name = "max_volume")
	private double maxVolume;
	
	@Column(name = "max_qty")
	private double maxQty;
	
	@Column(name = "occupied_qty")
	private double occupiedQty;
	
	@Column(name = "curr_vol")
	private double currVol;
	
	@Column(name = "curr_weight")
	private double currWeight;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    @JsonProperty("created_dttm")
	@Column(name = "created_dttm")
	private LocalDateTime createdDttm;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    @JsonProperty("created_dttm")
	@Column(name = "last_updated_dttm")
	private LocalDateTime lastUpdatedDttm;
	
	@Column(name = "created_source")
	private String createdSource;
	
	@Column(name = "last_updated_source")
	private String lastUpdatedSource;

	public Location() {
		// TODO Auto-generated constructor stub
	}

	public Location(int locn_id, String locnBrcd, String locnClass, double length, double width, double height,
			double maxWeight, double maxVolume, double maxQty,double occupiedQty, double currVol, double currWeight,
			LocalDateTime createdDttm, LocalDateTime lastUpdatedDttm, String createdSource, String lastUpdatedSource) {
		super();
		this.locn_id = locn_id;
		this.locnBrcd = locnBrcd;
		this.locnClass = locnClass;
		this.length = length;
		this.width = width;
		this.height = height;
		this.maxWeight = maxWeight;
		this.maxVolume = maxVolume;
		this.maxQty = maxQty;
		this.occupiedQty = occupiedQty;
		this.currVol = currVol;
		this.currWeight = currWeight;
		this.createdDttm = createdDttm;
		this.lastUpdatedDttm = lastUpdatedDttm;
		this.createdSource = createdSource;
		this.lastUpdatedSource = lastUpdatedSource;
	}

	public int getLocn_id() {
		return locn_id;
	}

	public void setLocn_id(int locn_id) {
		this.locn_id = locn_id;
	}

	public String getLocn_brcd() {
		return locnBrcd;
	}

	public void setLocn_brcd(String locnBrcd) {
		this.locnBrcd = locnBrcd;
	}

	public String getLocn_class() {
		return locnClass;
	}

	public void setLocn_class(String locnClass) {
		this.locnClass = locnClass;
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
		return maxWeight;
	}

	public void setMax_weight(double maxWeight) {
		this.maxWeight = maxWeight;
	}

	public double getMax_volume() {
		return maxVolume;
	}

	public void setMax_volume(double maxVolume) {
		this.maxVolume = maxVolume;
	}

	public double getMax_qty() {
		return maxQty;
	}

	public void setMax_qty(double maxQty) {
		this.maxQty = maxQty;
	}

	public double getOccupied_qty() {
		return occupiedQty;
	}

	public void setOccupied_qty(double occupiedQty) {
		this.occupiedQty = occupiedQty;
	}
	public double getCurr_vol() {
		return currVol;
	}

	public void setCurr_vol(double currVol) {
		this.currVol = currVol;
	}

	public double getCurr_weight() {
		return currWeight;
	}

	public void setCurr_weight(double currWeight) {
		this.currWeight = currWeight;
	}

	public LocalDateTime getCreated_dttm() {
		return createdDttm;
	}

	public void setCreated_dttm(LocalDateTime createdDttm) {
		this.createdDttm = createdDttm;
	}

	public LocalDateTime getLast_updated_dttm() {
		return lastUpdatedDttm;
	}

	public void setLast_updated_dttm(LocalDateTime lastUpdatedDttm) {
		this.lastUpdatedDttm = lastUpdatedDttm;
	}

	public String getCreated_source() {
		return createdSource;
	}

	public void setCreated_source(String createdSource) {
		this.createdSource = createdSource;
	}

	public String getLast_updated_source() {
		return lastUpdatedSource;
	}

	public void setLast_updated_source(String lastUpdatedSource) {
		this.lastUpdatedSource = lastUpdatedSource;
	}

	@Override
	public String toString() {
		return "Location [locn_id=" + locn_id + ", locnBrcd=" + locnBrcd + ", locnClass=" + locnClass + ", length="
				+ length + ", width=" + width + ", height=" + height + ", maxWeight=" + maxWeight + ", maxVolume="
				+ maxVolume + ", maxQty=" + maxQty + ", occupiedQty=" + occupiedQty + ", currVol=" + currVol
				+ ", currWeight=" + currWeight + ", createdDttm=" + createdDttm + ", lastUpdatedDttm="
				+ lastUpdatedDttm + ", createdSource=" + createdSource + ", lastUpdatedSource="
				+ lastUpdatedSource + "]";
	}

	


}
