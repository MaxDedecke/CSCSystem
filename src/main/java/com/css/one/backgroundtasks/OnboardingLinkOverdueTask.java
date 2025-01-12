package com.css.one.backgroundtasks;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.css.one.data.Association;
import com.css.one.data.AssociationSettings;
import com.css.one.data.enums.ExpirationTime;
import com.css.one.services.AssociationService;
import com.css.one.services.AssociationSettingsService;
import com.css.one.services.EmailService;

@Component
public class OnboardingLinkOverdueTask {
	
	@Autowired
	private AssociationSettingsService associationSettingsService;

	@Autowired
	private AssociationService associationService;

	private EmailService emailService;
	
	 //Email-Benachrichtigung senden und Link zurücksetzen, wenn Onboarding Link abgelaufen überschritten wurde
	//@Scheduled(cron = "0 0/30 20-21 * * ?") // Jeden tag um 21:00
	@Scheduled(cron = "0 0 21 * * ?") // Jeden tag um 21:00
    public void checkForOnboardingLingOverdueTasks() {
		
        // Logik, um überfällige Tasks zu überprüfen
        System.out.println("Clean up service - Überprüfung der Onboarding Links gestartet..");
        
        emailService = new EmailService();
        
        checkLinks();
    }

	private void checkLinks() {
		
		// Alle settings mit spezifischem onboarding link Ablaufdatum
		List<AssociationSettings> allExistingAssociationSettings = associationSettingsService.findWithSpecificOnboardingDuedate();
		
		
		for(AssociationSettings settings : allExistingAssociationSettings) {
			
			// Wenn gesetzes Datum bereits vorüber
			if(settings.getOnboardingTokenExpirationDate().isBefore(LocalDate.now())) {
				
				Optional<Association> optAssociation = associationService.get(Integer.toUnsignedLong(settings.getAssociationId()));
				
				if(optAssociation.isPresent()) {
					
					try {

						if (optAssociation.get().getEmail() != null) {
							// Verein informieren
							emailService.sendOnboardingLinkOverdueEmail(optAssociation.get().getEmail(),
									optAssociation.get().getName(), settings.getOnboardingTokenExpirationDate());
							
						} else {

							// TODO service email einfügen
							emailService.sendOnboardingLinkOverdueEmail("jm.dedecke@gmail.com",
									optAssociation.get().getName(), settings.getOnboardingTokenExpirationDate());
						}
						
						//Datum entfernen und Zeitraum auf 2 Wochen setzen
						settings.setOnboardingTokenExpirationDate(null);
						settings.setOnboardingTokenExpirationTime(ExpirationTime.TWO_WEEKS);
						
						associationSettingsService.update(settings);
						
					} catch (Exception e) {
						System.out.println("Es ist ein Fehler beim Ausführen der Onboarding Link check routine aufgetreten!");
						System.out.println(e.getMessage());
					}
				} 
			}
		}
		
		System.out.println("Clean up service - Überprüfung der Onboarding Links angeschlossen.");
	}
}
