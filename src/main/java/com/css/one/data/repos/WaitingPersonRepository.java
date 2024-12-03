package com.css.one.data.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.css.one.data.WaitingPerson;

public interface WaitingPersonRepository
		extends JpaRepository<WaitingPerson, Long>, JpaSpecificationExecutor<WaitingPerson> {

}
