package com.css.one.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.css.one.data.MemberSubscription;
import com.css.one.data.MemberSubscriptionRepository;
import com.css.one.data.Person;

@Service
public class MemberSubscriptionService {
	 private final MemberSubscriptionRepository repository;

	    public MemberSubscriptionService(MemberSubscriptionRepository repository) {
	        this.repository = repository;
	    }

	    public Optional<MemberSubscription> get(Long id) {
	        return repository.findById(id);
	    }

	    public MemberSubscription update(MemberSubscription entity) {
	        return repository.save(entity);
	    }

	    public void delete(Long id) {
	        repository.deleteById(id);
	    }

	    public Page<MemberSubscription> list(Pageable pageable) {
	        return repository.findAll(pageable);
	    }

	    public Page<MemberSubscription> list(Pageable pageable, Specification<MemberSubscription> filter) {
	        return repository.findAll(filter, pageable);
	    }

	    public int count() {
	        return (int) repository.count();
	    }
	    
	    public List<MemberSubscription> findAllByAssociation(int associationId) {
	    	return repository.findAll().stream().filter(e -> e.getAssociationId() == associationId).toList();
	    }
	    
	    public List<MemberSubscription> findByMonthAndYear(int month, int year, int associationId) {
			return findAllByAssociation(associationId).stream().filter(e -> e.getYear() == year && e.getMonth() == month).toList();
	    }
	    
	    public boolean needToCreateNewSubscriptions(int associationId, int year, int month) {
	    	
	    	if(findAllByAssociation(associationId).stream().filter(e -> e.getYear() == year && e.getMonth() == month).findAny().isPresent()) {
	    		return false;
	    	}
	    	return true;	    	
	    }
	    
	    public Optional<MemberSubscription> findSubscriptionByTransaction(int associationId, int transactionId) {	    	
	    	return findAllByAssociation(associationId).stream().filter(e -> e.getTransactionId() == transactionId).findAny();
	    }
	    
		public void createSubscriptionsForMonth(List<Person> members, int year, int month, int associationId) {

			members.forEach(e -> {
				MemberSubscription subscription = new MemberSubscription();
				subscription.setAssociationId(associationId);
				subscription.setMonth(month);
				subscription.setYear(year);
				subscription.setPersonId(e.getId().intValue());
				subscription.setPayed(false);

				update(subscription);
			});
	   }
}
