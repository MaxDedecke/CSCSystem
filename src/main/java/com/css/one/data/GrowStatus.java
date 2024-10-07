package com.css.one.data;

public enum GrowStatus {
	
	SPROUTING("Keimen"),
	NEW_PLANTED("Neu gepflanzt"), 
	GROWING("Im Wachstum"), 
	READY("Erntereif"),
	HARVESTED("Verarbeitet"),
	VERIFYING("Im Labor"),
	OUTPUT_READY("Ausgabebereit"),
	DESTORYED("Vernichtet");
	
	public final String label;

    private GrowStatus(String label) {
    	 this.label = label;
	}
    
    public String getLabel() {
    	return this.label;
    }
}
