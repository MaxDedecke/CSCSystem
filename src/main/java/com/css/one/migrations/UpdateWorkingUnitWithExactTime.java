package com.css.one.migrations;

import java.sql.Connection;
import java.sql.SQLException;

public class UpdateWorkingUnitWithExactTime {
 
	
	public UpdateWorkingUnitWithExactTime(Connection connection) {
		startMigration(connection);
	}
	
	public void startMigration(Connection connection) {
		
		 var sql1 = "ALTER TABLE working_unit ADD COLUMN IF NOT EXISTS hour_begin INTEGER NOT NULL DEFAULT 1";
		 var sql2 = "ALTER TABLE working_unit ADD COLUMN IF NOT EXISTS minute_begin INTEGER NOT NULL DEFAULT 1";
		 
		 
		 var sql3 = "ALTER TABLE working_unit ADD COLUMN IF NOT EXISTS hour_end INTEGER DEFAULT 0";
		 var sql4 = "ALTER TABLE working_unit ADD COLUMN IF NOT EXISTS minute_end INTEGER DEFAULT 0";
		 
		try {
			var statement = connection.createStatement();
			statement.executeUpdate(sql1);
			statement.executeUpdate(sql2);

			statement.executeUpdate(sql3);
			statement.executeUpdate(sql4);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
