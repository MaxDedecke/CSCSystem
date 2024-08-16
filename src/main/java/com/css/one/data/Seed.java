package com.css.one.data;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class Seed extends OutputEntity {
	
	private int seedNumber;
	private int amountOfSeeds;
 	
 	@ManyToOne
 	private Person responsiblePerson;

	@ManyToOne
 	private Blossom motherPlant;
	
	@Nullable
	@ManyToOne
	private Location growLocation;

	public Location getGrowLocation() {
		return growLocation;
	}

	public void setGrowLocation(Location growLocation) {
		this.growLocation = growLocation;
	}

	public int getSeedNumber() {
		return seedNumber;
	}
	
	public void setSeedNumber(int seedNumber) {
		this.seedNumber = seedNumber;
	}
	
	public int getAmountOfSeeds() {
		return amountOfSeeds;
	}
	
	public void setAmountOfSeeds(int amountOfSeeds) {
		this.amountOfSeeds = amountOfSeeds;
	}
	
	public Person getResponsiblePerson() {
		return responsiblePerson;
	}
	
	public void setResponsiblePerson(Person responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}

	public Blossom getMotherPlant() {
		return motherPlant;
	}

	public void setMotherPlant(Blossom motherPlant) {
		this.motherPlant = motherPlant;
	}
}
