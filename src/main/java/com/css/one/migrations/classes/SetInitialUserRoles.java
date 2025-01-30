package com.css.one.migrations.classes;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetInitialUserRoles {

	private final Logger logger = LoggerFactory.getLogger(SetInitialUserRoles.class);

	public SetInitialUserRoles(Connection connection) {
		startMigration(connection);
	}
	
	public void startMigration(Connection connection) {
		
		//first get ids of all application_user to find out who needs role
		//then insert new entry in user_roles for corresponding id of before found application_user
		logger.info("Set initial user roles migration started");
		
		//Step 1: get ids of users
		var sqlSelectIds = "SELECT id FROM application_user";

		try {
			var statement = connection.createStatement();
			ResultSet ids = statement.executeQuery(sqlSelectIds);

			// Step 2: Insert if not exists
			//for each id
			while (ids.next()) {
				
				//get actual id
				String idAsString = String.valueOf(ids.getLong("id"));
				
				var sqlInsert = 
						"INSERT INTO user_roles (user_id, roles) \r\n" 
						+ "SELECT '" + idAsString + "', 'ADMIN' \r\n"
						+ "WHERE NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = " + idAsString + ");";
				
				var insertStatement = connection.createStatement();
				insertStatement.execute(sqlInsert);
			}
			
			
			logger.info("Set initial user roles migration finished successfuly");

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
