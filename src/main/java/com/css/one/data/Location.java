package com.css.one.data;


import jakarta.persistence.Entity;

@Entity
public class Location extends AbstractEntity {
	
	private String name;
 	private String city;
    private String street;
    private String streetNumber;
    private int postalCode;
 	private int associationId;
 	private String note;
 	
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getStreetNumber() {
		return streetNumber;
	}
	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
	}
	public int getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(int postalCode) {
		this.postalCode = postalCode;
	}
	public int getAssociationId() {
		return associationId;
	}
	public void setAssociationId(int associationId) {
		this.associationId = associationId;
	}
}
