package com.css.one.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class WorkingUnitCategory {
	
 	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
 	
	private String name;
    private int associationId;
    
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

	public int getAssociationId() {
		return associationId;
	}

	public void setAssociationId(int associationId) {
		this.associationId = associationId;
	}
}
