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
	 	private GrowStatus status;	 	
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
	 	private LocalDate dateHarvested;
		
		@Nullable
		@ManyToOne
		private Location growLocation;
		
		@ManyToOne
		@Nullable
	 	private Plant motherPlant;
		
		public Plant getMotherPlant() {
			return motherPlant;
		}

		public void setMotherPlant(Plant motherPlant) {
			this.motherPlant = motherPlant;
		}
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
		public LocalDate getDateHarvested() {
			return dateHarvested;
		}
		public void setDateHarvested(LocalDate dateHarvested) {
			this.dateHarvested = dateHarvested;
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
		public int getStrainNumber() {
			return strainNumber;
		}
		public void setStrainNumber(int strainNumber) {
			this.strainNumber = strainNumber;
		}	 	
}
