package com.css.one.data;

public enum GrowStatus {
	NEW("Neu gepflanzt"), 
	GROWING("Im Wachstum"), 
	READY("Erntereif"),
	HARVESTED("Geerntet");
	
	public final String label;

    private GrowStatus(String label) {
    	 this.label = label;
	}
    
    public String getLabel() {
    	return this.label;
    }
}
