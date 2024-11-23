package com.css.one.migrations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.css.one.services.PropertyService;

public class DB {
	 public static Connection connect() throws SQLException {

	        try {
				final Properties properties = PropertyService.getProperties();
	            // Get database credentials from DatabaseConfig class
	            var jdbcUrl = properties.getProperty("spring.datasource.url");
	            var user = properties.getProperty("spring.datasource.username");
	            var password = properties.getProperty("spring.datasource.password");

	            // Open a connection
	            return DriverManager.getConnection(jdbcUrl, user, password);

	        } catch (SQLException  e) {
	            System.err.println(e.getMessage());
	            return null;
	        }
	    }
}
