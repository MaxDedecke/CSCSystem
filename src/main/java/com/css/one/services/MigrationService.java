package com.css.one.services;

import java.sql.Connection;
import java.sql.SQLException;

import com.css.one.migrations.AddWorkingCategoriesMigration;
import com.css.one.migrations.DB;
import com.css.one.migrations.UpdateWorkingUnitWithExactTime;

public class MigrationService {

	private int MAJOR = 0;
	private int MINOR1 = 0;
	private int MINOR2 = 2;
	
	public String VERSION_STRING = MAJOR + "." + MINOR1 + "." + MINOR2;
	
	public MigrationService() {
		 try (var connection =  DB.connect()){
	            System.out.println("Connected to the PostgreSQL database.");
	            startMigration(connection);
	        } catch (SQLException e) { 
	            System.err.println(e.getMessage());
	        }
	}

	public void startMigration(Connection connection) {
		
		if(MAJOR >= 0) {
			
			if(MINOR1 >= 0) {
				
				if(MINOR2 >= 1) {
					new AddWorkingCategoriesMigration(connection);
					new UpdateWorkingUnitWithExactTime(connection);
				}
				
			}
		}
	}
}
