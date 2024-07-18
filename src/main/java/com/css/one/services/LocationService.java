package com.css.one.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.css.one.data.Location;
import com.css.one.data.LocationRepository;

@Service
public class LocationService {
	
	  	private final LocationRepository repository;

	    public LocationService(LocationRepository repository) {
	        this.repository = repository;
	    }

	    public Optional<Location> get(Long id) {
	        return repository.findById(id);
	    }

	    public Location update(Location entity) {
	        return repository.save(entity);
	    }

	    public void delete(Long id) {
	        repository.deleteById(id);
	    }

	    public int count() {
	        return (int) repository.count();
	    }
	    
	    public List<Location> findAllByAssociation(int associationId) {
	    	return repository.findAll().stream().filter(e -> e.getAssociationId() == associationId).toList();
	    }
}
