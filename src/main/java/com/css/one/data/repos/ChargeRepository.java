package com.css.one.data.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.css.one.data.Charge;

public interface ChargeRepository extends JpaRepository<Charge, Long>, JpaSpecificationExecutor<Charge> {

}
