package com.css.one.data.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.css.one.data.SystemVersion;

public interface SystemVersionRepository extends JpaRepository<SystemVersion, Long>, JpaSpecificationExecutor<SystemVersion> {

}
