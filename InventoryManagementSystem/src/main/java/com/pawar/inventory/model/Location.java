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
	
	@Column(name = "grp")
	private String grp;
	
	@Column(name = "locn_class")
	private String locnClass;
	
	@Column(name = "length")
	private float length;
	
	@Column(name = "width")
	private float width;
	
	@Column(name = "height")
	private float height;
	
	@Column(name = "max_weight")
	private float maxWeight;
	
	@Column(name = "max_volume")
	private float maxVolume;
	
	@Column(name = "max_qty")
	private float maxQty;
	
	@Column(name = "occupied_qty")
	private float occupiedQty;
	
	@Column(name = "curr_vol")
	private float currVol;
	
	@Column(name = "curr_weight")
	private float currWeight;
	
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

	public Location(int locn_id, String locnBrcd,String grp, String locnClass, float length, float width, float height,
			float maxWeight, float maxVolume, float maxQty,float occupiedQty, float currVol, float currWeight,
			LocalDateTime createdDttm, LocalDateTime lastUpdatedDttm, String createdSource, String lastUpdatedSource) {
		super();
		this.locn_id = locn_id;
		this.locnBrcd = locnBrcd;
		this.grp = grp;
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

	public Location(String locnBrcd) {
		this.locnBrcd = locnBrcd;
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
	
	
	public String getGrp() {
		return grp;
	}

	public void setGrp(String grp) {
		this.grp = grp;
	}

	public String getLocn_class() {
		return locnClass;
	}

	public void setLocn_class(String locnClass) {
		this.locnClass = locnClass;
	}

	public float getLength() {
		return length;
	}

	public void setLength(float length) {
		this.length = length;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getMax_weight() {
		return maxWeight;
	}

	public void setMax_weight(float maxWeight) {
		this.maxWeight = maxWeight;
	}

	public float getMax_volume() {
		return maxVolume;
	}

	public void setMax_volume(float maxVolume) {
		this.maxVolume = maxVolume;
	}

	public float getMax_qty() {
		return maxQty;
	}

	public void setMax_qty(float maxQty) {
		this.maxQty = maxQty;
	}

	public float getOccupied_qty() {
		return occupiedQty;
	}

	public void setOccupied_qty(float occupiedQty) {
		this.occupiedQty = occupiedQty;
	}
	public float getCurr_vol() {
		return currVol;
	}

	public void setCurr_vol(float currVol) {
		this.currVol = currVol;
	}

	public float getCurr_weight() {
		return currWeight;
	}

	public void setCurr_weight(float currWeight) {
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
		return "Location [locn_id=" + locn_id + ", locnBrcd=" + locnBrcd + ", grp=" + grp + ", locnClass=" + locnClass
				+ ", length=" + length + ", width=" + width + ", height=" + height + ", maxWeight=" + maxWeight
				+ ", maxVolume=" + maxVolume + ", maxQty=" + maxQty + ", occupiedQty=" + occupiedQty + ", currVol="
				+ currVol + ", currWeight=" + currWeight + ", createdDttm=" + createdDttm + ", lastUpdatedDttm="
				+ lastUpdatedDttm + ", createdSource=" + createdSource + ", lastUpdatedSource=" + lastUpdatedSource
				+ "]";
	}
}
