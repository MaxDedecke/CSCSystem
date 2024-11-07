package com.css.one.data;

public enum EmailType {
	
	ONBOARING("Onboarding", "classpath:email.html"),
	
	//for later use
	ONBOARDING_FINISHED("Onboarding finished", ""),
	RESET_PASSWORD("Reset password", ""),
	NEW_ROLE("New role", ""),
	PUSHBACK_TO_WAITINGLIST("Sent back member to waitinglist", "")
	;

	public final String label;
	public final String classPathTemplate;
	
	private EmailType(String string, String string2) {
		this.label = string;
		this.classPathTemplate = string2;
	}
	    
    public String getLabel() {
    	return this.label;
    }
    
    public String getHtml() {
    	return this.classPathTemplate;
    }
}
