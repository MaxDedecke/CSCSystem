package com.css.one.data;

import jakarta.persistence.Entity;

@Entity
public class WorkingUnitCategory extends AbstractEntity {
	
	private String name;
    private int associationId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAssociationId() {
		return associationId;
	}

	public void setAssociationId(int associationId) {
		this.associationId = associationId;
	}
}
