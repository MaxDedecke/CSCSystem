package com.css.one.data;

import java.time.LocalDate;

import com.css.one.data.enums.PaymentMethod;
import com.css.one.data.enums.TransactionType;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;

@Entity
public class Transaction {

 	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

	@Version
	private int version;
	private String note;
	private double amount;
	private TransactionType type;
    private LocalDate dateOfTransaction;
    private int associationId;
    private PaymentMethod paymentMethod;
    
    @Nullable
    private int memberId;
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	@Override
	public int hashCode() {
		if (getId() != null) {
			return getId().hashCode();
		}
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AbstractEntity that)) {
			return false; // null or not an AbstractEntity class
		}
		if (getId() != null) {
			return getId().equals(that.getId());
		}
		return super.equals(that);
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public TransactionType getType() {
		return type;
	}

	public void setType(TransactionType type) {
		this.type = type;
	}

	public LocalDate getDateOfTransaction() {
		return dateOfTransaction;
	}

	public void setDateOfTransaction(LocalDate dateOfTransaction) {
		this.dateOfTransaction = dateOfTransaction;
	}

	public int getAssociationId() {
		return associationId;
	}

	public void setAssociationId(int associationId) {
		this.associationId = associationId;
	}

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
}
