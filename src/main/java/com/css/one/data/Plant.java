package com.css.one.data;

import java.time.LocalDate;
import java.util.List;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Plant implements EntityWrapper{
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Long id;
	
	private int associationId;
	
	String name;
	LocalDate dateOfExistense;
	
 	private GrowStatus status;
 	
	@Nullable
	@ManyToOne
	private Location growLocation;
	
	@Nullable
	private List<String> tags;
	
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	public GrowStatus getStatus() {
		return status;
	}
	public void setStatus(GrowStatus status) {
		this.status = status;
	}
	public Location getGrowLocation() {
		return growLocation;
	}
	public void setGrowLocation(Location growLocation) {
		this.growLocation = growLocation;
	}
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
	@Override
	public Location getLocation() {
		return getGrowLocation();
	}
	@Override
	public boolean hasElements() {
		return false;
	}
}
