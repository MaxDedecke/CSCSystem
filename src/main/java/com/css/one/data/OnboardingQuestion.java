package com.css.one.data;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class OnboardingQuestion {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

	private int associationId;
	
	private String question;
	
	@Nullable
	private String answer;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}
	
	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public int getAssociationId() {
		return associationId;
	}

	public void setAssociationId(int associationId) {
		this.associationId = associationId;
	}
}
