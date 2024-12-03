package com.css.one.data.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.css.one.data.MemberData;

public interface MemberDataRepository extends JpaRepository<MemberData, Long>, JpaSpecificationExecutor<MemberData> {

}
