package com.css.one.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.css.one.data.Output;
import com.css.one.data.OutputRepository;

@Service
public class OutputService {
	private final OutputRepository repository;

    public OutputService(OutputRepository repository) {
        this.repository = repository;
    }

    public Optional<Output> get(Long id) {
        return repository.findById(id);
    }

    public Output update(Output entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Output> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Output> list(Pageable pageable, Specification<Output> filter) {
        return repository.findAll(filter, pageable);
    }
    
    public List<Output> findAllByAssociation(int associationId) {
    	return repository.findAll().stream().filter(e -> e.getAssociationId() == associationId).toList();
    }
    
    public int count() {
        return (int) repository.count();
    }
    
    public List<Output> getAllAsList() {
    	return repository.findAll();
    }
}
