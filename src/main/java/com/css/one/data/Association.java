package com.css.one.data;

import jakarta.persistence.Entity;

@Entity
public class Association extends AbstractEntity {
	
	 	private String name;
	 	private long number;
	 	private String city;
	    private String street;
	    private String streetNumber;
	    private int postalCode;
	    
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public long getNumber() {
			return number;
		}
		public void setNumber(int number) {
			this.number = number;
		}
		public String getStreet() {
			return street;
		}
		public void setStreet(String street) {
			this.street = street;
		}
		public String getCity() {
			return city;
		}
		public void setCity(String city) {
			this.city = city;
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

}
