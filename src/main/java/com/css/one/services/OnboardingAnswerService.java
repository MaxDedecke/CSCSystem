package com.css.one.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.css.one.data.OnboardingAnswer;
import com.css.one.data.repos.OnboardingAnswerRepository;

public class OnboardingAnswerService {
	private final OnboardingAnswerRepository repository;

    public OnboardingAnswerService(OnboardingAnswerRepository repository) {
        this.repository = repository;
    }

    public Optional<OnboardingAnswer> get(Long id) {
        return repository.findById(id);
    }

    public OnboardingAnswer update(OnboardingAnswer entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public int count() {
        return (int) repository.count();
    }
    
    public List<OnboardingAnswer> findAllByAssociation(int associationId) {
    	List<OnboardingAnswer> questions = new ArrayList<>();
    	questions.addAll(repository.findAll().stream().filter(e -> e.getAssociationId() == associationId).toList());
    	return questions;
    }
    
    public Optional<OnboardingAnswer> findByQuestion(Long questionId) {
    	return repository.findAll().stream().filter(e -> e.getQuestion().getId().equals(questionId)).findAny();
    }
}
