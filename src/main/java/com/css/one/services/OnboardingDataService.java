package com.css.one.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.css.one.data.OnboardingData;
import com.css.one.data.repos.OnboardingDataRepository;

@Service
public class OnboardingDataService {
	private final OnboardingDataRepository repository;

    public OnboardingDataService(OnboardingDataRepository repository) {
        this.repository = repository;
    }

    public Optional<OnboardingData> get(Long id) {
        return repository.findById(id);
    }

    public OnboardingData update(OnboardingData entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
    
    public List<OnboardingData> findAllByAssociation(int associationId) {
    	return repository.findAll().stream().filter(e -> e.getAssociationId() == associationId).toList();
    }
    
    public Optional<OnboardingData> findByToken(Long tokenId) {
    	return repository.findAll().stream().filter(e -> e.getToken() != null).toList().stream()
    			.filter(e -> e.getToken().getId().equals(tokenId)).findAny();
    }
    
    public Optional<OnboardingData> findByMemberId(Long memberId) {
    	return repository.findAll().stream().filter(e -> e.getMemberNumber() != null).toList().stream()
    			.filter(e -> e.getMemberNumber().equals(memberId)).findAny();
    }

    public Page<OnboardingData> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<OnboardingData> list(Pageable pageable, Specification<OnboardingData> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }
    
    public List<OnboardingData> getAllAsList() {
    	return repository.findAll();
    }
}
