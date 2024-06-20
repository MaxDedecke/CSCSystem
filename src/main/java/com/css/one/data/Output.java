package com.css.one.data;

import java.time.LocalDate;

import jakarta.persistence.Entity;

@Entity
public class Output extends AbstractEntity {
	
	private LocalDate date;
 	private String note;
    private int associationId;
    private int strainId;
    private int personId;
    private double amount;
    private boolean outdated;
    
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public int getAssociationId() {
		return associationId;
	}
	public void setAssociationId(int associationId) {
		this.associationId = associationId;
	}
	public int getStrainId() {
		return strainId;
	}
	public void setStrainId(int strainId) {
		this.strainId = strainId;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public int getPersonId() {
		return personId;
	}
	public void setPersonId(int personId) {
		this.personId = personId;
	}
	public boolean isOutdated() {
		return outdated;
	}
	public void setOutdated(boolean outdated) {
		this.outdated = outdated;
	}

}
