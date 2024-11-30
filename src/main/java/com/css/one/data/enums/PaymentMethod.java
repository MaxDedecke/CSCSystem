package com.css.one.data.enums;

public enum PaymentMethod {
	CASH("Barzahlung"),
	CARD("Kartenzahlung"),
	BANKTRANSACTION("Banküberweisung");

	public final String label;

    private PaymentMethod(String label) {
    	 this.label = label;
	}
    
    public String getLabel() {
    	return this.label;
    }
}
