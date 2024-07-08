package com.css.one.data;

public enum Timezone {
	WEEKLY("Wöchentlich"), 
	MONTHLY("Monatlich"), 
	EVERYFOURMONTHS("Alle 3 Monate"), 
	HALFYEAR("Halbjährig"), 
	YEARLY("Jährlich");
	
	public final String label;

    private Timezone(String label) {
    	 this.label = label;
	}
    
    public String getLabel() {
    	return this.label;
    }
}
