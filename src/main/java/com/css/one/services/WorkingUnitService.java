package com.css.one.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.css.one.data.WorkingUnit;
import com.css.one.data.WorkingUnitRepository;

@Service
public class WorkingUnitService {
	 private final WorkingUnitRepository repository;

	    public WorkingUnitService(WorkingUnitRepository repository) {
	        this.repository = repository;
	    }

	    public Optional<WorkingUnit> get(Long id) {
	        return repository.findById(id);
	    }

	    public WorkingUnit update(WorkingUnit entity) {
	        return repository.save(entity);
	    }

	    public void delete(Long id) {
	        repository.deleteById(id);
	    }

	    public Page<WorkingUnit> list(Pageable pageable) {
	        return repository.findAll(pageable);
	    }

	    public Page<WorkingUnit> list(Pageable pageable, Specification<WorkingUnit> filter) {
	        return repository.findAll(filter, pageable);
	    }

	    public int count() {
	        return (int) repository.count();
	    }
	    
	    public List<WorkingUnit> findAllByAssociation(int associationId) {
	    	return repository.findAll().stream().filter(e -> e.getAssociationId() == associationId).toList(); 
	    }
	    
	    public List<WorkingUnit> findByCategory(String category, int associationId) {
	    	return findAllByAssociation(associationId).stream().filter(e -> e.getCategory().equals(category)).toList();
	    }
}
