package com.css.one.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.css.one.data.CalculationResult;
import com.css.one.data.Transaction;
import com.css.one.data.TransactionRepository;
import com.css.one.data.TransactionType;

@Service
public class TransactionService {
	 private final TransactionRepository repository;

	    public TransactionService(TransactionRepository repository) {
	        this.repository = repository;
	    }

	    public Optional<Transaction> get(Long id) {
	        return repository.findById(id);
	    }

	    public Transaction update(Transaction entity) {
	        return repository.save(entity);
	    }

	    public void delete(Long id) {
	        repository.deleteById(id);
	    }

	    public Page<Transaction> list(Pageable pageable) {
	        return repository.findAll(pageable);
	    }

	    public Page<Transaction> list(Pageable pageable, Specification<Transaction> filter) {
	        return repository.findAll(filter, pageable);
	    }

	    public int count() {
	        return (int) repository.count();
	    }
	    
	    public List<Transaction> findAllByAssociation(int associationId) {
	    	return repository.findAll().stream().filter(e -> e.getAssociationId() == associationId).toList();
	    }
	    
	    public List<Transaction> findByType(TransactionType type, int associationId) {
			return findAllByAssociation(associationId).stream().filter(e -> e.getType() == type).toList();
	    }
	    
	    public CalculationResult getBalanceForType(TransactionType type, int associationId) {	    	
	    	if(type == null) {
	    		List<Double> collectCosts = new ArrayList<Double>();
	    		List<Double> collectIncome = new ArrayList<Double>();

	    		findAllByAssociation(associationId).stream().filter(e -> e.getType() == TransactionType.COST).collect(Collectors.toList()).forEach(t -> collectCosts.add(t.getAmount()));
	    		findAllByAssociation(associationId).stream().filter(e -> e.getType() == TransactionType.INCOME).collect(Collectors.toList()).forEach(t -> collectIncome.add(t.getAmount()));
	    		
	    		double amountCosts = 0;
	    		double amountIncome = 0;
	    		
	    		for(Double cost : collectCosts) {
	    			amountCosts = amountCosts + cost.doubleValue();
	    		}
	    		
	    		for(Double income: collectIncome) {
	    			amountIncome = amountIncome + income.doubleValue();
	    		}
	    			    		
	    		return new CalculationResult(amountIncome - amountCosts, false);
	    	} else {
		    	List<Double> amount = new ArrayList<Double>();

		    	findAllByAssociation(associationId).stream().filter(e -> e.getType() == type).collect(Collectors.toList()).forEach(t -> amount.add(t.getAmount()));
	    		
	    		double result = 0;
	    		
	    		for(Double singleAmount : amount) {
	    			result = result + singleAmount.doubleValue();
	    		}
	    		
	    		return new CalculationResult(result, false);
	    		
	    	}
	    }
}
