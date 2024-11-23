package com.css.one.views;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.css.one.data.OnboardingToken;
import com.css.one.data.SubscriptionModel;
import com.css.one.services.OnboardingDataService;
import com.css.one.services.OnboardingTokenService;
import com.css.one.services.SubscriptionModelService;
import com.css.one.services.WaitingPersonService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
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
@Route(value = "onboarding/:token", layout = MainLayout.class)
@RouteAlias("onboarding")
@AnonymousAllowed
public class OnboardingView extends VerticalLayout implements BeforeEnterObserver{

	private static final long serialVersionUID = 1862806183284315642L;
	
	private TabSheet wizzard = new TabSheet();
	
	private String token;
	private OnboardingToken onboardingToken;
	
	private TextField firstName;
	private TextField lastName;
	private TextField email;
	private TextField phone;
	private DatePicker dateOfBirth;
	private Checkbox confirmAgeBox;
	private Button buttonConfirmStepOne = new Button("weiter zu Schritt 2");
	
	private TextField streetName;
	private TextField streetNumber;
	private TextField postalCode;
	private TextField city;
	private Checkbox confirmAddressBox;
	private Checkbox confirmGeneralDataBox;
	private Button buttonConfirmStepTwo = new Button("weiter zu Schritt 3");
	
	private Checkbox confirmExclusiveMemberShipBox = new Checkbox();
	private Checkbox confirmDataUsageBox = new Checkbox();

	private Tab tabBegin;
	private Tab tabStepOne;
	private Tab tabStepTwo;
	private Tab tabStepThree;
	private Tab tabStepFour;
	private Tab tabEnd;

	VerticalLayout beginWrapper = new VerticalLayout();
	VerticalLayout stepOneWrapper = new VerticalLayout();
	VerticalLayout stepTwoWrapper = new VerticalLayout();
	VerticalLayout stepThreeWrapper = new VerticalLayout();
	VerticalLayout stepFourWrapper = new VerticalLayout();
	VerticalLayout endWrapper = new VerticalLayout();

	private final WaitingPersonService waitingPersonService;
	private final OnboardingDataService onboardingDataService;
	private final SubscriptionModelService subscriptionModelService;
	private final OnboardingTokenService onboardingTokenService;
	
	private List<VerticalLayout> cards = new ArrayList<>();
	private Button finishOnboarding = new Button("Onboarding abschließen");

	public OnboardingView(WaitingPersonService waitingPersonService,
			OnboardingDataService onboardingDataService,
			SubscriptionModelService subscriptionModelService,
			OnboardingTokenService onboardingTokenService) {

		addClassNames("onboarding-view", LumoUtility.Padding.NONE);
		
		this.waitingPersonService = waitingPersonService;
		this.onboardingDataService = onboardingDataService;
		this.subscriptionModelService = subscriptionModelService;
		this.onboardingTokenService = onboardingTokenService;
		
		setWidth("100%");
		
		wizzard.addClassNames(LumoUtility.Margin.NONE, LumoUtility.Padding.NONE, LumoUtility.AlignItems.CENTER);
		wizzard.setWidthFull();
		wizzard.setHeightFull();
		add(wizzard);
	}
	
	private void createEnd() {
		
		endWrapper.addClassNames(LumoUtility.AlignItems.CENTER);
		endWrapper.setWidthFull();
		endWrapper.setHeightFull();
		
		VerticalLayout innerLayout = new VerticalLayout();
		innerLayout.addClassNames(LumoUtility.AlignItems.CENTER);
		innerLayout.setHeightFull();
		innerLayout.setWidthFull();
		
		Span header = new Span();
		header.addClassName("onboarding-intro");
		header.setText("Und schon fertig !");
		
		Span outro = new Span();
		outro.addClassName("onboarding-intro");
		outro.setText("Dein Verein wird sich per Email an dich wenden. Du kannst den Tab jetzt schließen :)");
		
		innerLayout.add(header, outro);
		endWrapper.add(innerLayout);
		
		tabEnd = new Tab("Erfolgreich");
}
	
