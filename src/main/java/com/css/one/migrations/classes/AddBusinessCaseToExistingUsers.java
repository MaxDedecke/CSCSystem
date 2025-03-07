package com.css.one.migrations.classes;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddBusinessCaseToExistingUsers {
	private final Logger logger = LoggerFactory.getLogger(SetInitialUserRoles.class);

	public AddBusinessCaseToExistingUsers(Connection connection) {
		startMigration(connection);
	}
	
	public void startMigration(Connection connection) {
		
		//first get ids of all application_user to find out who needs role
		//then insert new entry in user_roles for corresponding id of before found application_user
		logger.info("Add business case to existing users");
		
		//Step 1: get ids of users
		var sqlSelectIds = "UPDATE application_user SET business_case = 0";

		try {
			var statement = connection.createStatement();
			statement.execute(sqlSelectIds);
			
			
			logger.info("Add business case to existing users migration finished successfuly");

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
