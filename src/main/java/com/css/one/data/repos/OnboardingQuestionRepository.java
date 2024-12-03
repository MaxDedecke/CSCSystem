package com.css.one.data.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.css.one.data.OnboardingQuestion;

public interface OnboardingQuestionRepository extends JpaRepository<OnboardingQuestion, Long>, JpaSpecificationExecutor<OnboardingQuestion> {

}
