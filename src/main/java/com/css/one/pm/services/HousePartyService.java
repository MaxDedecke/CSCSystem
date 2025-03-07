package com.css.one.pm.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.css.one.pm.data.HouseParty;
import com.css.one.pm.data.repos.HousePartyRepository;

@Service
public class HousePartyService {

	 private final HousePartyRepository repository;

	    public HousePartyService(HousePartyRepository repository) {
	        this.repository = repository;
	    }

	    public Optional<HouseParty> get(Long id) {
	        return repository.findById(id);
	    }

	    public HouseParty update(HouseParty entity) {
	        return repository.save(entity);
	    }

	    public void delete(Long id) {
	        repository.deleteById(id);
	    }

	    public int count() {
	        return (int) repository.count();
	    }
	    
	    public Optional<HouseParty> findAllByPropertyManagementId(Long propertyManagementId) { 
	    	return repository.findAll().stream().filter(e -> e.getPropertyManagementId().equals(propertyManagementId)).findAny();
	    }
}
