package com.css.one.data.enums;

public enum AssociationRole {
	MEMBER("Mitglied"), 
	ACCOUNTANT("Kassenwart"), 
	BOARD("Vorstandsmitglied"), 
	PREVENTION("Präventionsbeauftragte(r)");
	
	public final String label;

    private AssociationRole(String label) {
    	 this.label = label;
	}
    
    public String getLabel() {
    	return this.label;
    }
}
