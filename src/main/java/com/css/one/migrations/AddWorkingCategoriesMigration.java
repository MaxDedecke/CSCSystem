package com.css.one.migrations;

import java.sql.Connection;
import java.sql.SQLException;

public class AddWorkingCategoriesMigration {
	
	public AddWorkingCategoriesMigration(Connection connection) {
		startMigration(connection);
	}
	
	public void startMigration(Connection connection) {
		
		 var sql = "INSERT INTO working_unit_category(id, name, association_id)"
	                + "VALUES(0, 'Allgemein', 0)  ON CONFLICT DO NOTHING";
		 
		try {
			var statement = connection.createStatement();
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
}
