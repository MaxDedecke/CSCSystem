package com.css.one.data;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class MemberData {
	
 	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
 	
    private String streetName;
    private String streetNumber;
    private int postalCode;
	private String cityName;
    private LocalDate dateOfRegistration;

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
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public LocalDate getDateOfRegistration() {
		return dateOfRegistration;
	}
	public void setDateOfRegistration(LocalDate dateOfRegistration) {
		this.dateOfRegistration = dateOfRegistration;
	}
}
