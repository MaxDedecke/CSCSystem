package com.css.one.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.css.one.data.OnboardingQuestion;
import com.css.one.data.OnboardingQuestionRepository;

@Service
public class OnboardingQuestionService {
	private final OnboardingQuestionRepository repository;

    public OnboardingQuestionService(OnboardingQuestionRepository repository) {
        this.repository = repository;
    }

    public Optional<OnboardingQuestion> get(Long id) {
        return repository.findById(id);
    }

    public OnboardingQuestion update(OnboardingQuestion entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public int count() {
        return (int) repository.count();
    }
    
    public List<OnboardingQuestion> findAllByAssociation(int associationId) {
    	List<OnboardingQuestion> questions = new ArrayList<>();
    	questions.addAll(repository.findAll().stream().filter(e -> e.getAssociationId() == associationId).toList());
    	return questions;
    }
    
}
