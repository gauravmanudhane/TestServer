package com.ethoca.customer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CUSTOMER")
public class Customer {
	@Id
	private long id;

	@Column(name = "NAME")
	private String name;

	@Column(name = "SECTOR")
	private String sector;

	@Column(name = "LOC")
	private String location;

	public Customer() {
	}

	public Customer(long id, String name, String sector, String location) {
		this.id = id;
		this.name = name;
		this.sector = sector;
		this.location = location;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@Override
	public String toString() {
		return id + "," + name + "," + sector + "," + location + "\n";
	}

}
