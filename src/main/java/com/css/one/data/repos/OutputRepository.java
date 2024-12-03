package com.css.one.data.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.css.one.data.Output;

public interface OutputRepository extends JpaRepository<Output, Long>, JpaSpecificationExecutor<Output> {

}
