package com.css.one.services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.css.one.data.SystemVersion;
import com.css.one.migrations.DB;

public class MigrationService {
	
	private final Logger logger = LoggerFactory.getLogger(MigrationService.class);

		
	public MigrationService() {
		
		//Create a connection to database
		 try (var connection =  DB.connect()){
	            System.out.println("Connected to the PostgreSQL database.");
	            startMigration(connection);
	        } catch (SQLException e) { 
	            System.err.println(e.getMessage());
	        }
	}

	public void startMigration(Connection connection) {

		var sql = "SELECT * FROM system_version";
		
        List<SystemVersion> versions = new ArrayList<>();
        
        //Get all versions from database
        
		try {
			var statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(sql);

			while (rs.next()) {
				SystemVersion version = new SystemVersion();
				version.setId(rs.getLong("id"));
				version.setVersionNumber(rs.getString("version_number"));
				version.setVersionInteger(rs.getInt("version_integer"));
				version.setReleaseDate(rs.getObject("release_date", LocalDate.class));
				version.setCreatedAt(rs.getObject("created_at", LocalDate.class));
				version.setUpdatedAt(rs.getObject("updated_at", LocalDate.class));
				version.setDescription(rs.getString("description"));
				version.setActive(rs.getBoolean("is_active"));
				version.setMigrated(rs.getBoolean("is_migrated"));
				versions.add(version);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		

		if(!versions.isEmpty()) {
			
			logger.info("Starting migration process");
			
		} else {
			logger.error("NO VERSION ACTIVE!");
			logger.error("FALLBACK TO DEFAULT VERSION!");
			
			//If no version is existing because of blank database create initial one
			versions.add(createInitialSystemVersion(connection));
			
			
		}
		
		//Take versions and proceed
		startMigrationsDependingOnVersion(versions, connection);
		
		logger.info("Migration process finished or skiped");
	}

	private SystemVersion createInitialSystemVersion(Connection connection) {
		
		//set version
		String versionAsString = "0.7.5";
		int versionAsInteger = 75;
		String defaultDescription = "Default inserted initial version";
		
		//create object
		SystemVersion version = new SystemVersion();
		version.setVersionNumber(versionAsString);
		version.setVersionInteger(versionAsInteger);
		version.setReleaseDate(LocalDate.now());
		version.setCreatedAt(LocalDate.now());
		version.setUpdatedAt(LocalDate.now());
		version.setDescription(defaultDescription);
		version.setActive(false);
		version.setMigrated(false);	
		
		//save version
		var sql = "INSERT INTO public.system_version(\r\n"
				+ "	created_at, description, is_active, release_date, updated_at, version_number, is_migrated, version_integer)\r\n"
				+ "	VALUES ("
				+ "'" +  LocalDate.now() + "'" + ", "
				+ "'Default inserted initial version'" + ", "
				+ "true" + ", " 
				+ "'" + LocalDate.now() + "'" + ", "
				+ "null, "
				+ "'" + versionAsString + "'" + ", "
				+ "false" + ", "
				+ versionAsInteger + ");";
		
		try {
			var statement = connection.createStatement();
			statement.executeUpdate(sql);
			logger.info("Updated migration status of version: " + versionAsString);
		} catch (Exception e) {
			logger.error("FAILED UPDATING MIGRATION STATUS OF VERSION: " + versionAsString);
			logger.error(e.getMessage());
		}
		
		return version;
	}

	private void startMigrationsDependingOnVersion(List<SystemVersion> versions, Connection connection) {
		
		//Sort the versions so that the current version is the LAST element in list
		Collections.sort(versions, Comparator.comparingInt(SystemVersion::getVersionInteger));

		//for each version
		for(SystemVersion version : versions) {
					
			//During a migration, something can go wrong
			//If so, versions isMigrated field is left false and a fallback to the last migrated version should happen
			//When the migration starts, it needs to start on the last not migrated version to provide integrity
			
			if(!version.isMigrated()) {
				
				logger.info("Starting at with version: " + version.getVersionNumber());

				startMigrationProcess(version, versions, connection);
			}	
		}
	}

	private void startMigrationProcess(SystemVersion version, List<SystemVersion> versions, Connection connection) {
		
		//
		//Migrations of 0.X - Early stage
		//
		
		//If version is 0.7.5
		if(version.getVersionInteger() == 75) {
			
			//add migrations to version 0.7.5 here
			
			
			//set is_migrated to true since all migrations where successful
			updateVersion(version, connection);
			
			//if version is not active and also not migrated, another version must be active version
			if(version.isActive() == true) {
				return;
			}
		}
		
		
		//
		//Migrations of 1.X - first productive stage
		//	
		if(version.getVersionInteger() >= 100) {
			
		}
	}

	private void updateVersion(SystemVersion version, Connection connection) {
		
		var sql = "UPDATE system_version SET is_migrated = true, updated_at = '" + LocalDate.now() + "'"
						+ " WHERE version_integer = " + version.getVersionInteger();

		try {
			var statement = connection.createStatement();
			statement.executeUpdate(sql);
			logger.info("Updated migration status of version: " + version.getVersionNumber());
		} catch (Exception e) {
			logger.error("FAILED UPDATING MIGRATION STATUS OF VERSION: " + version.getVersionNumber());
			logger.error(e.getMessage());
		}
		
		logger.info("Finished updating migration status of version: " + version.getVersionNumber());
	}
}
