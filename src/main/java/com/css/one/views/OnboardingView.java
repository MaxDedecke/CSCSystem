package com.css.one.views;

import com.css.one.data.SubscriptionModel;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
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
@Route(value = "onboarding/:token?", layout = MainLayout.class)
@RouteAlias("onboarding")
@AnonymousAllowed
public class OnboardingView extends VerticalLayout implements BeforeEnterObserver {

	private static final long serialVersionUID = 1862806183284315642L;
	
	private TabSheet wizzard = new TabSheet();

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
	
	private	ComboBox<SubscriptionModel> subscBox = new ComboBox<>("Modelle");
	private Span descriptionSubscription = new Span("testDescription");

	private Tab tabStepTwo;
	private Tab tabStepThree;
	private Tab tabStepFour;

	VerticalLayout stepOneWrapper = new VerticalLayout();
	VerticalLayout stepTwoWrapper = new VerticalLayout();
	VerticalLayout stepThreeWrapper = new VerticalLayout();
	VerticalLayout stepFourWrapper = new VerticalLayout();

	public OnboardingView() {
		addClassNames("onboaring-view", LumoUtility.Padding.NONE);
		
		setWidth("100%");
		createStepOneLayout();
		createStepTwoLayout();
		createStepThreeLayout();
		createStepFourLayout();
		
		wizzard.addClassNames(LumoUtility.Margin.NONE, LumoUtility.Padding.NONE ,LumoUtility.AlignItems.CENTER);
		wizzard.setWidthFull();
		wizzard.setHeightFull();
		
		wizzard.add("Schritt 1", stepOneWrapper);
		tabStepTwo = wizzard.add("Schritt 2", stepTwoWrapper);
		tabStepTwo.setEnabled(false);
		tabStepTwo.addClassName(LumoUtility.Width.AUTO);
		tabStepThree = wizzard.add("Schritt 3", stepThreeWrapper);
		tabStepThree.setEnabled(false);
		tabStepFour = wizzard.add("Schritt 4", stepFourWrapper);
		tabStepFour.setEnabled(false);
			
		add(wizzard);
	}
	
	private void createStepFourLayout() {
		stepFourWrapper.addClassNames(LumoUtility.AlignItems.CENTER);
		stepFourWrapper.setMaxWidth(1000, Unit.PIXELS);
		
		H1 stepThreeHeading = new H1("Abo auswählen");
		stepThreeHeading.addClassName("customheader");
		
		FormLayout dataLayout = new FormLayout();		
		VerticalLayout buttonWrapper = new VerticalLayout();
		
		buttonWrapper.addClassNames(LumoUtility.AlignItems.END);	
		
		Button finishOnboarding = new Button("Onboarding abschließen");
		finishOnboarding.setEnabled(false);
		finishOnboarding.addClickListener(e -> {
			if(!subscBox.isEmpty()) {
				//End data onboarding here 
			}
		});
		
		finishOnboarding.addClassName("save-button");
		buttonWrapper.add(finishOnboarding);
		
		dataLayout.add(subscBox, descriptionSubscription);
		stepFourWrapper.add(dataLayout, buttonWrapper);
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
		buttonConfirmStepThree.setEnabled(false);
		buttonConfirmStepThree.addClickListener(e -> {
			if(validateInputStepThree()) {				
				tabStepFour.setEnabled(true);
				wizzard.setSelectedTab(tabStepFour);
			}
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

		stepOneWrapper.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
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
		buttonConfirmStepOne.setEnabled(false);
		
		buttonConfirmStepOne.addClickListener(e -> {
			if(validateInputGeneralData()) {
				tabStepTwo.setEnabled(true);
				wizzard.setSelectedTab(tabStepTwo);
			}
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
		
		H1 stepTwoHeading = new H1("Schritt 2: Angaben zu deinem Wohnort");
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
		buttonConfirmStepTwo.setEnabled(false);
		buttonConfirmStepTwo.addClickListener(e -> {
			if(validateInputAddressData()) {
				tabStepThree.setEnabled(true);	
				wizzard.setSelectedTab(tabStepThree);
			}
		});
		
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
