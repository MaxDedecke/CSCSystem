package com.css.one.data;

import java.time.LocalDate;

import com.css.one.data.enums.OnboardingStatus;

import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Email;

@Entity
public class WaitingPerson {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String firstName;
	private String lastName;
	@Email
	private String email;
	private String phone;
	private LocalDate dateOfBirth;
	private int associationId;
	private LocalDate dateOfRegistration;
	
	private boolean isOnboaring;
	
	@Nullable
	private OnboardingStatus onboardingStatus;
	
	@Nullable
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private MemberData memberData;
	
	@Nullable
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Person member;
	
	public Person getMember() {
		return member;
	}
	public void setMember(Person member) {
		this.member = member;
	}
	public MemberData getMemberData() {
		return memberData;
	}
	public void setMemberData(MemberData memberData) {
		this.memberData = memberData;
	} 
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public int getAssociationId() {
		return associationId;
	}

	public void setAssociationId(int associationId) {
		this.associationId = associationId;
	}

	public LocalDate getDateOfRegistration() {
		return dateOfRegistration;
	}

	public void setDateOfRegistration(LocalDate dateOfRegistration) {
		this.dateOfRegistration = dateOfRegistration;
	}
	public boolean isOnboaring() {
		return isOnboaring;
	}
	public void setOnboaring(boolean isOnboaring) {
		this.isOnboaring = isOnboaring;
	}
	public OnboardingStatus getOnboardingStatus() {
		return onboardingStatus;
	}
	public void setOnboardingStatus(OnboardingStatus onboardingStatus) {
		this.onboardingStatus = onboardingStatus;
	}
}
