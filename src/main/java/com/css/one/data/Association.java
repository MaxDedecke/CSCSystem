package com.css.one.data;

import java.time.LocalDate;
import java.util.List;

import io.micrometer.common.lang.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Association {
	
	 	@Id
	    @GeneratedValue(strategy = GenerationType.AUTO)
	 	private Long id;

		private String name;
	 	private long number;
	 	private String city;
	    private String street;
	    private String streetNumber;
	    private int postalCode;
	    private double amountMemberSubscription;
	    private LocalDate registrationDate;
	    
	    @Nullable
	    private String statutePath;
	    
		@OneToMany
	    private List<Location> locations;
	    
		
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
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
		public double getAmountMemberSubscription() {
			return amountMemberSubscription;
		}
		public void setAmountMemberSubscription(double amountMemberSubscription) {
			this.amountMemberSubscription = amountMemberSubscription;
		}
		public LocalDate getRegistrationDate() {
			return registrationDate;
		}
		public void setRegistrationDate(LocalDate registrationDate) {
			this.registrationDate = registrationDate;
		}

		public String getStatutePath() {
			return statutePath;
		}

		public void setStatutePath(String statutePath) {
			this.statutePath = statutePath;
		}
}
