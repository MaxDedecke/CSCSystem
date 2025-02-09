package com.css.one.pm.data;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class PropertyDocument {

	//database representation of document present on storage
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
 	private Long id;
	
	private String pathToDocument;
	
	private List<HouseParty> sharedParties;
	
	private Long propertyManagementId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPathToDocument() {
		return pathToDocument;
	}

	public void setPathToDocument(String pathToDocument) {
		this.pathToDocument = pathToDocument;
	}

	public List<HouseParty> getSharedParties() {
		return sharedParties;
	}

	public void setSharedParties(List<HouseParty> sharedParties) {
		this.sharedParties = sharedParties;
	}

	public Long getPropertyManagementId() {
		return propertyManagementId;
	}

	public void setPropertyManagementId(Long propertyManagementId) {
		this.propertyManagementId = propertyManagementId;
	}	
}
