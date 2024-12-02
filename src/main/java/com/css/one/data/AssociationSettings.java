package com.css.one.data;

import java.time.LocalDate;

import com.css.one.data.enums.ExpirationTime;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class AssociationSettings {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
 	private Long id;

	private int associationId;
	
	@Nullable
	private LocalDate onboardingTokenExpirationDate;
	
	@Nullable
	private ExpirationTime onboardingTokenExpirationTime;
	
	private int minimumMemberAge;

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

	public LocalDate getOnboardingTokenExpirationDate() {
		return onboardingTokenExpirationDate;
	}

	public void setOnboardingTokenExpirationDate(LocalDate onboardingTokenExpirationDate) {
		this.onboardingTokenExpirationDate = onboardingTokenExpirationDate;
	}

	public ExpirationTime getOnboardingTokenExpirationTime() {
		return onboardingTokenExpirationTime;
	}

	public void setOnboardingTokenExpirationTime(ExpirationTime onboardingTokenExpirationTime) {
		this.onboardingTokenExpirationTime = onboardingTokenExpirationTime;
	}

	public int getMinimumMemberAge() {
		return minimumMemberAge;
	}

	public void setMinimumMemberAge(int minimumMemberAge) {
		this.minimumMemberAge = minimumMemberAge;
	}
}
