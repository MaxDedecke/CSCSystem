package com.css.one.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.css.one.data.SystemVersion;
import com.css.one.data.repos.SystemVersionRepository;

@Service
public class SystemVersionService {
	private final SystemVersionRepository repository;

    public SystemVersionService(SystemVersionRepository repository) {
        this.repository = repository;
    }

    public Optional<SystemVersion> get(Long id) {
        return repository.findById(id);
    }

    public SystemVersion update(SystemVersion entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<SystemVersion> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<SystemVersion> list(Pageable pageable, Specification<SystemVersion> filter) {
        return repository.findAll(filter, pageable);
    }
    
    public List<SystemVersion> findAllById(Long id) {
    	List<SystemVersion> list = new ArrayList<>();
    	list.addAll(repository.findAll().stream().filter(e -> e.getId().equals(id)).toList());
    	return list;
    }
    
    public int count() {
        return (int) repository.count();
    }
}
