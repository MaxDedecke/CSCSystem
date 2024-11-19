package com.css.one.data;

import java.util.List;

import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class SubscriptionModel {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
 	
    private double amount;
    private String name;
    private String description;
    
    private int associationId;
    
    @Nullable
    @OneToMany
    (fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    List<Person> memberOfModel;
    
	@Nullable
	@OneToMany
	(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    List<WaitingPerson> waitingPersonOfModel;
	
	@Nullable
	private double amountSale;
	
	private boolean isOnline;
	private int maxAllowedMembers;
    
    public double getAmountSale() {
		return amountSale;
	}

	public void setAmountSale(double amountSale) {
		this.amountSale = amountSale;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Person> getMemberOfModel() {
		return memberOfModel;
	}

	public void setMemberOfModel(List<Person> memberOfModel) {
		this.memberOfModel = memberOfModel;
	}

	public List<WaitingPerson> getWaitingPersonOfModel() {
		return waitingPersonOfModel;
	}

	public void setWaitingPersonOfModel(List<WaitingPerson> waitingPersonOfModel) {
		this.waitingPersonOfModel = waitingPersonOfModel;
	}

	public int getAssociationId() {
		return associationId;
	}

	public void setAssociationId(int associationid) {
		this.associationId = associationid;
	}

	public boolean isOnline() {
		return isOnline;
	}

	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}

	public int getMaxAllowedMembers() {
		return maxAllowedMembers;
	}

	public void setMaxAllowedMembers(int maxAllowedMembers) {
		this.maxAllowedMembers = maxAllowedMembers;
	}
}
