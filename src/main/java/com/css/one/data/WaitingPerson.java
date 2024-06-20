package com.css.one.data;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Email;

@Entity
public class WaitingPerson extends AbstractEntity {
	
	  private String firstName;
	    private String lastName;
	    @Email
	    private String email;
	    private String phone;
	    private LocalDate dateOfBirth;
	    private int associationId;
	    private LocalDate dateOfRegistration;
	    
	    public String getFirstName() {
	        return firstName;
	    }
	    public void setFirstName(String firstName) {
	        this.firstName = firstName;
	    }
	    public String getLastName() {
	        return lastName;
	    }
	    public void setLastName(String lastName) {
	        this.lastName = lastName;
	    }
	    public String getEmail() {
	        return email;
	    }
	    public void setEmail(String email) {
	        this.email = email;
	    }
	    public String getPhone() {
	        return phone;
	    }
	    public void setPhone(String phone) {
	        this.phone = phone;
	    }
	    public LocalDate getDateOfBirth() {
	        return dateOfBirth;
	    }
	    public void setDateOfBirth(LocalDate dateOfBirth) {
	        this.dateOfBirth = dateOfBirth;
	    }
		public int getAssociationId() {
			return associationId;
		}
		public void setAssociationId(int associationId) {
			this.associationId = associationId;
		}
		public LocalDate getDateOfRegistration() {
			return dateOfRegistration;
		}
		public void setDateOfRegistration(LocalDate dateOfRegistration) {
			this.dateOfRegistration = dateOfRegistration;
		}
}
