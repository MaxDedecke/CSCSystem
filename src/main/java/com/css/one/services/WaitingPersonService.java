package com.css.one.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.css.one.data.WaitingPerson;
import com.css.one.data.WaitingPersonRepository;

@Service
public class WaitingPersonService {
	
	private final WaitingPersonRepository repository;
	
	public WaitingPersonService(WaitingPersonRepository repository) {
        this.repository = repository;
    }

    public Optional<WaitingPerson> get(Long id) {
        return repository.findById(id);
    }

    public WaitingPerson update(WaitingPerson entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
    
    public List<WaitingPerson> findAllByAssociation(int associationId) {
    	return repository.findAll().stream().filter(e -> e.getAssociationId() == associationId).toList();
    }

    public Page<WaitingPerson> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<WaitingPerson> list(Pageable pageable, Specification<WaitingPerson> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }
    
    public List<WaitingPerson> getAllAsList() {
    	return repository.findAll();
    }


}
