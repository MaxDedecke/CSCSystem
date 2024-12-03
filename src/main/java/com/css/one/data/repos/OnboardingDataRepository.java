package com.css.one.data.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.css.one.data.OnboardingData;

public interface OnboardingDataRepository  extends JpaRepository<OnboardingData, Long>, JpaSpecificationExecutor<OnboardingData> {

}
