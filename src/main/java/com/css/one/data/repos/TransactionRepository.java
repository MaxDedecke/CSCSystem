package com.css.one.data.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.css.one.data.Transaction;

public interface TransactionRepository extends  JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction>{

}
