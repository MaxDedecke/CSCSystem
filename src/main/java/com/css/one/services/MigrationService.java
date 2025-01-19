package com.css.one.services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.css.one.data.SystemVersion;
import com.css.one.migrations.DB;

public class MigrationService {
	
	private final Logger logger = LoggerFactory.getLogger(MigrationService.class);

	private final String versionString= "0.7.6";
	private final int version = 76;
	
    List<SystemVersion> versions = new ArrayList<>();
		
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
		
		//loads all versions and caches them in versions list
		loadAndCacheVersionsFromDatabase(connection);	

		if(!versions.isEmpty()) {
			
			Optional<SystemVersion> currentActiveVersion = versions.stream().filter(version -> version.isActive()).findAny();
			
			logger.info("Starting migration service");
			
			if (currentActiveVersion.isPresent()) {
				//if final version has changed and is higher than active version in db
				if (currentActiveVersion.get().getVersionInteger() < version) {
					logger.error("------------- UPDATE INCOMING -------------");
					logger.error("------------- " + versionString + "-------------");
					
					//create new version in db
					createNewSystemVersion(connection);
					
					//set is_active = false of old active version
					outdateOldVersion(connection, currentActiveVersion.get());
					
				}
			} else {
				//if no current version is available - fallback to initial version
				logger.error("NO VERSION ACTIVE!");
				logger.error("FALLBACK TO DEFAULT VERSION!");
				versions.add(createInitialSystemVersion(connection));
			}

		} else {
			
			logger.error("NO VERSION ACTIVE!");
			logger.error("FALLBACK TO DEFAULT VERSION!");
			
			//If no version is existing because of blank database create initial one
			versions.add(createInitialSystemVersion(connection));
		}
		
		loadAndCacheVersionsFromDatabase(connection);
		
		//Take versions and proceed
		startMigrationsDependingOnVersion(versions, connection);
		
		logger.info("Migration process finished or skiped");
	}

	private void loadAndCacheVersionsFromDatabase(Connection connection) {
		
		//reset list
		versions = new ArrayList<>();
		
		var sql = "SELECT * FROM system_version";
        
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
	}

	private void outdateOldVersion(Connection connection, SystemVersion oldVersion) {

		//set is_active to false for 
		var sql = "UPDATE system_version SET is_active = false, updated_at = '" + LocalDate.now() + "'"
				+ " WHERE version_integer = " + oldVersion.getVersionInteger();

		try {
			var statement = connection.createStatement();
			statement.executeUpdate(sql);
			logger.info("Updated migration status of version: " + oldVersion.getVersionNumber());
		} catch (Exception e) {
			logger.error("FAILED UPDATING IS ACTIVE OF VERSION: " + oldVersion.getVersionNumber());
			logger.error(e.getMessage());
		}

		logger.info("Finished updating is_active of version: " + oldVersion.getVersionNumber());

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
				+ "id, created_at, description, is_active, release_date, updated_at, version_number, is_migrated, version_integer)\r\n"
				+ "	VALUES ("
				+ String.valueOf(versions.size() + 1) + ", " 
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
	
	private SystemVersion createNewSystemVersion(Connection connection) {

		// set version
		String versionAsString = this.versionString;
		int versionAsInteger = this.version;
		String defaultDescription = "New version";

		// create object
		SystemVersion version = new SystemVersion();
		version.setVersionNumber(versionAsString);
		version.setVersionInteger(versionAsInteger);
		version.setReleaseDate(LocalDate.now());
		version.setCreatedAt(LocalDate.now());
		version.setUpdatedAt(LocalDate.now());
		version.setDescription(defaultDescription);
		version.setActive(false);
		version.setMigrated(false);

		// save version
		var sql = "INSERT INTO public.system_version(\r\n"
				+ "id, created_at, description, is_active, release_date, updated_at, version_number, is_migrated, version_integer)\r\n"
				+ "	VALUES (" + String.valueOf(versions.size() + 1) + ", " + "'" + LocalDate.now() + "'" + ", " + "'Default inserted initial version'" + ", "
				+ "true" + ", " + "'" + LocalDate.now() + "'" + ", " + "null, " + "'" + versionAsString + "'" + ", "
				+ "false" + ", " + versionAsInteger + ");";

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
		
		if (version.getVersionInteger() == 76) {

			// add migrations to version 0.7.5 here

			// set is_migrated to true since all migrations where successful
			updateVersion(version, connection);

			// if version is not active and also not migrated, another version must be
			// active version
			if (version.isActive() == true) {
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
