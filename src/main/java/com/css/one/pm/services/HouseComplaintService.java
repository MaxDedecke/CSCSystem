package com.css.one.pm.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.css.one.pm.data.HouseComplaint;
import com.css.one.pm.data.repos.HouseComplaintRepository;

@Service
public class HouseComplaintService {

	 private final HouseComplaintRepository repository;

	    public HouseComplaintService(HouseComplaintRepository repository) {
	        this.repository = repository;
	    }

	    public Optional<HouseComplaint> get(Long id) {
	        return repository.findById(id);
	    }

	    public HouseComplaint update(HouseComplaint entity) {
	        return repository.save(entity);
	    }

	    public void delete(Long id) {
	        repository.deleteById(id);
	    }

	    public int count() {
	        return (int) repository.count();
	    }
	    
	    public Optional<HouseComplaint> findAllByPropertyManagementId(Long propertyManagementId) { 
	    	return repository.findAll().stream().filter(e -> e.getPropertyManagementId().equals(propertyManagementId)).findAny();
	    }
}
