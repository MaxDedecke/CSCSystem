package com.css.one.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.css.one.views.warenlager.WarenlagerView;

public class PropertyService {

	final static Properties properties = new Properties();
	final static Properties onboaringProperties = new Properties();
	
	public PropertyService() {
		getPropertiesInitial();
	}
	
	private static Properties getPropertiesInitial() {

		try (InputStream input = new FileInputStream(new File("/application.properties"))) {
			// Load the properties file
			properties.load(input);
		} catch (IOException ex) {
			try (InputStream input = WarenlagerView.class.getClassLoader()
					.getResourceAsStream("application.properties")) {
				if (input == null) {
					System.out.println("Unable to find application.properties");
					System.exit(1);
				}
				// Load the properties file
				properties.load(input);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return properties;
	}
	
	public static Properties getProperties() {
		return properties;
	}
}
