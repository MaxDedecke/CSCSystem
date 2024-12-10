package com.css.one.data;

import java.time.LocalDate;
import java.util.List;

import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Email;

@Entity
public class OnboardingData {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

	@Nullable
	private int memberNumber;
	
	@Nullable
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	private OnboardingToken token;
	
	private String firstName;
	private String lastName;
	
	@Email
	private String email;
	private String phone;
	private LocalDate dateOfBirth;
	
	@Nullable
	private int associationId;

	@Nullable
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	private MemberData memberData;
	
	@Nullable
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	List<OnboardingAnswer> answers;
	
	public List<OnboardingAnswer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<OnboardingAnswer> answers) {
		this.answers = answers;
	}

	public int getMemberNumber() {
		return memberNumber;
	}

	public void setMemberNumber(int memberNumber) {
		this.memberNumber = memberNumber;
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

	public OnboardingToken getToken() {
		return token;
	}

	public void setToken(OnboardingToken token) {
		this.token = token;
	}
}
