package com.css.one.data;

import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Email;
import java.time.LocalDate;

@Entity
public class Person {
	
 	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
 	
    private int memberNumber;
    private String firstName;
    private String lastName;
    @Email
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private AssociationRole associationRole;
    private int associationId;
    @Nullable
    private LocalDate dateOfHigherRole;
    
    @Nullable
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private MemberData memberData;
    
    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public MemberData getMemberData() {
		return memberData;
	}
	public void setMemberData(MemberData memberData) {
		this.memberData = memberData;
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
	public AssociationRole getAssociationRole() {
		return associationRole;
	}
	public void setAssociationRole(AssociationRole associationRole) {
		this.associationRole = associationRole;
	}
	public LocalDate getDateOfHigherRole() {
		return dateOfHigherRole;
	}
	public void setDateOfHigherRole(LocalDate dateOfHigherRole) {
		this.dateOfHigherRole = dateOfHigherRole;
	}
	public int getMemberNumber() {
		return memberNumber;
	}
	public void setMemberNumber(int memberNumber) {
		this.memberNumber = memberNumber;
	}

}
