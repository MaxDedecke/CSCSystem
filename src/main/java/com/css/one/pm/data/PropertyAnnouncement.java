package com.css.one.pm.data;

import java.time.LocalDate;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

@Entity
public class PropertyAnnouncement {

	//Digitaler Aushang
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
 	private Long id;
	
	@Lob
	private String text;
	
	@Nullable
	private LocalDate dateFrom;
	
	@Nullable
	private LocalDate dateUntil;
	
	private Long propertyManagementId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public LocalDate getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(LocalDate dateFrom) {
		this.dateFrom = dateFrom;
	}

	public LocalDate getDateUntil() {
		return dateUntil;
	}

	public void setDateUntil(LocalDate dateUntil) {
		this.dateUntil = dateUntil;
	}

	public Long getPropertyManagementId() {
		return propertyManagementId;
	}

	public void setPropertyManagementId(Long propertyManagementId) {
		this.propertyManagementId = propertyManagementId;
	}
}
