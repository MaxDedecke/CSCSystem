package com.css.one.data.enums;

public enum EmailType {
	
	ONBOARING("Onboarding", "classpath:emailStartOnboarding.html"),
	ONBOARDING_DATA_FINISHED("Data input finished", "classpath:emailOnboardingDataFinished.html"),
	ONBOARDING_LINK_DATE_RESET("Onboarding link date reset", "classpath:emailOnboardingDateReset.html"),
	
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
