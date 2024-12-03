package com.css.one.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.css.one.data.AssociationSettings;
import com.css.one.data.enums.ExpirationTime;
import com.css.one.data.repos.AssociationSettingsRepository;

@Service
public class AssociationSettingsService {
	
	private final AssociationSettingsRepository repository;

    public AssociationSettingsService(AssociationSettingsRepository repository) {
        this.repository = repository;
    }

    public Optional<AssociationSettings> get(Long id) {
        return repository.findById(id);
    }

    public AssociationSettings update(AssociationSettings entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<AssociationSettings> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<AssociationSettings> list(Pageable pageable, Specification<AssociationSettings> filter) {
        return repository.findAll(filter, pageable);
    }
    
    public Optional<AssociationSettings> findAllByAssociation(int associationId) { 
    	return repository.findAll().stream().filter(e -> e.getAssociationId() == associationId).findAny();
    }
    
    public AssociationSettings createInitialSettings(int associationId) {
    	AssociationSettings newSettings = new AssociationSettings();
    	newSettings.setAssociationId(associationId);
    	newSettings.setOnboardingTokenExpirationTime(ExpirationTime.TWO_WEEKS);
    	
    	return update(newSettings);
    }
}
