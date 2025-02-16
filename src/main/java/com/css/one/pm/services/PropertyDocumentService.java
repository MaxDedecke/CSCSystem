package com.css.one.pm.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.css.one.pm.data.PropertyDocument;
import com.css.one.pm.data.repos.PropertyDocumentRepository;

@Service
public class PropertyDocumentService {

	 private final PropertyDocumentRepository repository;

	    public PropertyDocumentService(PropertyDocumentRepository repository) {
	        this.repository = repository;
	    }

	    public Optional<PropertyDocument> get(Long id) {
	        return repository.findById(id);
	    }

	    public PropertyDocument update(PropertyDocument entity) {
	        return repository.save(entity);
	    }

	    public void delete(Long id) {
	        repository.deleteById(id);
	    }

	    public int count() {
	        return (int) repository.count();
	    }
	    
	    public Optional<PropertyDocument> findAllByPropertyManagementId(Long propertyManagementId) { 
	    	return repository.findAll().stream().filter(e -> e.getPropertyManagementId().equals(propertyManagementId)).findAny();
	    }
}
