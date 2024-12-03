package com.css.one.data.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.css.one.data.Cutting;

public interface CuttingRepository extends JpaRepository<Cutting, Long>, JpaSpecificationExecutor<Cutting> {

}
