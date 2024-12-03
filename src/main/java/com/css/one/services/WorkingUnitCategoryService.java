package com.css.one.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.css.one.data.WorkingUnitCategory;
import com.css.one.data.repos.WorkingUnitCategoryRepository;

@Service
public class WorkingUnitCategoryService {
	private final WorkingUnitCategoryRepository repository;
	 
	public WorkingUnitCategoryService(WorkingUnitCategoryRepository repository) {
	        this.repository = repository;
	    }

	    public Optional<WorkingUnitCategory> get(Long id) {
	        return repository.findById(id);
	    }

	    public WorkingUnitCategory update(WorkingUnitCategory entity) {
	        return repository.save(entity);
	    }

	    public void delete(Long id) {
	        repository.deleteById(id);
	    }

	    public Page<WorkingUnitCategory> list(Pageable pageable) {
	        return repository.findAll(pageable);
	    }

	    public Page<WorkingUnitCategory> list(Pageable pageable, Specification<WorkingUnitCategory> filter) {
	        return repository.findAll(filter, pageable);
	    }

	    public int count() {
	        return (int) repository.count();
	    }
	    
	    public List<WorkingUnitCategory> findAllByAssociation(int associationId) {
	    	List<WorkingUnitCategory> categories = new ArrayList<>();
	    	Optional<WorkingUnitCategory> byId = repository.findById(Integer.toUnsignedLong(0));
	    	if(byId.isPresent()) {	    		
	    		categories.addAll(Arrays.asList(byId.get()));
	    	}
	    	categories.addAll(repository.findAll().stream().filter(e -> e.getAssociationId() == associationId).toList());
	    	return categories;
	    }
}
