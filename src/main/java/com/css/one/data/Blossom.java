package com.css.one.data;

import java.time.LocalDate;
import java.util.List;

import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

@Entity
public class Blossom extends OutputEntity {
	
    	private int strainNumber;
	 	private LocalDate datePlanted;
	 	private GrowStatus status;
	 	private int amountOfPlants;
	 	
	 	@Nullable
	 	private String pathOfCertificate;
	 	
		@Nullable
	 	private double amountGramm;
	 	
	 	@Nullable
	 	private double thc;
	 	
	 	@Nullable
	 	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	 	private List<Person> weighedByMembers;
	 	
		@Nullable
	 	private LocalDate dateFinished;
		
		@Nullable
		@ManyToOne
		private Location growLocation;
		
		public String getPathOfCertificate() {
			return pathOfCertificate;
		}
		public void setPathOfCertificate(String pathOfCertificate) {
			this.pathOfCertificate = pathOfCertificate;
		}
		public Location getGrowLocation() {
			return growLocation;
		}
		public void setGrowLocation(Location growLocation) {
			this.growLocation = growLocation;
		}
		
		public List<Person> getWeighedByMembers() {
			return weighedByMembers;
		}
		public void setWeighedByMembers(List<Person> weighedByMembers) {
			this.weighedByMembers = weighedByMembers;
		}
		public double getAmountGramm() {
			return amountGramm;
		}
		public void setAmountGramm(double amount) {
			this.amountGramm = amount;
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
		public int getAmountOfPlants() {
			return amountOfPlants;
		}
		public void setAmountOfPlants(int amountOfPlants) {
			this.amountOfPlants = amountOfPlants;
		}
		public int getStrainNumber() {
			return strainNumber;
		}
		public void setStrainNumber(int strainNumber) {
			this.strainNumber = strainNumber;
		}	 	
}
