package com.css.one.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.css.one.data.LawInfo;
import com.css.one.data.repos.LawInfoRepository;

@Service
public class LawInfoService {
	
	private final LawInfoRepository repository;

    public LawInfoService(LawInfoRepository repository) {
        this.repository = repository;
    }

    public Optional<LawInfo> get(Long id) {
        return repository.findById(id);
    }

    public LawInfo update(LawInfo entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public int count() {
        return (int) repository.count();
    }
    
    public Optional<LawInfo> getByAssociation(int associationId) {
    	return repository.findAll().stream().filter(e -> e.getAssociation().getId().equals(Integer.toUnsignedLong(associationId))).findAny();
    }
}
