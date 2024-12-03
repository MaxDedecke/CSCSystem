package com.css.one.services;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.css.one.data.OnboardingToken;
import com.css.one.data.WaitingPerson;
import com.css.one.data.repos.OnboardingTokenRepository;

@Service
public class OnboardingTokenService {
	private final OnboardingTokenRepository repository;

    public OnboardingTokenService(OnboardingTokenRepository repository) {
        this.repository = repository;
    }

    public Optional<OnboardingToken> get(Long id) {
        return repository.findById(id);
    }

    public OnboardingToken update(OnboardingToken entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<OnboardingToken> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<OnboardingToken> list(Pageable pageable, Specification<OnboardingToken> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }
    
    public Optional<OnboardingToken> findByToken(String token) {
    	Optional<OnboardingToken> optionalToken = repository.findAll().stream().filter(e -> e.getToken().equals(token)).findAny();
    	return optionalToken;
    }
    
    public Optional<OnboardingToken> findByWaitingPerson(WaitingPerson person) {
    	Optional<OnboardingToken> optionalToken = repository.findAll().stream().filter(e -> e.getWaintingPerson().getId().equals(person.getId())).findAny();
    	return optionalToken;
    }
    
    public String generateToken() {
    	return UUID.randomUUID().toString();
    }
}
