package com.css.one.pm.services;

import java.util.Optional;

import com.css.one.pm.data.PropertyManagementData;
import com.css.one.pm.data.repos.PropertyManagementDataRepository;

public class PropertyManagementDataService {
	 private final PropertyManagementDataRepository repository;

	    public PropertyManagementDataService(PropertyManagementDataRepository repository) {
	        this.repository = repository;
	    }

	    public Optional<PropertyManagementData> get(Long id) {
	        return repository.findById(id);
	    }

	    public PropertyManagementData update(PropertyManagementData entity) {
	        return repository.save(entity);
	    }

	    public void delete(Long id) {
	        repository.deleteById(id);
	    }

	    public int count() {
	        return (int) repository.count();
	    }
	    
	    public Optional<PropertyManagementData> findAllByPropertyManagementId(Long propertyManagementId) { 
	    	return repository.findAll().stream().filter(e -> e.getPropertyManagementId().equals(propertyManagementId)).findAny();
	    }
}
