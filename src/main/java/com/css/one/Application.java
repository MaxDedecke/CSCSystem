package com.css.one;

import com.css.one.data.repos.PersonRepository;
import com.css.one.services.MigrationService;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.theme.Theme;
import javax.sql.DataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.sql.init.SqlDataSourceScriptDatabaseInitializer;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *  
 */
@SpringBootApplication
@EnableScheduling
@Theme("css-system-one")
public class Application implements AppShellConfigurator { 

    private static final long serialVersionUID = 3173515292498804205L;

    private static ConfigurableApplicationContext applicationContext;
    
	public static void main(String[] args) {
        applicationContext = SpringApplication.run(Application.class, args);            
    }
	
	@Override
	  public void configurePage(AppShellSettings settings) {

	    settings.addFavIcon("icon", "logoCodeGreen.png", "192x192");
	    settings.addLink("shortcut icon", "logoCodeGreen.png");
	}

    @Bean
    SqlDataSourceScriptDatabaseInitializer dataSourceScriptDatabaseInitializer(DataSource dataSource,
            SqlInitializationProperties properties, PersonRepository repository) {
        // This bean ensures the database is only initialized when empty
        return new SqlDataSourceScriptDatabaseInitializer(dataSource, properties) {
            @Override
            public boolean initializeDatabase() { 
            	
                if (repository.count() == 0L) {
                    return super.initializeDatabase();
                }     
                
                try {
                	//if something goes wrong during fallback of migration process, the app shuts down to not have undefinded behavior
					checkMigrationStatus();
				} catch (Exception e) {
					SpringApplication.exit(applicationContext, () -> 0);
				}
                
                return false;
            }

			private void checkMigrationStatus() throws Exception {
				new MigrationService();
			}
        };
    }
}
