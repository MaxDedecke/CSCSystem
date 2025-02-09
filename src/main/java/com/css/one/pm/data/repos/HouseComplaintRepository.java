package com.css.one.pm.data.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.css.one.pm.data.HouseComplaint;

public interface HouseComplaintRepository extends JpaRepository<HouseComplaint, Long>, JpaSpecificationExecutor<HouseComplaint> {

}
