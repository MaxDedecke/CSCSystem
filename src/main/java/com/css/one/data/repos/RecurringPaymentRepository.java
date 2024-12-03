package com.css.one.data.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.css.one.data.RecurringPayment;

public interface RecurringPaymentRepository extends JpaRepository<RecurringPayment, Long>, JpaSpecificationExecutor<RecurringPayment> {

}
