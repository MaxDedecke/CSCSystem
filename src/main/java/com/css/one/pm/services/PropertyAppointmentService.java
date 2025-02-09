package com.css.one.pm.services;

import java.util.Optional;

import com.css.one.pm.data.PropertyAppointment;
import com.css.one.pm.data.repos.PropertyAppointmentRepository;

public class PropertyAppointmentService {
	
	 private final PropertyAppointmentRepository repository;

	    public PropertyAppointmentService(PropertyAppointmentRepository repository) {
	        this.repository = repository;
	    }

	    public Optional<PropertyAppointment> get(Long id) {
	        return repository.findById(id);
	    }

	    public PropertyAppointment update(PropertyAppointment entity) {
	        return repository.save(entity);
	    }

	    public void delete(Long id) {
	        repository.deleteById(id);
	    }

	    public int count() {
	        return (int) repository.count();
	    }
	    
	    public Optional<PropertyAppointment> findAllByPropertyManagementId(Long propertyManagementId) { 
	    	return repository.findAll().stream().filter(e -> e.getPropertyManagementId().equals(propertyManagementId)).findAny();
	    }
}
