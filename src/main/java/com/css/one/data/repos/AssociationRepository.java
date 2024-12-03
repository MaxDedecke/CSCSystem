package com.css.one.data.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.css.one.data.Association;

public interface AssociationRepository extends JpaRepository<Association, Long>, JpaSpecificationExecutor<Association> {

}
