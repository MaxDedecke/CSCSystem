package com.css.one.pm.services;

import java.util.Optional;

import com.css.one.pm.data.HouseUnit;
import com.css.one.pm.data.repos.HouseUnitRepository;

public class HouseUnitService {

	 private final HouseUnitRepository repository;

	    public HouseUnitService(HouseUnitRepository repository) {
	        this.repository = repository;
	    }

	    public Optional<HouseUnit> get(Long id) {
	        return repository.findById(id);
	    }

	    public HouseUnit update(HouseUnit entity) {
	        return repository.save(entity);
	    }

	    public void delete(Long id) {
	        repository.deleteById(id);
	    }

	    public int count() {
	        return (int) repository.count();
	    }
	    
	    public Optional<HouseUnit> findAllByPropertyManagementId(Long propertyManagementId) { 
	    	return repository.findAll().stream().filter(e -> e.getPropertyManagementId().equals(propertyManagementId)).findAny();
	    }
}
