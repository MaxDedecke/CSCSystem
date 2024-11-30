package com.css.one.data;

import java.time.LocalDate;

import com.css.one.data.enums.OutputType;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;

@Entity
public class Output extends AbstractEntity {
	
	private LocalDate date;
    private int associationId;
    private int entityId;
    private int personId;
    private double amount;
    private boolean outdated;
    
    private OutputType type;
    
    @Nullable
    private String note;
    
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
	public int getEntityId() {
		return entityId;
	}
	public void setEntityId(int strainId) {
		this.entityId = strainId;
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
	public OutputType getType() {
		return type;
	}
	public void setType(OutputType type) {
		this.type = type;
	}

}
