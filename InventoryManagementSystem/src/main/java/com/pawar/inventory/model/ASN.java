package com.pawar.inventory.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.relational.core.mapping.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pawar.inventory.entity.ASNDto;
import com.pawar.inventory.entity.LpnDto;
import com.pawar.inventory.exceptions.CategoryNotFoundException;
import com.pawar.inventory.exceptions.ItemNotFoundException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
@Table(name = "asn")
public class ASN {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "asn_id")
	@JsonProperty("id")
	private Integer id;

	@Column(name = "asn_brcd")
	private String asnBrcd;
	
	@Column(name = "total_quantity")
	private int totalQuantity;
	
	@JsonManagedReference
	@OneToMany(mappedBy = "asn")
	private List<Lpn> lpns;

	@Column(name = "asn_status")
	private int asnStatus;

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

	public ASN() {
	}

	public ASN(int id, String asnBrcd,int totalQuantity, List<Lpn> lpns, int asnStatus, LocalDateTime created_dttm,
			LocalDateTime last_updated_dttm, String created_source, String last_updated_source) {
		super();
		this.id = id;
		this.asnBrcd = asnBrcd;
		this.totalQuantity = totalQuantity;
		this.lpns = lpns;
		this.asnStatus = asnStatus;
		this.created_dttm = created_dttm;
		this.last_updated_dttm = last_updated_dttm;
		this.created_source = created_source;
		this.last_updated_source = last_updated_source;
	}

	public ASN(ASNDto asnDto) throws ItemNotFoundException, CategoryNotFoundException {
//		this.id = asnDto.getId();
		this.asnBrcd = asnDto.getAsnBrcd();
		this.lpns = convertLpnDtoToEntity(asnDto.getLpns());
		this.totalQuantity = asnDto.getTotalQuantity();
		this.created_dttm = asnDto.getCreated_dttm();
		this.last_updated_dttm = asnDto.getLast_updated_dttm();
		this.created_source = asnDto.getCreated_source();
		this.last_updated_source = asnDto.getLast_updated_source();
	}

	public List<Lpn> convertLpnDtoToEntity(List<LpnDto> lpnDtos)
			throws ItemNotFoundException, CategoryNotFoundException {
		List<Lpn> lpns = new ArrayList<>();

		for (LpnDto lpnDto : lpnDtos) {
			Lpn lpn = new Lpn(lpnDto);
			lpns.add(lpn);
		}
		return lpns;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAsnBrcd() {
		return asnBrcd;
	}

	public void setAsnBrcd(String asnBrcd) {
		this.asnBrcd = asnBrcd;
	}
	
	public int getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(int totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	public List<Lpn> getLpns() {
		return lpns;
	}

	public void setLpns(List<Lpn> lpns) {
		this.lpns = lpns;
	}

	public int getAsnStatus() {
		return asnStatus;
	}

	public void setAsnStatus(int asnStatus) {
		this.asnStatus = asnStatus;
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
		return "ASN [id=" + id + ", asnBrcd=" + asnBrcd + ", totalQuantity=" + totalQuantity + ", lpns=" + lpns
				+ ", asnStatus=" + asnStatus + ", created_dttm=" + created_dttm + ", last_updated_dttm="
				+ last_updated_dttm + ", created_source=" + created_source + ", last_updated_source="
				+ last_updated_source + "]";
	}

}
