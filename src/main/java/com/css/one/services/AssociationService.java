package com.css.one.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.css.one.data.Association;
import com.css.one.data.AssociationRepository;

@Service
public class AssociationService {

    private final AssociationRepository repository;

    public AssociationService(AssociationRepository repository) {
        this.repository = repository;
    }

    public Optional<Association> get(Long id) {
        return repository.findById(id);
    }

    public Association update(Association entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public int count() {
        return (int) repository.count();
    }
}
