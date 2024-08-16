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
public class RecurringPayment {
	
 	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
 	
	Timezone timezone;
	TimeDelcaration timeDeclaration;
	TransactionType type;

	double amount;
	String title;

	int associationId;
	private String note;
	boolean isActive;
	
	@Nullable   
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true) 
	List<Transaction> transactions;
	
	@Nullable
	private PaymentMethod paymentMethod;
	
	@Nullable
	int personId;
	
	@Nullable
	int dayOfPayment;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public TransactionType getType() {
		return type;
	}

	public void setType(TransactionType type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public TimeDelcaration getTimeDeclaration() {
		return timeDeclaration;
	}
	
	public void setTimeDeclaration(TimeDelcaration timeDeclaration) {
		this.timeDeclaration = timeDeclaration;
	}

	public Timezone getTimezone() {
		return timezone;
	}
	
	public void setTimezone(Timezone timezone) {
		this.timezone = timezone;
	}
	
	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public boolean isActive() {
		return isActive;
	}
	
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	public int getDayOfPayment() {
		return dayOfPayment;
	}

	public void setDayOfPayment(int dayOfPayment) {
		this.dayOfPayment = dayOfPayment;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public int getAssociationId() {
		return associationId;
	}

	public void setAssociationId(int associationId) {
		this.associationId = associationId;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

	public int getPersonId() {
		return personId;
	}

	public void setPersonId(int personId) {
		this.personId = personId;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}
