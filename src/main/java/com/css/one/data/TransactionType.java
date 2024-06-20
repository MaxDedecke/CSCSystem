package com.css.one.data;

public enum TransactionType {
	
	COST("Kosten"),
	INCOME("Einnahme");

	public final String label;

    private TransactionType(String label) {
    	 this.label = label;
	}
    
    public String getLabel() {
    	return this.label;
    }
    
    public String getDisplayName() {
    	return getLabel().equals("Kosten") ? "Kosten" : "Einnahme";
    }
}