	private void createBegin() {
		
		beginWrapper.addClassNames(LumoUtility.AlignItems.CENTER);
		beginWrapper.setWidthFull();
		beginWrapper.setHeightFull();
		
		VerticalLayout innerLayout = new VerticalLayout();
		innerLayout.addClassNames(LumoUtility.AlignItems.CENTER);
		innerLayout.setHeightFull();
		innerLayout.setWidthFull();
		
		Span introduction = new Span();
		introduction.addClassName("onboarding-intro");
		introduction.setText("Herzlich willkommen zum Onboarding in deinem Cannabis Social Club!");
		
		Button startButton = new Button("Los geht*s");
		startButton.addClassName("save-button");;
		startButton.addClickListener(e -> {
			tabStepOne.setEnabled(true);
			wizzard.setSelectedTab(tabStepOne);
			wizzard.remove(tabBegin);
		});		
		
		innerLayout.add(introduction, startButton);
		beginWrapper.add(innerLayout);
		
		tabBegin = new Tab("Start");
		tabBegin = wizzard.add(tabBegin, beginWrapper);
	}

	private void createStepFourLayout() {
		stepFourWrapper.addClassNames(LumoUtility.AlignItems.CENTER);
		
		H1 stepThreeHeading = new H1("Abo auswählen");
		stepThreeHeading.addClassName("customheader");
		
		VerticalLayout buttonWrapper = new VerticalLayout();
		buttonWrapper.addClassNames(LumoUtility.AlignItems.CENTER);	
		
		finishOnboarding.setEnabled(false);
		finishOnboarding.addClickListener(e -> {
			wizzard.add(tabEnd, endWrapper);
			wizzard.setSelectedTab(tabEnd);
			wizzard.remove(tabStepOne);
			wizzard.remove(tabStepTwo);
			wizzard.remove(tabStepThree);
			wizzard.remove(tabStepFour);
		});
		
		finishOnboarding.addClassName("save-button");
		buttonWrapper.add(finishOnboarding);
		
		stepFourWrapper.add(createPricingModelsLayout(), buttonWrapper);
	}
	
	private Component createPricingModelsLayout() {
		HorizontalLayout modelsLayout = new HorizontalLayout();
		modelsLayout.setWidthFull();
		
		List<SubscriptionModel> models = subscriptionModelService.findAllByAssociation(onboardingToken.getAssociationId());
		
		models.forEach(model -> {
			modelsLayout.add(createModelCardComponent(model.getName(), model.getDescription(), String.valueOf(model.getAmount())));
		});
		
		return modelsLayout;
	}
	
	private Component createModelCardComponent(String titleValue, String descValue, String priceValue) {
		
		VerticalLayout modelCardWrapper = new VerticalLayout();
		Span amountPerMonth = new Span();
		Span title = new Span();
		Span description = new Span();
		
		modelCardWrapper.setWidthFull();
		modelCardWrapper.setHeightFull();
		modelCardWrapper.addClassNames(LumoUtility.JustifyContent.CENTER, LumoUtility.AlignItems.CENTER);
		
		VerticalLayout card = new VerticalLayout();
		card.setMinHeight(500, Unit.PIXELS);
		card.setMinWidth(500, Unit.PIXELS);
		card.setMaxWidth(550, Unit.PIXELS);
		card.addClassNames("onboarding-model-box-grey");
		cards.add(card);
		
		VerticalLayout priceWrapper = new VerticalLayout();
		priceWrapper.addClassNames(LumoUtility.JustifyContent.CENTER, LumoUtility.AlignItems.CENTER);
		priceWrapper.setWidthFull();
		amountPerMonth.setText(priceValue + "€");
		amountPerMonth.addClassNames("price-membership");
		
		Span perMonth = new Span("/pro Monat");
		perMonth.addClassNames("desc-membership", LumoUtility.Margin.Top.XLARGE);
		priceWrapper.add(amountPerMonth, perMonth);
		
		VerticalLayout titleWrapper = new VerticalLayout();
		titleWrapper.addClassNames(LumoUtility.JustifyContent.CENTER, LumoUtility.AlignItems.CENTER);
		title.setText(titleValue);
		title.addClassNames("title-membership");
		titleWrapper.add(title);
		
		VerticalLayout descWrapper = new VerticalLayout();
		descWrapper.addClassNames(LumoUtility.JustifyContent.CENTER, LumoUtility.AlignItems.CENTER);
		description.setText(descValue);
		
		description.addClassNames("desc-membership");
		descWrapper.add(description);
		
		VerticalLayout buttonWrapper = new VerticalLayout();
		buttonWrapper.addClassNames(LumoUtility.JustifyContent.CENTER, LumoUtility.AlignItems.CENTER);
		Button choiceButton = new Button("Auswählen");
		choiceButton.addClassName("save-button");
		
		choiceButton.addClickListener(e -> {
			resetCards();
			card.removeClassName("onboarding-model-box-grey");
			card.addClassNames("onboarding-model-box");
			finishOnboarding.setEnabled(true);
		});
		
		buttonWrapper.add(choiceButton);
		card.add(priceWrapper, titleWrapper, descWrapper, buttonWrapper);	
		modelCardWrapper.add(card);
		
		return modelCardWrapper;
	}


