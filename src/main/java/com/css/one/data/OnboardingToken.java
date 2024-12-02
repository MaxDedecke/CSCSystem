package com.css.one.data;

import java.time.LocalDate;

import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class OnboardingToken {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	private String token;
	
	@Nullable
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	private WaitingPerson waintingPerson;
	
	@Nullable
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Person member;
	
	private LocalDate expirationDate;
	
	private int associationId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public WaitingPerson getWaintingPerson() {
		return waintingPerson;
	}

	public void setWaintingPerson(WaitingPerson waintingPerson) {
		this.waintingPerson = waintingPerson;
	}

	public Person getMember() {
		return member;
	}

	public void setMember(Person member) {
		this.member = member;
	}

	public LocalDate getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(LocalDate expirationDate) {
		this.expirationDate = expirationDate;
	}

	public int getAssociationId() {
		return associationId;
	}

	public void setAssociationId(int associationId) {
		this.associationId = associationId;
	}	
}
