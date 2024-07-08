package com.css.one.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RecurringPaymentRepository extends JpaRepository<RecurringPayment, Long>, JpaSpecificationExecutor<RecurringPayment> {

}
