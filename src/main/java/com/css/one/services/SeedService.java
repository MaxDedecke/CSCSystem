package com.css.one.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.css.one.data.Seed;
import com.css.one.data.SeedRepository;
import com.css.one.data.Blossom;

@Service
public class SeedService {
	 private final SeedRepository repository;

	    public SeedService(SeedRepository repository) {
	        this.repository = repository;
	    }

	    public Optional<Seed> get(Long id) {
	        return repository.findById(id);
	    }

	    public Seed update(Seed entity) {
	        return repository.save(entity);
	    }

	    public void delete(Long id) {
	        repository.deleteById(id);
	    }

	    public Page<Seed> list(Pageable pageable) {
	        return repository.findAll(pageable);
	    }

	    public Page<Seed> list(Pageable pageable, Specification<Seed> filter) {
	        return repository.findAll(filter, pageable);
	    }

	    public int count() {
	        return (int) repository.count();
	    }
	    
	    public List<Seed> findAllByAssociation(int associationId) {
	    	List<Seed> cuttings = new ArrayList<Seed>();
	    	cuttings.addAll(repository.findAll().stream().filter(e -> e.getAssociationId() == associationId).toList());
	    	return cuttings;
	    }
	    
	    public List<Seed> findByStrain(Blossom strain, int associationId) {
			return findAllByAssociation(associationId).stream().filter(e -> e.getMotherPlant().getId().equals(strain.getId())).toList();
	    }  
	    
	    public int getFreeSeedNumber(int associationId) {

			Random rand = new Random();
			boolean isNotEqual = false;
			int n = 0;

			while (!isNotEqual) {
				n = rand.nextInt(1000000);
				for (Seed s : findAllByAssociation(associationId)) {
					if (s.getSeedNumber() == n) {
						isNotEqual = false;
					}
				}
				isNotEqual = true;
			}

			return n;
		}
}
