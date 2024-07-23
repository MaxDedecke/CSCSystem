package com.css.one.data;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class OutputEntity extends AbstractEntity{

	private String name;
 	private int associationId;
 	private double price;
 	
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
