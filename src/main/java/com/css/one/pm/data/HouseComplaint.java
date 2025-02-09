package com.css.one.pm.data;

import com.css.one.pm.data.enums.ComplainType;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class HouseComplaint {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
 	private Long id;
	
	private ComplainType type;
	
	private Long propertyManagementId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ComplainType getType() {
		return type;
	}

	public void setType(ComplainType type) {
		this.type = type;
	}

	public Long getPropertyManagementId() {
		return propertyManagementId;
	}

	public void setPropertyManagementId(Long propertyManagementId) {
		this.propertyManagementId = propertyManagementId;
	}
	
}
