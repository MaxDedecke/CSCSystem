package com.css.one.data.enums;

public enum ExpirationTime {
		FIVEDAYS("5 Tage", 5),
		ONE_WEEK("Eine Woche", 7),
		TWO_WEEKS("Zwei Wochen", 14),
		ONE_MONTH("Einen Monat", 30),
		THREE_MONTHS("3 Monate", 90),
		HALF_YEAR("Halbes Jahr", 180);
	
	public final String label;
	public final int daysUntilExpiration;
	
	private ExpirationTime(String string, int days) {
		this.label = string;
		this.daysUntilExpiration = days;
	}
	
	 public String getLabel() {
	    	return this.label;
	 }
	 
	 public int getDayUntilExpiration() {
		 return daysUntilExpiration;
	 }
}
