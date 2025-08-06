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
@Table(name = "grp")
public class Grp {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
    @JsonProperty("grp_name")
	@Column(name = "grp_name")
	private String grpName;
	
    @JsonProperty("grp_desc")
	@Column(name = "grp_desc")
	private String grpDesc;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    @JsonProperty("created_dttm")
	@Column(name = "created_dttm")
	private LocalDateTime createdDttm;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    @JsonProperty("last_updated_dttm")
	@Column(name = "last_updated_dttm")
	private LocalDateTime lastUpdatedDttm;
	
    @JsonProperty("created_source")
	@Column(name = "created_source")
	private String createdSource;
	
    @JsonProperty("last_updated_source")
	@Column(name = "last_updated_source")
	private String lastUpdatedSource;

	public Grp() {
	}
	
	public Grp(int id, String grpName,String grpDesc, LocalDateTime createdDttm, LocalDateTime lastUpdatedDttm, String createdSource,
			String lastUpdatedSource) {
		super();
		this.id = id;
		this.grpName = grpName;
		this.grpDesc = grpDesc;
		this.createdDttm = createdDttm;
		this.lastUpdatedDttm = lastUpdatedDttm;
		this.createdSource = createdSource;
		this.lastUpdatedSource = lastUpdatedSource;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getGrpName() {
		return grpName;
	}

	public void setGrpName(String grpName) {
		this.grpName = grpName;
	}
	
	public String getGrpDesc() {
		return grpDesc;
	}

	public void setGrpDesc(String grpDesc) {
		this.grpDesc = grpDesc;
	}

	public LocalDateTime getCreatedDttm() {
		return createdDttm;
	}

	public void setCreatedDttm(LocalDateTime createdDttm) {
		this.createdDttm = createdDttm;
	}

	public LocalDateTime getLastUpdatedDttm() {
		return lastUpdatedDttm;
	}

	public void setLastUpdatedDttm(LocalDateTime lastUpdatedDttm) {
		this.lastUpdatedDttm = lastUpdatedDttm;
	}

	public String getCreatedSource() {
		return createdSource;
	}

	public void setCreatedSource(String createdSource) {
		this.createdSource = createdSource;
	}

	public String getLastUpdatedSource() {
		return lastUpdatedSource;
	}

	public void setLastUpdatedSource(String lastUpdatedSource) {
		this.lastUpdatedSource = lastUpdatedSource;
	}

	@Override
	public String toString() {
		return "Grp [id=" + id + ", grpName=" + grpName + ", grpDesc=" + grpDesc + ", createdDttm=" + createdDttm
				+ ", lastUpdatedDttm=" + lastUpdatedDttm + ", createdSource=" + createdSource + ", lastUpdatedSource="
				+ lastUpdatedSource + "]";
	}
}
