package com.css.one.data;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class OnboardingAnswer {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
 	private Long id;

	private int associationId;
	
	private String answer;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	private OnboardingQuestion question;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getAssociationId() {
		return associationId;
	}

	public void setAssociationId(int associationId) {
		this.associationId = associationId;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public OnboardingQuestion getQuestion() {
		return question;
	}

	public void setQuestion(OnboardingQuestion question) {
		this.question = question;
	}
}
