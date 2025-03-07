package com.css.one.pm.data.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.css.one.pm.data.HouseUnit;

public interface HouseUnitRepository extends JpaRepository<HouseUnit, Long>, JpaSpecificationExecutor<HouseUnit> {

}
