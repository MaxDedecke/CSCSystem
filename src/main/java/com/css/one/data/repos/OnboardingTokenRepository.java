package com.css.one.data.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.css.one.data.OnboardingToken;

public interface OnboardingTokenRepository extends JpaRepository<OnboardingToken, Long>, JpaSpecificationExecutor<OnboardingToken> {

}
