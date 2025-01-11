package com.css.one.views;

import java.time.LocalDate;
import java.util.Optional;

import com.css.one.components.OnboardingWizzard;
import com.css.one.data.OnboardingToken;
import com.css.one.services.MemberDataService;
import com.css.one.services.OnboardingDataService;
import com.css.one.services.OnboardingQuestionService;
import com.css.one.services.OnboardingTokenService;
import com.css.one.services.SubscriptionModelService;
import com.css.one.services.WaitingPersonService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

@PageTitle("Onboarding - Selbstauskunft")
@Route(value = "onboarding/:token", layout = MainLayout.class)
@RouteAlias("onboarding")
@AnonymousAllowed
public class OnboardingView extends VerticalLayout implements BeforeEnterObserver{

	private static final long serialVersionUID = 1862806183284315642L;
	
	private String token;
	private OnboardingToken onboardingToken;

	VerticalLayout beginWrapper = new VerticalLayout();
	VerticalLayout stepOneWrapper = new VerticalLayout();
	VerticalLayout stepTwoWrapper = new VerticalLayout();
	VerticalLayout stepThreeWrapper = new VerticalLayout();
	VerticalLayout stepFourWrapper = new VerticalLayout();
	VerticalLayout questionsWrapper = new VerticalLayout();
	VerticalLayout endWrapper = new VerticalLayout();

	private final OnboardingDataService onboardingDataService;
	private final SubscriptionModelService subscriptionModelService;
	private final OnboardingTokenService onboardingTokenService;
	private final OnboardingQuestionService onboardingQuestionService;
	private final MemberDataService memberDataService;
	private final WaitingPersonService waitingPersonService;
	
	public OnboardingView(
			OnboardingDataService onboardingDataService,
			SubscriptionModelService subscriptionModelService,
			OnboardingTokenService onboardingTokenService,
			OnboardingQuestionService onboardingQuestionService,
			MemberDataService memberDataService,
			WaitingPersonService waitingPersonService) {

		addClassNames("onboarding-view", LumoUtility.Padding.NONE);
		
		this.onboardingDataService = onboardingDataService;
		this.subscriptionModelService = subscriptionModelService;
		this.onboardingTokenService = onboardingTokenService;
		this.onboardingQuestionService = onboardingQuestionService;
		this.memberDataService = memberDataService;
		this.waitingPersonService = waitingPersonService;	
		
		setWidth("100%");
	}
	
	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		//takes token + URL refresh or redirect to login	
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
		Optional<OnboardingToken> optionalToken = onboardingTokenService.findByToken(token);
		
		if(optionalToken.isPresent()) {
			LocalDate expirationDate = optionalToken.get().getExpirationDate();
			onboardingToken = optionalToken.get();

			if(LocalDate.now().isAfter(expirationDate)) {
				UI.getCurrent().navigate("login");
				onboardingDataService.delete(onboardingToken.getId());
				onboardingToken = null;
				Notification show = Notification.show("Onboarding Link abgelaufen. Kontaktiere den Support oder deinen Verein!");
				show.addThemeVariants(NotificationVariant.LUMO_ERROR);
			} else {
				OnboardingWizzard wizzard = new OnboardingWizzard(onboardingDataService,
						subscriptionModelService,
						onboardingTokenService,
						onboardingQuestionService,
						memberDataService,
						waitingPersonService,
						Optional.of(onboardingToken),
						Optional.empty());

				add(wizzard);
			}
		} else {			
			UI.getCurrent().navigate("login");
			Notification show = Notification.show("Onboarding Link funktioniert nicht. Kontaktiere den Support oder deinen Verein!");
			show.addThemeVariants(NotificationVariant.LUMO_ERROR);
		}
	}

}
