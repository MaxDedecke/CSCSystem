package com.css.one.migrations;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
	 private static final Properties properties = new Properties();
 
	 static {
		try (InputStream input = new FileInputStream(new File("/application.properties"))) {

			// Load the properties file
			properties.load(input);
		} catch (IOException ex) {
			try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
				if (input == null) {
					System.out.println("Sorry, unable to find application.properties");
					System.exit(1);
				}

				// Load the properties file
				properties.load(input);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	 }

	public static String getDbUrl() {	
		return properties.getProperty("spring.datasource.url");
	}

	public static String getDbUsername() {
		return properties.getProperty("spring.datasource.username");
	}

	public static String getDbPassword() {
		return properties.getProperty("spring.datasource.password");
	}
}
