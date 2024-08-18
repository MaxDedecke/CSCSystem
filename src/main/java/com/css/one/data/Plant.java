package com.css.one.data;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Plant implements EntityWrapper{
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private int associationId;
	
	String name;
	LocalDate dateOfExistense;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public int getAssociationId() {
		return associationId;
	}
	public void setAssociationId(int associationId) {
		this.associationId = associationId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public LocalDate getDateOfExistense() {
		return dateOfExistense;
	}
	public void setDateOfExistense(LocalDate dateOfExistense) {
		this.dateOfExistense = dateOfExistense;
	}

	@Override
	public LocalDate getErfasst() {
		return getDateOfExistense();
	}
	@Override
	public String getNummer() {
		return String.valueOf(getId());

	}
	@Override
	public boolean isCharge() {
		return false;
	}
}
