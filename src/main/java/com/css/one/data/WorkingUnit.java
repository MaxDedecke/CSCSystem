package com.css.one.data;

import java.time.LocalDate;

import jakarta.persistence.Entity;

@Entity
public class WorkingUnit extends AbstractEntity {
	
	private Long personId;
	private String personName;
    private String category;
    private int workingHours;
    private LocalDate dateBegin;
    private LocalDate dateEnd;
    
    private String note;
    private int associationId;
    
    public void setPersonId(Long personId) {
    	this.personId = personId;
    }
    public void setPersonName(String personName) {
    	this.personName = personName;
    }
    public void setWorkingHours(int workingHour) {
    	this.workingHours = workingHour;
    }
    public void setNote(String note) {
    	this.note = note;
    }
    public void setCategory(String kategorie) {
    	this.category = kategorie;
    }
    public void setBegin(LocalDate date) {
    	this.dateBegin = date;
    }
    public void setEnd(LocalDate end) {
    	this.dateEnd = end;
    }
    public Long getPersonId() {
    	return personId;
    }
    public String getPersonName() {
    	return personName;
    }
    public int getWorkingHours() {
    	return workingHours;
    }
    public String getNote() {
    	return note;
    }
    public String getCategory() {
    	return category;
    }
	public LocalDate getBegin() {
		return dateBegin;
	}
	public LocalDate getEnd() {
		return dateEnd;
	}
	public int getAssociationId() {
		return associationId;
	}
	public void setAssociationId(int associationId) {
		this.associationId = associationId;
	}

}
