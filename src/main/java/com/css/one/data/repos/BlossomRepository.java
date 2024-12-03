package com.css.one.data.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.css.one.data.Blossom;

public interface BlossomRepository extends JpaRepository<Blossom, Long>, JpaSpecificationExecutor<Blossom> {
}
