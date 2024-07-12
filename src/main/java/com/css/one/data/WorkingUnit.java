package com.css.one.data;

import java.time.LocalDate;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class WorkingUnit extends AbstractEntity {
	
	private Long personId;
	private String personName;
	@ManyToOne
    private WorkingUnitCategory category;
    private int workingHours;
    private LocalDate dateBegin;
    @Nullable
    private LocalDate dateEnd;
    
    private int hourBegin;
    private int minuteBegin;
    
    @Nullable
    private int hourEnd;
    @Nullable
    private int minuteEnd;
    @Nullable
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
    public void setCategory(WorkingUnitCategory kategorie) {
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
    public WorkingUnitCategory getCategory() {
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
	public int getMinuteBegin() {
		return minuteBegin;
	}
	public void setMinuteBegin(int minuteBegin) {
		this.minuteBegin = minuteBegin;
	}
	public int getHourBegin() {
		return hourBegin;
	}
	public void setHourBegin(int hourBegin) {
		this.hourBegin = hourBegin;
	}
	public int getHourEnd() {
		return hourEnd;
	}
	public void setHourEnd(int hourEnd) {
		this.hourEnd = hourEnd;
	}
	public int getMinuteEnd() {
		return minuteEnd;
	}
	public void setMinuteEnd(int minuteEnd) {
		this.minuteEnd = minuteEnd;
	}
}
