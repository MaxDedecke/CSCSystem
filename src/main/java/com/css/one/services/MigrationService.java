package com.css.one.services;

public class MigrationService {

	private int MAJOR = 0;
	private int MINOR1 = 0;
	private int MINOR2 = 1;
	
	public String VERSION_STRING = MAJOR + "." + MINOR1 + "." + MINOR2;
	
	public MigrationService() {
	
	}

	public void startMigration() {
		
		if(MAJOR >= 0) {
			
			if(MINOR1 >= 0) {
				
				if(MINOR2 >= 1) {
					
				}
			}
		}
		
//		if(MAJOR >= 1) {
//			
//		}
	}
}
