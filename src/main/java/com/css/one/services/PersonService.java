package com.css.one.services;

import com.css.one.data.Person;
import com.css.one.data.PersonRepository;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

    private final PersonRepository repository;

    public PersonService(PersonRepository repository) {
        this.repository = repository;
    }

    public Optional<Person> get(Long id) {
        return repository.findById(id);
    }

    public Person update(Person entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
    
    public List<Person> findAllByAssociation(int associationId) {
    	return repository.findAll().stream().filter(e -> e.getAssociationId() == associationId).toList();
    }

    public Page<Person> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Person> list(Pageable pageable, Specification<Person> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }
    
    public List<Person> getAllAsList() {
    	return repository.findAll();
    }
    
    public int getFreeMemberNumber(int associationId) {
    	
    	Random rand = new Random();
    	boolean isNotEqual = false;
    	int n = 0;
    	
		while (!isNotEqual) {
			n = rand.nextInt(1000000);
			for (Person p : findAllByAssociation(associationId)) {
				if (p.getMemberNumber() == n) {
					isNotEqual = false;
				}
			}
			isNotEqual = true;
		}
		
    	return n;
    }
}
