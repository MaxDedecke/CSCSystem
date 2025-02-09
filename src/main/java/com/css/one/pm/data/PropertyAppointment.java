package com.css.one.pm.data;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class PropertyAppointment {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
 	private Long id;

	private Long propertyManagementId;

	private LocalDateTime dateOfAppointment;
	private String note;
	
	private List<HouseParty> invitedPeople;

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

	public LocalDateTime getDateOfAppointment() {
		return dateOfAppointment;
	}

	public void setDateOfAppointment(LocalDateTime dateOfAppointment) {
		this.dateOfAppointment = dateOfAppointment;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public List<HouseParty> getInvitedPeople() {
		return invitedPeople;
	}

	public void setInvitedPeople(List<HouseParty> invitedPeople) {
		this.invitedPeople = invitedPeople;
	}	
}
