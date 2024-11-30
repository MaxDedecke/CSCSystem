package com.css.one.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.css.one.data.Blossom;
import com.css.one.data.BlossomRepository;
import com.css.one.data.enums.GrowStatus;

@Service
public class BlossomService {
	private final BlossomRepository repository;

    public BlossomService(BlossomRepository repository) {
        this.repository = repository;
    }

    public Optional<Blossom> get(Long id) {
        return repository.findById(id);
    }

    public Blossom update(Blossom entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Blossom> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Blossom> list(Pageable pageable, Specification<Blossom> filter) {
        return repository.findAll(filter, pageable);
    }
    
    public List<Blossom> findAllByAssociation(int associationId) {
    	List<Blossom> list = new ArrayList<>();
    	list.addAll(repository.findAll().stream().filter(e -> e.getAssociationId() == associationId).toList());
    	return list;
    }
    
    public List<Blossom> findAllReadyForOutput(int associationId) {
    	return findAllByAssociation(associationId).stream().filter(e -> e.getStatus() == GrowStatus.OUTPUT_READY).toList();
    }
    
    public int count() {
        return (int) repository.count();
    }
    
    public List<Blossom> getAllAsList() {
    	return repository.findAll();
    }
    
	public int getFreeStrainNumber(int associationId) {

		Random rand = new Random();
		boolean isNotEqual = false;
		int n = 0;

		while (!isNotEqual) {
			n = rand.nextInt(1000000);
			for (Blossom s : findAllByAssociation(associationId)) {
				if (s.getStrainNumber() == n) {
					isNotEqual = false;
				}
			}
			isNotEqual = true;
		}

		return n;
	}
}
