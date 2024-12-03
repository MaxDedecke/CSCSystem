package com.css.one.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.css.one.data.Charge;
import com.css.one.data.Plant;
import com.css.one.data.repos.ChargeRepository;

@Service
public class ChargeService {
	private final ChargeRepository repository;

    public ChargeService(ChargeRepository repository) {
        this.repository = repository;
    }

    public Optional<Charge> get(Long id) {
        return repository.findById(id);
    }

    public Charge update(Charge entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Charge> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Charge> list(Pageable pageable, Specification<Charge> filter) {
        return repository.findAll(filter, pageable);
    }
    
    public List<Charge> findAllByAssociation(int associationId) {
    	return repository.findAll().stream().filter(e -> e.getAssociationId() == associationId).toList();
    }
    
    public int count() {
        return (int) repository.count();
    }
    
    public List<Charge> getAllAsList() {
    	return repository.findAll();
    }
    
	public Optional<Charge> findChargeByPlant(int associationId, Plant plant) {
		return repository.findAll().stream()
				.filter(e -> e.getAssociationId() == associationId
						&& e.getPlants().stream().filter(p -> p.getId().equals(plant.getId())).findAny().isPresent())
				.findAny();
	}
    
//    public List<Plant> findOutputByStrain(int strainId) {
//    	return repository.findAll().stream().filter(e -> e.getEntityId() == strainId).toList();
//    }
}
