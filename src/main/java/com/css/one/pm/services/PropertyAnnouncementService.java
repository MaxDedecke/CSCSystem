package com.css.one.pm.services;

import java.util.Optional;

import com.css.one.pm.data.PropertyAnnouncement;
import com.css.one.pm.data.repos.PropertyAnnouncementRepository;

public class PropertyAnnouncementService {
	
	 private final PropertyAnnouncementRepository repository;

	    public PropertyAnnouncementService(PropertyAnnouncementRepository repository) {
	        this.repository = repository;
	    }

	    public Optional<PropertyAnnouncement> get(Long id) {
	        return repository.findById(id);
	    }

	    public PropertyAnnouncement update(PropertyAnnouncement entity) {
	        return repository.save(entity);
	    }

	    public void delete(Long id) {
	        repository.deleteById(id);
	    }

	    public int count() {
	        return (int) repository.count();
	    }
	    
	    public Optional<PropertyAnnouncement> findAllByPropertyManagementId(Long propertyManagementId) { 
	    	return repository.findAll().stream().filter(e -> e.getPropertyManagementId().equals(propertyManagementId)).findAny();
	    }
}
