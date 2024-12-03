package com.css.one.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.css.one.data.Plant;
import com.css.one.data.repos.PlantRepository;

@Service
public class PlantService {
	private final PlantRepository repository;

    public PlantService(PlantRepository repository) {
        this.repository = repository;
    }

    public Optional<Plant> get(Long id) {
        return repository.findById(id);
    }

    public Plant update(Plant entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Plant> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Plant> list(Pageable pageable, Specification<Plant> filter) {
        return repository.findAll(filter, pageable);
    }
    
    public List<Plant> findAllByAssociation(int associationId) {
    	return repository.findAll().stream().filter(e -> e.getAssociationId() == associationId).toList();
    }
    
    public int count() {
        return (int) repository.count();
    }
    
    public List<Plant> getAllAsList() {
    	return repository.findAll();
    }
//    
//    public List<Plant> findOutputByStrain(int strainId) {
//    	return repository.findAll().stream().filter(e -> e.getEntityId() == strainId).toList();
//    }
}
