package com.css.one.pm.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class HouseUnit {

	//Liegenschaft
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
 	private Long id;
	
	private Long propertyManagementId;
	
	private String streetName;
	private int postalCode;
	private String houseNumbers;
	private String city;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public Long getPropertyManagementId() {
		return propertyManagementId;
	}
	public void setPropertyManagementId(Long propertyManagementId) {
		this.propertyManagementId = propertyManagementId;
	}
	
	
	
}