	private void resetCards() {
		cards.forEach(card -> {
			card.removeClassName("onboarding-model-box");
			card.addClassNames("onboarding-model-box-grey");
		});
	}

	private void createStepThreeLayout() {
		stepThreeWrapper.addClassNames(LumoUtility.AlignItems.CENTER);
		stepThreeWrapper.setMaxWidth(1000, Unit.PIXELS);
		
		H1 stepThreeHeading = new H1("Einwilligungen");
		stepThreeHeading.addClassName("customheader");
				
		FormLayout dataLayout = new FormLayout();
		
		confirmExclusiveMemberShipBox.setLabel("Ich bestätige, dass ich nur in diesem Verein angemeldet bin.");
		
		confirmDataUsageBox.setLabel("Ich bestätige die Erlaubnis zur Verarbeitung meiner Daten.");
		
		dataLayout.add(confirmExclusiveMemberShipBox, createExclusiveMemberDetails(), confirmDataUsageBox, createDataUsageDetails());
		
		Button buttonConfirmStepThree = new Button("bestätigen & weiter");
		buttonConfirmStepThree.setEnabled(true);
		buttonConfirmStepThree.addClickListener(e -> {
//			if(validateInputStepThree()) {				
				tabStepFour.setEnabled(true);
				wizzard.setSelectedTab(tabStepFour);
//			}
		});
		buttonConfirmStepThree.addClassName("save-button");
		VerticalLayout buttonWrapper = new VerticalLayout();
		
		confirmDataUsageBox.addValueChangeListener(e -> {
			buttonConfirmStepThree.setEnabled(e.getValue());
		});
		
		buttonWrapper.addClassNames(LumoUtility.AlignItems.END);
		buttonWrapper.add(buttonConfirmStepThree);	
		
		stepThreeWrapper.add(stepThreeHeading, dataLayout, buttonWrapper);			
	}
	
	private boolean validateInputStepThree() {
		
		if(confirmExclusiveMemberShipBox.getValue()) {
			confirmExclusiveMemberShipBox.removeClassName("red");
		} else {
			confirmExclusiveMemberShipBox.addClassName("red");
			return false;
		}
		
		return true;
	}

	private Details createDataUsageDetails() {
		Span titleDataUsage = new Span("Datenverarbeitung");
		Span textDataUsage = new Span(
				"Ich bin damit einverstanden, dass meine Daten von der Organisation \"Garden Regensburg e. V.\" zum Zweck der Vertragserfüllung im Rahmen der Mitgliedschaft hinterlegt, verarbeitet und genutzt werden. Ich bin darauf hingewiesen worden, dass die im Rahmen der vorstehend genannten Zwecke erhobenen personenbezogenen Daten unter Beachtung der EU-Datenschutzgrundverordnung, erhoben, verarbeitet, genutzt und übermittelt werden. Ich wurde über meine Rechte als Betroffener unterrichtet. Die Einverständniserklärung erfolgt auf freiwilliger Basis. Ich wurde darüber aufgeklärt, dass ich die Einverständniserklärung jederzeit durch schriftliche Mitteilung für die Zukunft widerrufen kann.");
		
		VerticalLayout contentWarpper = new VerticalLayout(titleDataUsage, textDataUsage);
		contentWarpper.setSpacing(false);
		contentWarpper.setPadding(false);

		Details detailsDataUsage = new Details("Datenverarbeitung nach DSGVO", contentWarpper);
		detailsDataUsage.setOpened(false);
		return detailsDataUsage;
	}
	
