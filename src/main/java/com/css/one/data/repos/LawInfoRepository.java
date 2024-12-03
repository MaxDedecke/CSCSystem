package com.css.one.data.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.css.one.data.LawInfo;

public interface LawInfoRepository extends JpaRepository<LawInfo, Long>, JpaSpecificationExecutor<LawInfo> {

}
