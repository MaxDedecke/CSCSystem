package com.css.one.data;

import java.time.LocalDate;

import jakarta.persistence.Entity;

@Entity
public class Strain extends AbstractEntity {
	
	 	private String name;
	 	private double amount;
	 	private double thc;
	 	private LocalDate datePlanted;
	 	private LocalDate dateFinished;
	    private int associationId;
	    private GrowStatus status;
	 	
		public double getAmount() {
			return amount;
		}
		public void setAmount(double amount) {
			this.amount = amount;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public LocalDate getDatePlanted() {
			return datePlanted;
		}
		public void setDatePlanted(LocalDate datePlanted) {
			this.datePlanted = datePlanted;
		}
		public LocalDate getDateFinished() {
			return dateFinished;
		}
		public void setDateFinished(LocalDate dateFinished) {
			this.dateFinished = dateFinished;
		}
		public int getAssociationId() {
			return associationId;
		}
		public void setAssociationId(int associationId) {
			this.associationId = associationId;
		}
		public double getThc() {
			return thc;
		}
		public void setThc(double thc) {
			this.thc = thc;
		}
		public GrowStatus getStatus() {
			return status;
		}
		public void setStatus(GrowStatus status) {
			this.status = status;
		}	 	
}
