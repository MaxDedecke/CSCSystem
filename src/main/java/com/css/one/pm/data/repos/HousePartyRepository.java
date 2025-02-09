package com.css.one.pm.data.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.css.one.pm.data.HouseParty;

public interface HousePartyRepository extends JpaRepository<HouseParty, Long>, JpaSpecificationExecutor<HouseParty> {

}
