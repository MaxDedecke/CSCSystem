package com.css.one.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.css.one.data.MemberData;
import com.css.one.data.Person;
import com.css.one.data.WaitingPerson;
import com.css.one.data.repos.MemberDataRepository;

@Service
public class MemberDataService {
	private final MemberDataRepository repository;

    public MemberDataService(MemberDataRepository repository) {
        this.repository = repository;
    }

    public Optional<MemberData> get(Long id) {
        return repository.findById(id);
    }

    public MemberData update(MemberData entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<MemberData> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<MemberData> list(Pageable pageable, Specification<MemberData> filter) {
        return repository.findAll(filter, pageable);
    }
    
    public Optional<MemberData> findById(Long id) {
    	return repository.findById(id);
    }
    
    public Optional<MemberData> findByMember(WaitingPerson person) {
    	return repository.findById(person.getMemberData().getId());
    }
    
    public Optional<MemberData> findByMember(Person person) {
    	return repository.findById(person.getMemberData().getId());
    }
    
    public int count() {
        return (int) repository.count();
    }
    
    public List<MemberData> getAllAsList() {
    	return repository.findAll();
    }
}
