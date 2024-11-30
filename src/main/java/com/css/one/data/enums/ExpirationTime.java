package com.css.one.data.enums;

public enum ExpirationTime {
		FIVEDAYS("5 Tage"),
		ONE_WEEK("Eine Woche"),
		TWO_WEEKS("Zwei Wochen"),
		ONE_MONTH("Einen Monat"),
		THREE_MONTHS("3 Monate"),
		HALF_YEAR("Halbes Jahr");
	
	public final String label;
	
	private ExpirationTime(String string) {
		this.label = string;
	}
	
	 public String getLabel() {
	    	return this.label;
	    }
}
