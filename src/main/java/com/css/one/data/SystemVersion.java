package com.css.one.data;

import java.time.LocalDate;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class SystemVersion {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
 	private Long id;
	
	private String versionNumber;
	
	private int versionInteger;
	
	private String description;
	
	private LocalDate releaseDate;
	
	private LocalDate createdAt;
	
	@Nullable
	private LocalDate updatedAt;

	private boolean isActive;	
	
	private boolean isMigrated;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(String versionNumber) {
		this.versionNumber = versionNumber;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDate getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(LocalDate releaseDate) {
		this.releaseDate = releaseDate;
	}

	public LocalDate getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDate createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDate getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDate updatedAt) {
		this.updatedAt = updatedAt;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public boolean isMigrated() {
		return isMigrated;
	}

	public void setMigrated(boolean isMigrated) {
		this.isMigrated = isMigrated;
	}

	public int getVersionInteger() {
		return versionInteger;
	}

	public void setVersionInteger(int versionInteger) {
		this.versionInteger = versionInteger;
	}
	
}
