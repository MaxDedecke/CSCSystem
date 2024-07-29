package com.css.one.data;

import java.time.LocalDate;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class DiaryEntry extends AbstractEntity {
	
 	private int associationId;
 	private LocalDate date;
	private String text;
	
	@Nullable
	@ManyToOne
	private Strain strain;
	
	@Nullable
	@ManyToOne
	private Seed seed;
	
	@Nullable
	@ManyToOne
	private Cutting cutting;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Strain getStrain() {
		return strain;
	}

	public void setStrain(Strain strain) {
		this.strain = strain;
	}

	public Seed getSeed() {
		return seed;
	}

	public void setSeed(Seed seed) {
		this.seed = seed;
	}

	public Cutting getCutting() {
		return cutting;
	}

	public void setCutting(Cutting cutting) {
		this.cutting = cutting;
	}

	public int getAssociationId() {
		return associationId;
	}

	public void setAssociationId(int associationId) {
		this.associationId = associationId;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}	
}
