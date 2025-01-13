package com.css.one.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.css.one.data.SubscriptionModel;
import com.css.one.data.repos.SubscriptionModelRepository;

@Service
public class SubscriptionModelService {
	private final SubscriptionModelRepository repository;

    public SubscriptionModelService(SubscriptionModelRepository repository) {
        this.repository = repository;
    }

    public Optional<SubscriptionModel> get(Long id) {
        return repository.findById(id);
    }

    public SubscriptionModel update(SubscriptionModel entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<SubscriptionModel> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<SubscriptionModel> list(Pageable pageable, Specification<SubscriptionModel> filter) {
        return repository.findAll(filter, pageable);
    }
    
    public List<SubscriptionModel> findAllByAssociation(int associationId) {
    	List<SubscriptionModel> list = new ArrayList<>();
    	list.addAll(repository.findAll().stream().filter(e -> e.getAssociationId() == associationId).toList());
    	return list;
    }
    
    public int count() {
        return (int) repository.count();
    }
    
    public int countOfAssociation(int associationId) {
    	return repository.findAll().stream().filter(e -> e.getAssociationId() == associationId).toList().size();
    }
}
