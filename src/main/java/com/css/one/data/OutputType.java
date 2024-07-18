package com.css.one.data;

public enum OutputType {
	
	BLOSSOM("Blüten"),
	CUTTING("Steckling"),
	SEED("Samen");
	
	public final String label;

    private OutputType(String label) {
    	 this.label = label;
	}
    
    public String getLabel() {
    	return this.label;
    }
}
