package com.css.one.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.css.one.data.Strain;
import com.css.one.data.StrainRepository;

@Service
public class StrainService {
	private final StrainRepository repository;

    public StrainService(StrainRepository repository) {
        this.repository = repository;
    }

    public Optional<Strain> get(Long id) {
        return repository.findById(id);
    }

    public Strain update(Strain entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Strain> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Strain> list(Pageable pageable, Specification<Strain> filter) {
        return repository.findAll(filter, pageable);
    }
    
    public List<Strain> findAllByAssociation(int associationId) {
    	return repository.findAll().stream().filter(e -> e.getAssociationId() == associationId).toList();
    }
    
    public int count() {
        return (int) repository.count();
    }
    
    public List<Strain> getAllAsList() {
    	return repository.findAll();
    }
}
