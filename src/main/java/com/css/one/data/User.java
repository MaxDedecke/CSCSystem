package com.css.one.data;

import com.css.one.data.enums.Role;
import com.css.one.security.BusinessCase;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.util.Set;

@Entity
@Table(name = "application_user")
public class User {

 	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
 	
    private String username;
    private String name;
    @JsonIgnore
    private String hashedPassword;
    
    private Long entityId;
    
	@Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles;
    @Lob
    @Column(length = 1000000)
    @Nullable
    private byte[] profilePicture;
    
    private int associationId;
    
    private BusinessCase businessCase;
    
    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public int getAssociationId() {
		return associationId;
	}

	public void setAssociationId(int associationId) {
		this.associationId = associationId;
	}
	public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getHashedPassword() {
        return hashedPassword;
    }
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }
    public Set<Role> getRoles() {
        return roles;
    }
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
    public byte[] getProfilePicture() {
        return profilePicture;
    }
    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }
	public BusinessCase getBusinessCase() {
		return businessCase;
	}
	public void setBusinessCase(BusinessCase businessCase) {
		this.businessCase = businessCase;
	}

}
