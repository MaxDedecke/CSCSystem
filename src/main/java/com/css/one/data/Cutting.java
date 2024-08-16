package com.css.one.data;

import java.time.LocalDate;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class Cutting extends OutputEntity {
	
	private int cuttingNumber;
 	private LocalDate datePlanted;
 	private GrowStatus status;
 	private int amountOfCuttings;
 	
 	@ManyToOne
 	private Person responsiblePerson;

	@ManyToOne
	@Nullable
 	private Blossom motherPlant;
 	
	@Nullable
	@ManyToOne
	private Location growLocation;

	public Person getResponsiblePerson() {
		return responsiblePerson;
	}
	
	public void setResponsiblePerson(Person responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}

	public int getCuttingNumber() {
		return cuttingNumber;
	}

	public void setCuttingNumber(int cuttingNumber) {
		this.cuttingNumber = cuttingNumber;
	}
	
	public LocalDate getDatePlanted() {
		return datePlanted;
	}

	public void setDatePlanted(LocalDate datePlanted) {
		this.datePlanted = datePlanted;
	}

	public GrowStatus getStatus() {
		return status;
	}

	public void setStatus(GrowStatus status) {
		this.status = status;
	}

	public int getAmountOfCuttings() {
		return amountOfCuttings;
	}

	public void setAmountOfCuttings(int amountOfCuttings) {
		this.amountOfCuttings = amountOfCuttings;
	}

	public Blossom getMotherPlant() {
		return motherPlant;
	}

	public void setMotherPlant(Blossom motherPlant) {
		this.motherPlant = motherPlant;
	}

	public Location getGrowLocation() {
		return growLocation;
	}

	public void setGrowLocation(Location growLocation) {
		this.growLocation = growLocation;
	}
}
