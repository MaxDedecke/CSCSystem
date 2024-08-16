package com.css.one.data;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class OutputEntity {

 	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
 	
	private String name;
 	private int associationId;
 	private double price;
 	
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
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
}
