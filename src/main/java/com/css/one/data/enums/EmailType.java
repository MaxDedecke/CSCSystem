package com.css.one.data.enums;

public enum EmailType {
	
	ONBOARING("Onboarding", "classpath:emailStartOnboarding.html"),
	ONBOARDING_DATA_FINISHED("Data input finished", "classpath:emailOnboardingDataFinished.html"),
	ONBOARDING_LINK_DATE_RESET("Onboarding link date reset", "classpath:emailOnboardingDateReset.html"),
	
	SET_INITIAL_PASSWORD("Set first user account password", "classpath:emailNewUserInitialPassword.html"),
	NEW_INITIAL_MEMBER("New Member with initial data", "classpath:emailNewInitialMember.html"),
	
	//for later use
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