	private Details createExclusiveMemberDetails() {
		Span name = new Span("Abschnitt 2 § 16 des Cannabisgesetzes");	
		Span law = new Span("(3) Als Mitglied in einer Anbauvereinigung darf nur aufgenommen werden, wer gegenüber der Anbauvereinigung\r\n"
				+ "schriftlich oder elektronisch versichert, dass er oder sie kein Mitglied in einer anderen Anbauvereinigung ist. Die\r\n"
				+ "Selbstauskunft nach Satz 1 ist von der Anbauvereinigung drei Jahre aufzubewahren.");
		
		VerticalLayout content = new VerticalLayout(name, law);
		content.setSpacing(false);
		content.setPadding(false);

		Details details = new Details("Mitgliegschaft nach dem CanG", content);
		details.setOpened(false);
		return details;
	}

	private void createStepOneLayout() {

		stepOneWrapper.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.Margin.NONE);
		stepOneWrapper.setMaxWidth(1000, Unit.PIXELS);
		H1 stepOneHeading = new H1("Angaben zu deiner Person");
		stepOneHeading.addClassName("customheader");
		
		FormLayout dataLayout = new FormLayout();
		
		firstName = new TextField("Vorname");
		lastName = new TextField("Nachname");
		email = new TextField("Email");
		phone = new TextField("Telefonnummer");
        phone.setAllowedCharPattern("[0-9/]");
		dateOfBirth = new DatePicker("Geburtstag");
		dateOfBirth.setOverlayClassName("waiting-list-view-date-picker-1");
		dateOfBirth.addClassName("waiting-list-view-date-picker-1");
		
		confirmAgeBox = new Checkbox();
		confirmAgeBox.setLabel("Ich bestätige, dass ich 21 Jahre alt bin.");
		confirmAgeBox.addClickListener(value -> {
			if(confirmAgeBox.getValue()) {				
				confirmAgeBox.removeClassName("red");
			}
		});
		
		confirmGeneralDataBox = new Checkbox();
		confirmGeneralDataBox.setLabel("Ich bestätige, dass ich die Daten zu meiner Person wahrheitsgemäß angegeben habe.");
		confirmGeneralDataBox.addClickListener(value -> {
			buttonConfirmStepOne.setEnabled(confirmGeneralDataBox.getValue());
		});
		
		Hr hr = new Hr();
		
		dataLayout.add(firstName, lastName, dateOfBirth, phone, email, hr, confirmAgeBox, confirmGeneralDataBox);
		dataLayout.setColspan(email, 2);
		dataLayout.setColspan(confirmAgeBox, 2);
		dataLayout.setColspan(confirmGeneralDataBox, 2);
		dataLayout.setColspan(hr, 2);
		
		streetName = new TextField("Straße");	
		streetNumber = new TextField("Hausnummer");
		postalCode = new TextField("PLZ");
		city = new TextField("Ort");
		
		buttonConfirmStepOne.addClassName("save-button");
		buttonConfirmStepOne.setEnabled(true);
		
		buttonConfirmStepOne.addClickListener(e -> {
//			if(validateInputGeneralData()) {
				tabStepTwo.setEnabled(true);
				wizzard.setSelectedTab(tabStepTwo);
//			}
		});
		
		VerticalLayout buttonWrapper = new VerticalLayout();		
		buttonWrapper.addClassNames(LumoUtility.AlignItems.END);
		buttonWrapper.add(buttonConfirmStepOne);	
		
