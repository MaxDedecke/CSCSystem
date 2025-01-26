package com.css.one.services;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.css.one.data.PasswordResetToken;
import com.css.one.data.repos.PasswordResetTokenRepository;

@Service
public class PasswordResetTokenService {
	  private final PasswordResetTokenRepository repository;

	    public PasswordResetTokenService(PasswordResetTokenRepository repository) {
	        this.repository = repository;
	    }

	    public Optional<PasswordResetToken> get(Long id) {
	        return repository.findById(id);
	    }

	    public PasswordResetToken update(PasswordResetToken entity) {
	        return repository.save(entity);
	    }

	    public void delete(Long id) {
	        repository.deleteById(id);
	    }

	    public int count() {
	        return (int) repository.count();
	    }
	    
	    public Optional<PasswordResetToken> findByToken(String token) {
	    	Optional<PasswordResetToken> optionalToken = repository.findAll().stream().filter(e -> e.getToken().equals(token)).findAny();
	    	return optionalToken;
	    }
	    
	    public String generateToken() {
	    	return UUID.randomUUID().toString();
	    }
}
