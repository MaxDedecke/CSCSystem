package com.css.one.views;

import java.time.LocalDate;
import java.util.Optional;

import com.css.one.components.OnboardingWizzard;
import com.css.one.data.OnboardingToken;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;

public class PasswordView extends VerticalLayout implements BeforeEnterObserver {
	
	private static final long serialVersionUID = -7194925821596350470L;
	private String token;

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		token = event.getRouteParameters().get("token").orElse("");

        if (token.isBlank()) {
            // No Token
            event.forwardTo("login");
            Notification.show("Kein gültiger Onboarding Link!");
        } else {
        	//refresh URL
            getElement().executeJs("window.history.replaceState({}, '', window.location.pathname);");
            validateToken();
        }		
	}

	private void validateToken() {
//		Optional<OnboardingToken> optionalToken = onboardingTokenService.findByToken(token);
//		
//		if(optionalToken.isPresent()) {
//			LocalDate expirationDate = optionalToken.get().getExpirationDate();
//			onboardingToken = optionalToken.get();
//
//			if(LocalDate.now().isAfter(expirationDate)) {
//				UI.getCurrent().navigate("login");
//				onboardingDataService.delete(onboardingToken.getId());
//				onboardingToken = null;
//				Notification show = Notification.show("Onboarding Link abgelaufen. Kontaktiere den Support oder deinen Verein!");
//				show.addThemeVariants(NotificationVariant.LUMO_ERROR);
//			} else {
//				OnboardingWizzard wizzard = new OnboardingWizzard(onboardingDataService,
//						subscriptionModelService,
//						onboardingTokenService,
//						onboardingQuestionService,
//						memberDataService,
//						waitingPersonService,
//						Optional.of(onboardingToken),
//						Optional.empty());
//
//				add(wizzard);
//			}
//		} else {			
//			UI.getCurrent().navigate("login");
//			Notification show = Notification.show("Onboarding Link funktioniert nicht. Kontaktiere den Support oder deinen Verein!");
//			show.addThemeVariants(NotificationVariant.LUMO_ERROR);
//		}
	}
}