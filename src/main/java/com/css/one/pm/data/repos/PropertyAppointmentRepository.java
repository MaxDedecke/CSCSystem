package com.css.one.pm.data.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.css.one.pm.data.PropertyAppointment;

public interface PropertyAppointmentRepository extends JpaRepository<PropertyAppointment, Long>, JpaSpecificationExecutor<PropertyAppointment> {

}
