package com.css.one.data;

import java.util.List;

import io.micrometer.common.lang.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class LawInfo {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
 	private Long id;
	
	@OneToOne
	Association association;
	
	@Nullable
	String statutePath;
	@Nullable
	String statuteName;
	@Nullable
	String attorneyOrgName;
	@Nullable
	String attorneyName;
	@Nullable
	String attorneyEmail;
	@Nullable
	String attorneyPhone;
	
	@Nullable
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true) 
	List<Person> suspects;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getAttorneyOrgName() {
		return attorneyOrgName;
	}

	public void setAttorneyOrgName(String attorneyOrgName) {
		this.attorneyOrgName = attorneyOrgName;
	}
	
	public String getStatuteName() {
		return statuteName;
	}

	public void setStatuteName(String statuteName) {
		this.statuteName = statuteName;
	}
	
	public Association getAssociation() {
		return association;
	}

	public void setAssociation(Association association) {
		this.association = association;
	}
	
	public String getStatutePath() {
		return statutePath;
	}

	public void setStatutePath(String statutePath) {
		this.statutePath = statutePath;
	}

	public String getAttorneyName() {
		return attorneyName;
	}

	public void setAttorneyName(String attorneyName) {
		this.attorneyName = attorneyName;
	}

	public String getAttorneyEmail() {
		return attorneyEmail;
	}

	public void setAttorneyEmail(String attorneyEmail) {
		this.attorneyEmail = attorneyEmail;
	}

	public String getAttorneyPhone() {
		return attorneyPhone;
	}

	public void setAttorneyPhone(String attorneyPhone) {
		this.attorneyPhone = attorneyPhone;
	}

	public List<Person> getSuspects() {
		return suspects;
	}

	public void setSuspects(List<Person> suspects) {
		this.suspects = suspects;
	}
}
