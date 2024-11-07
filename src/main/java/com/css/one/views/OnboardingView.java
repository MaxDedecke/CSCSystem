package com.css.one.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

@PageTitle("Onboarding - Selbstauskunft")
@Route(value = "onboarding/:token?", layout = MainLayout.class)
@RouteAlias("onboarding")
@AnonymousAllowed
public class OnboardingView extends VerticalLayout implements BeforeEnterObserver {

	private static final long serialVersionUID = 1862806183284315642L;

	private TextField firstName;
	private TextField lastName;
	private TextField email;
	private TextField phone;
	private DatePicker dateOfBirth;
	
	private TextField streetName;
	private TextField streetNumber;
	private TextField postalCode;
	private TextField city;
	
	VerticalLayout stepOneWrapper = new VerticalLayout();
	VerticalLayout stepTwoWrapper = new VerticalLayout();
	VerticalLayout stepThreeWrapper = new VerticalLayout();

	public OnboardingView() {
		addClassNames(
				"onboaring-view",
				LumoUtility.Width.FULL, LumoUtility.Height.FULL
				);
		
		createStepOneLayout();
		createStepTwoLayout();
		createStepThreeLayout();
		
		TabSheet wizzard = new TabSheet();
		wizzard.addClassNames(LumoUtility.Margin.NONE, LumoUtility.AlignItems.CENTER);
		wizzard.setWidth("100%");
		wizzard.setHeightFull();
		
		wizzard.add("Schritt 1", stepOneWrapper);
		wizzard.add("Schritt 2", stepTwoWrapper);
		wizzard.add("Schritt 3", stepThreeWrapper);
		add(wizzard);
	}
	
	private void createStepThreeLayout() {
		stepThreeWrapper.addClassNames(LumoUtility.Margin.NONE, LumoUtility.AlignItems.CENTER, "rechtliches-box");
		H1 stepThreeHeading = new H1("Schritt 3: Einwilligungen");
		
		FormLayout dataLayout = new FormLayout();
		
//		dataLayout.add(firstName, lastName, dateOfBirth, phone, email);
		
		Button buttonConfirmStepThree = new Button("bestätigen und abschließen");
		buttonConfirmStepThree.addClassName("save-button");
		VerticalLayout buttonWrapper = new VerticalLayout();
		
		buttonWrapper.addClassNames(LumoUtility.AlignItems.END);
		buttonWrapper.add(buttonConfirmStepThree);	
		
		stepThreeWrapper.add(stepThreeHeading, dataLayout, buttonWrapper);	
		
	}

	private void createStepOneLayout() {

		stepOneWrapper.addClassNames(LumoUtility.Margin.NONE, LumoUtility.AlignItems.CENTER, "rechtliches-box");
		H1 stepOneHeading = new H1("Schritt 1: Angaben zu deiner Person");
		
		FormLayout dataLayout = new FormLayout();
		
		firstName = new TextField("Vorname");
		lastName = new TextField("Nachname");
		email = new TextField("Email");
		phone = new TextField("Telefonnummer");
        phone.setAllowedCharPattern("[0-9/]");
		dateOfBirth = new DatePicker("Geburtstag");
		dateOfBirth.setOverlayClassName("waiting-list-view-date-picker-1");
		dateOfBirth.addClassName("waiting-list-view-date-picker-1");
		
		dataLayout.add(firstName, lastName, dateOfBirth, phone, email);
		dataLayout.setColspan(email, 2);
		
		streetName = new TextField("Straße");	
		streetNumber = new TextField("Hausnummer");
		postalCode = new TextField("PLZ");
		city = new TextField("Ort");
		
		Button buttonConfirmStepOne = new Button("weiter zu Schritt 2");
		buttonConfirmStepOne.addClassName("save-button");
		VerticalLayout buttonWrapper = new VerticalLayout();
		
		buttonWrapper.addClassNames(LumoUtility.AlignItems.END);
		buttonWrapper.add(buttonConfirmStepOne);	
		
		stepOneWrapper.add(stepOneHeading, dataLayout, buttonWrapper);	
	}
	
	private void createStepTwoLayout() {
		stepTwoWrapper.addClassNames(LumoUtility.Margin.NONE, LumoUtility.AlignItems.CENTER, "rechtliches-box");
		
		H1 stepTwoHeading = new H1("Schritt 2: Angaben zu deinem Wohnort");

		FormLayout dataLayout = new FormLayout();
		
		streetName = new TextField("Straße");	
		streetNumber = new TextField("Hausnummer");
		postalCode = new TextField("PLZ");
		city = new TextField("Ort");
		
		dataLayout.add(streetName, streetNumber, postalCode, city);
		dataLayout.setColspan(streetName, 2);
		dataLayout.setColspan(city, 2);

		Button buttonConfirmStepTwo = new Button("weiter zu Schritt 3");
		buttonConfirmStepTwo.addClassName("save-button");
		VerticalLayout buttonWrapper = new VerticalLayout();
		
		buttonWrapper.addClassNames(LumoUtility.AlignItems.END);
		buttonWrapper.add(buttonConfirmStepTwo);	
		
		stepTwoWrapper.add(stepTwoHeading, dataLayout, buttonWrapper);
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		// get token
		String token = event.getRouteParameters().get("token")
//        		.orElse("Kein token vorhanden!");
				.orElse("");

		if (!token.equals("")) {
			Notification notification = Notification.show(token);
			notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
		}

	}
}
