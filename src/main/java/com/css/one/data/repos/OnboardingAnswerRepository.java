package com.css.one.data.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.css.one.data.OnboardingAnswer;

public interface OnboardingAnswerRepository extends JpaRepository<OnboardingAnswer, Long>, JpaSpecificationExecutor<OnboardingAnswer>{

}
