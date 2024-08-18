package com.pawar.inventory.model;

import java.time.LocalDateTime;

import org.springframework.data.relational.core.mapping.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pawar.inventory.entity.LpnDto;
import com.pawar.inventory.exceptions.CategoryNotFoundException;
import com.pawar.inventory.exceptions.ItemNotFoundException;
import com.pawar.inventory.repository.item.ItemRepository;
import com.pawar.inventory.service.ItemService;

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
	@JsonProperty("lpn_id")
	private int lpn_id;

	@JsonProperty("lpn_name")
	@Column(name = "lpn_name")
	private String lpn_name;

	@ManyToOne
	@JoinColumn(name = "item_id")
	private Item item;

	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "asn_id")
	private ASN asn;

	@Column(name = "quantity")
	private int quantity;

	@Column(name = "length")
	private float length;

	@Column(name = "width")
	private float width;

	@Column(name = "height")
	private float height;

	@Column(name = "weight")
	private float weight;

	@Column(name = "volume")
	private float volume;

	@Column(name = "lpn_facility_status")
	private int lpn_facility_status;

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

	}

	public Lpn(int id, String lpn_name, Item item, ASN asn, int quantity, float length, float width, float height,
			float weight, float volume, int lpn_facility_status, LocalDateTime created_dttm,
			LocalDateTime last_updated_dttm, String created_source, String last_updated_source) {
		super();
		this.lpn_id = id;
		this.lpn_name = lpn_name;
		this.item = item;
		this.asn = asn;
		this.quantity = quantity;
		this.length = length;
		this.width = width;
		this.height = height;
		this.weight = weight;
		this.volume = volume;
		this.lpn_facility_status = lpn_facility_status;
		this.created_dttm = created_dttm;
		this.last_updated_dttm = last_updated_dttm;
		this.created_source = created_source;
		this.last_updated_source = last_updated_source;
	}

	public Lpn(int id, String lpn_name, Item item, int quantity, float length, float width, float height, float weight,
			float volume, int lpn_facility_status, LocalDateTime created_dttm, LocalDateTime last_updated_dttm,
			String created_source, String last_updated_source) {
		super();
		this.lpn_id = id;
		this.lpn_name = lpn_name;
		this.item = item;
		this.quantity = quantity;
		this.length = length;
		this.width = width;
		this.height = height;
		this.weight = weight;
		this.volume = volume;
		this.lpn_facility_status = lpn_facility_status;
		this.created_dttm = created_dttm;
		this.last_updated_dttm = last_updated_dttm;
		this.created_source = created_source;
		this.last_updated_source = last_updated_source;
	}

	public Item getItem(String itemName) throws ItemNotFoundException, CategoryNotFoundException {
		System.out.println(itemName);
		Item item = new Item(itemName);
		return item;
	}

	public Lpn(LpnDto lpnDto) throws ItemNotFoundException, CategoryNotFoundException {
//		this.lpn_id = lpnDto.getId();
		this.lpn_name = lpnDto.getLpnNumber();
		this.item = getItem(lpnDto.getItemName());
		this.quantity = lpnDto.getQuantity();
		this.created_dttm = lpnDto.getCreated_dttm();
		this.last_updated_dttm = lpnDto.getLast_updated_dttm();
		this.created_source = lpnDto.getCreated_source();
		this.last_updated_source = lpnDto.getLast_updated_source();
	}

	public int getLpn_id() {
		return lpn_id;
	}

	public void setLpn_id(int id) {
		this.lpn_id = id;
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

	public ASN getAsn() {
		return asn;
	}

	public void setAsn(ASN asn) {
		this.asn = asn;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
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

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public float getVolume() {
		return volume;
	}

	public void setVolume(float volume) {
		this.volume = volume;
	}

	public int getLpn_facility_status() {
		return lpn_facility_status;
	}

	public void setLpn_facility_status(int lpn_facility_status) {
		this.lpn_facility_status = lpn_facility_status;
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
		return "Lpn [id=" + lpn_id + ", lpn_name=" + lpn_name + ", item=" + item + ", quantity=" + quantity
				+ ", length=" + length + ", width=" + width + ", height=" + height + ", weight=" + weight + ", volume="
				+ volume + ", lpn_facility_status=" + lpn_facility_status + ", created_dttm=" + created_dttm
				+ ", last_updated_dttm=" + last_updated_dttm + ", created_source=" + created_source
				+ ", last_updated_source=" + last_updated_source + "]";
	}
}
