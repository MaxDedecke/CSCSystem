package com.css.one.data.enums;

public enum TimeDelcaration {
	
	DAY_MONDAY("Montag",1),
	DAY_TUESDAY("Dienstag",1),
	DAY_WENDSDAY("Mittwoch",1),
	DAY_THURSDAY("Donnerstag",1),
	DAY_FRIDAY("Freitag",1),
	DAY_SATURDAY("Samstag",1),
	DAY_SUNDAY("Sonntag",1),
	
	BEGIN_OF_MONTH("Anfang des Monats",2),
	MIDDEL_OF_MONTH("Mitte des Monats",2),
	END_OF_MONTH("Ende des Monats",2),
	
	START_JANUARY("Start Januar",3),
	START_FEBRUARY("Start Februar",3),
	START_MARCH("Start März",3),
	START_APRIL("Start April",3),
	START_MAI("Start Mai",3),
	START_JUNE("Start Juni",3),
	START_JULY("Start Juli",3),
	START_AUGUST("Start August",3),
	START_SEPTEMBER("Start September",3),
	START_OCTOBER("Start Oktober",3),
	START_NOVEMBER("Start November",3),
	START_DECEMBER("Start Dezember",3),
	
	JANUARY_JULY("Januar-Juli",4),
	FEBRUARY_AUGUST("Februar-August",4),
	MARCH_SEPTEMBER("März-September",4),
	APRIL_OKTOBER("April-Oktober",4),
	MAI_NOVEMBER("Mai-November",4),
	JUNE_DECEMBER("Juni-Dezember",4)
	;
	public final String label;
	public final int type;
	
    private TimeDelcaration(String label, int type) {
    	 this.label = label;
    	 this.type = type;
	}
    
    public String getLabel() {
    	return this.label;
    }
    
    public int getType() {
    	return this.type;
    }
}
