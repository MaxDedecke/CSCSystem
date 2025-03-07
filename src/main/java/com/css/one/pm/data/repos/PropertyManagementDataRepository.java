package com.css.one.pm.data.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.css.one.pm.data.PropertyManagementData;

public interface PropertyManagementDataRepository extends JpaRepository<PropertyManagementData, Long>, JpaSpecificationExecutor<PropertyManagementData>  {

}
