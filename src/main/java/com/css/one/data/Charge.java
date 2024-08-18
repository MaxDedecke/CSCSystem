package com.css.one.data;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Charge implements EntityWrapper {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	String name;
	LocalDate dateOfExistense;
	
	private int associationId;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	List<Plant> plants;

	public List<Plant> getPlants() {
		return plants;
	}
	public void setPlants(List<Plant> plants) {
		this.plants = plants;
	}
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
	public LocalDate getDateOfExistense() {
		return dateOfExistense;
	}
	public void setDateOfExistense(LocalDate dateOfExistense) {
		this.dateOfExistense = dateOfExistense;
	}
	public int getAssociationId() {
		return associationId;
	}
	public void setAssociationId(int associationId) {
		this.associationId = associationId;
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
		return true;
	}
}
