package com.css.one.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.css.one.data.Cutting;
import com.css.one.data.repos.CuttingRepository;
import com.css.one.data.Blossom;

@Service
public class CuttingService {
	 private final CuttingRepository repository;

	    public CuttingService(CuttingRepository repository) {
	        this.repository = repository;
	    }

	    public Optional<Cutting> get(Long id) {
	        return repository.findById(id);
	    }

	    public Cutting update(Cutting entity) {
	        return repository.save(entity);
	    }

	    public void delete(Long id) {
	        repository.deleteById(id);
	    }

	    public Page<Cutting> list(Pageable pageable) {
	        return repository.findAll(pageable);
	    }

	    public Page<Cutting> list(Pageable pageable, Specification<Cutting> filter) {
	        return repository.findAll(filter, pageable);
	    }

	    public int count() {
	        return (int) repository.count();
	    }
	    
	    public List<Cutting> findAllByAssociation(int associationId) {
	    	List<Cutting> cuttings = new ArrayList<Cutting>();
	    	cuttings.addAll(repository.findAll().stream().filter(e -> e.getAssociationId() == associationId).toList());
	    	return cuttings;
	    }
	    
	    public List<Cutting> findByStrain(Blossom strain, int associationId) {
			return findAllByAssociation(associationId).stream().filter(e -> e.getMotherPlant().getId().equals(strain.getId())).toList();
	    }
	    
	    public int getFreeCuttingNumber(int associationId) {

			Random rand = new Random();
			boolean isNotEqual = false;
			int n = 0;

			while (!isNotEqual) {
				n = rand.nextInt(1000000);
				for (Cutting c : findAllByAssociation(associationId)) {
					if (c.getCuttingNumber() == n) {
						isNotEqual = false;
					}
				}
				isNotEqual = true;
			}

			return n;
		}
}
