package com.css.one.migrations;

import java.sql.Connection;
import java.sql.SQLException;

public class AddFirstPropertyManagementMigration {

	public AddFirstPropertyManagementMigration(Connection connection) {
		startMigration(connection);
	}
	
	public void startMigration(Connection connection) {

		var sql = "ALTER TABLE public.property_management_data DROP COLUMN property_management_id";

		try {
			var statement = connection.createStatement();
			statement.execute(sql);

			sql = "INSERT INTO public.property_management_data(\r\n"
					+ "	id, city, email, house_numbers, phone_number, postal_code, street_name)\r\n"
					+ "	VALUES (1, 'Regensburg', 'max@code-green-systems.de', '1A','+4917623264277', '93049', 'Maximilianstraße');";

			statement = connection.createStatement();
			statement.executeUpdate(sql);			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
