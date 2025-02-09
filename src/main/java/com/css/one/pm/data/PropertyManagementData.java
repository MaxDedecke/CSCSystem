package com.css.one.pm.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class PropertyManagementData {
	
	//same as data of association
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
 	private Long id;
	
	private Long propertyManagementId;
	
	private String streetName;
	private int postalCode;
	private String houseNumbers;
	private String city;
	
	private String email;
	private String phoneNumber;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getPropertyManagementId() {
		return propertyManagementId;
	}
	public void setPropertyManagementId(Long propertyManagementId) {
		this.propertyManagementId = propertyManagementId;
	}
	public String getStreetName() {
		return streetName;
	}
	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}
	public int getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(int postalCode) {
		this.postalCode = postalCode;
	}
	public String getHouseNumbers() {
		return houseNumbers;
	}
	public void setHouseNumbers(String houseNumbers) {
		this.houseNumbers = houseNumbers;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}	
}
