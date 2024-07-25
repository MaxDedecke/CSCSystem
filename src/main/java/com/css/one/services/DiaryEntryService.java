package com.css.one.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.css.one.data.DiaryEntry;
import com.css.one.data.DiaryEntryRepository;

@Service
public class DiaryEntryService {
	private final DiaryEntryRepository repository;

    public DiaryEntryService(DiaryEntryRepository repository) {
        this.repository = repository;
    }

    public Optional<DiaryEntry> get(Long id) {
        return repository.findById(id);
    }

    public DiaryEntry update(DiaryEntry entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public int count() {
        return (int) repository.count();
    }
    
    public List<DiaryEntry> findAllByAssociation(int associationId) {
    	List<DiaryEntry> locations = new ArrayList<>();
    	locations.addAll(repository.findAll().stream().filter(e -> e.getAssociationId() == associationId).toList());
    	return locations;
    }
}