		stepOneWrapper.add(stepOneHeading, dataLayout, buttonWrapper);	
	}
	
	private boolean validateInputGeneralData() {		
		
		if(firstName.getValue().equals(firstName.getEmptyValue())) {
			firstName.setInvalid(true);
			firstName.setHelperText("Der Vorname muss angegeben werden");
			firstName.getStyle().set("--vaadin-input-field-invalid-background", "--lumo-error-color-10pct");
			return false;
		} else {
			firstName.setInvalid(false);
			firstName.setHelperText("");
			firstName.getStyle().set("--vaadin-input-field-invalid-background", "--lumo-contrast-10pct");
		}
		
		if(lastName.getValue().equals(lastName.getEmptyValue())) {
			lastName.setInvalid(true);
			lastName.setHelperText("Der Nachname muss angegeben werden");
			lastName.getStyle().set("--vaadin-input-field-invalid-background", "--lumo-error-color-10pct");
			return false;
		} else {
			lastName.setInvalid(false);
			lastName.setHelperText("");
			lastName.getStyle().set("--vaadin-input-field-invalid-background", "--lumo-contrast-10pct");
		}
		
		if(phone.getValue().equals(phone.getEmptyValue())) {
			phone.setInvalid(true);
			phone.setHelperText("Deine Telefonnummer muss angegeben sein");
			phone.getStyle().set("--vaadin-input-field-invalid-background", "--lumo-error-color-10pct");
			return false;
		} else {
			phone.setInvalid(false);
			phone.setHelperText("");
			phone.getStyle().set("--vaadin-input-field-invalid-background", "--lumo-contrast-10pct");
		}
		
		if(dateOfBirth.getValue().equals(dateOfBirth.getEmptyValue())) {
			dateOfBirth.setInvalid(true);
			dateOfBirth.setHelperText("Dein Geburtsdatum muss ebenso angegeben sein");
			dateOfBirth.getStyle().set("--vaadin-input-field-invalid-background", "--lumo-error-color-10pct");
			return false;
		} else {
			dateOfBirth.setInvalid(false);
			dateOfBirth.setHelperText("");
			dateOfBirth.getStyle().set("--vaadin-input-field-invalid-background", "--lumo-contrast-10pct");
		}
		
		if(email.getValue().equals(email.getEmptyValue())) {
			email.setInvalid(true);
			email.setHelperText("Deine Email muss angegeben sein");
			email.getStyle().set("--vaadin-input-field-invalid-background", "--lumo-error-color-10pct");
			return false;
		} else {
			email.setInvalid(false);
			email.setHelperText("");
			email.getStyle().set("--vaadin-input-field-invalid-background", "--lumo-contrast-10pct");
		}
		
		if(!confirmAgeBox.getValue()) {
			confirmAgeBox.addClassName("red");
			return false;
		} else {
			confirmAgeBox.removeClassName("red");
		}
		
		return confirmGeneralDataBox.getValue();
	}
	
	private boolean validateInputAddressData() {
		
		if(streetName.getValue().equals(streetName.getEmptyValue())) {
			streetName.setInvalid(true);
			streetName.setHelperText("Der Name der Straße muss angegeben werden");
			streetName.getStyle().set("--vaadin-input-field-invalid-background", "--lumo-error-color-10pct");
			return false;
		} else {
			streetName.setInvalid(false);
			streetName.setHelperText("");
			streetName.getStyle().set("--vaadin-input-field-invalid-background", "--lumo-contrast-10pct");
		}
		
		if(streetNumber.getValue().equals(streetNumber.getEmptyValue())) {
			streetNumber.setInvalid(true);
			streetNumber.setHelperText("Die Hausnummer muss angegeben werden");
			streetNumber.getStyle().set("--vaadin-input-field-invalid-background", "--lumo-error-color-10pct");
			return false;
		} else {
			streetNumber.setInvalid(false);
			streetNumber.setHelperText("");
			streetNumber.getStyle().set("--vaadin-input-field-invalid-background", "--lumo-contrast-10pct");
		}
		
		if(postalCode.getValue().equals(postalCode.getEmptyValue())) {
			postalCode.setInvalid(true);
			postalCode.setHelperText("Die Postleitzahl muss angegeben werden");
			postalCode.getStyle().set("--vaadin-input-field-invalid-background", "--lumo-error-color-10pct");
			return false;
		} else {
			postalCode.setInvalid(false);
			postalCode.setHelperText("");
			postalCode.getStyle().set("--vaadin-input-field-invalid-background", "--lumo-contrast-10pct");
		}
		
		if(city.getValue().equals(city.getEmptyValue())) {
			city.setInvalid(true);
			city.setHelperText("Die Stadt fehlt noch");
			city.getStyle().set("--vaadin-input-field-invalid-background", "--lumo-error-color-10pct");
			return false;
		} else {
			city.setInvalid(false);
			city.setHelperText("");
			city.getStyle().set("--vaadin-input-field-invalid-background", "--lumo-contrast-10pct");
		}
		
		return confirmAddressBox.getValue();		
	}
	
	private void createStepTwoLayout() {
		stepTwoWrapper.addClassNames(LumoUtility.Margin.NONE, LumoUtility.AlignItems.CENTER);
		stepTwoWrapper.setMaxWidth(1000, Unit.PIXELS);
		
		H1 stepTwoHeading = new H1("Angaben zu deinem Wohnort");
		stepTwoHeading.addClassName("customheader");
		
		FormLayout dataLayout = new FormLayout();
		
		streetName = new TextField("Straße");	
		streetNumber = new TextField("Hausnummer");
		postalCode = new TextField("PLZ");
		city = new TextField("Ort");
		
		Hr hr = new Hr();
		
		confirmAddressBox = new Checkbox();
		confirmAddressBox.setLabel("Ich bestätige, dass die Angaben zu meiner Adresse korrekt sind.");
		confirmAddressBox.addValueChangeListener(e -> {
			buttonConfirmStepTwo.setEnabled(e.getValue());
		});
		
		dataLayout.add(streetName, streetNumber, postalCode, city, hr, confirmAddressBox);
		dataLayout.setColspan(streetName, 2);
		dataLayout.setColspan(city, 2);
		dataLayout.setColspan(confirmAddressBox, 2);
		dataLayout.setColspan(hr, 2);

		buttonConfirmStepTwo.addClassName("save-button");
		buttonConfirmStepTwo.setEnabled(true);
		buttonConfirmStepTwo.addClickListener(e -> {
//			if(validateInputAddressData()) {
				tabStepThree.setEnabled(true);	
				wizzard.setSelectedTab(tabStepThree);
//			}
		});
		
		VerticalLayout buttonWrapper = new VerticalLayout();
		
		buttonWrapper.addClassNames(LumoUtility.AlignItems.END);
		buttonWrapper.add(buttonConfirmStepTwo);	
		
		stepTwoWrapper.add(stepTwoHeading, dataLayout, buttonWrapper);
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		//takes token + URL refresh or redirect to login	
		token = event.getRouteParameters().get("token").orElse("");

        if (token.isBlank()) {
            // No Token
            event.forwardTo("login");
            Notification.show("NO TOKEN");
        } else {
        	//refresh URL
            getElement().executeJs("window.history.replaceState({}, '', window.location.pathname);");
            validateToken();
        }
	}

	private void validateToken() {
		Optional<OnboardingToken> optionalToken = onboardingTokenService.findByToken(token);
		
		if(optionalToken.isPresent()) {
			Date expirationDate = optionalToken.get().getExpirationDate();
			onboardingToken = optionalToken.get();

			if(new Date().after(expirationDate)) {
				UI.getCurrent().navigate("login");
				onboardingDataService.delete(onboardingToken.getId());
				onboardingToken = null;
				Notification show = Notification.show("Onboarding Link abgelaufen. Kontaktiere deinen Verein");
				show.addThemeVariants(NotificationVariant.LUMO_ERROR);
			}
			
			createBegin();
			createStepOneLayout();
			createStepTwoLayout();
			createStepThreeLayout();
			createStepFourLayout();
			createEnd();
			
			tabStepOne = wizzard.add("Schritt 1", stepOneWrapper);
			tabStepOne.setEnabled(false);

			tabStepTwo = wizzard.add("Schritt 2", stepTwoWrapper);
			tabStepTwo.setEnabled(false);
			
			tabStepThree = wizzard.add("Schritt 3", stepThreeWrapper);
			tabStepThree.setEnabled(false);
			
			tabStepFour = wizzard.add("Schritt 4", stepFourWrapper);
			tabStepFour.setEnabled(false);

		} else {			
			UI.getCurrent().navigate("login");
			Notification show = Notification.show("Onboarding Link abgelaufen. Kontaktiere deinen Verein");
			show.addThemeVariants(NotificationVariant.LUMO_ERROR);
		}
	}
}
