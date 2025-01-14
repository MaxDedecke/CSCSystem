package com.css.one.components;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import com.css.one.data.MemberData;
import com.css.one.data.OnboardingAnswer;
import com.css.one.data.OnboardingData;
import com.css.one.data.OnboardingQuestion;
import com.css.one.data.OnboardingToken;
import com.css.one.data.SubscriptionModel;
import com.css.one.data.WaitingPerson;
import com.css.one.data.enums.OnboardingStatus;
import com.css.one.services.EmailService;
import com.css.one.services.MemberDataService;
import com.css.one.services.OnboardingDataService;
import com.css.one.services.OnboardingQuestionService;
import com.css.one.services.OnboardingTokenService;
import com.css.one.services.SubscriptionModelService;
import com.css.one.services.WaitingPersonService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoUtility;

@CssImport(value = "/themes/css-system-one/theme-editor.css")
public class OnboardingWizzard extends TabSheet {

	private static final long serialVersionUID = -53247114243375326L;
	
	private int associationId;

	private Tab tabBegin;
	private Tab tabStepOne;
	private Tab tabStepTwo;
	private Tab tabStepThree;
	private Tab tabStepFour;
	private Tab tabQuestions;
	private Tab tabEnd;
	
	VerticalLayout beginWrapper = new VerticalLayout();
	VerticalLayout stepOneWrapper = new VerticalLayout();
	VerticalLayout stepTwoWrapper = new VerticalLayout();
	VerticalLayout stepThreeWrapper = new VerticalLayout();
	VerticalLayout stepFourWrapper = new VerticalLayout();
	VerticalLayout questionsWrapper = new VerticalLayout();
	VerticalLayout endWrapper = new VerticalLayout();
	
	private List<VerticalLayout> cards = new ArrayList<>();
	private List<OnboardingAnswer> tmpAnswers = new ArrayList<>();

	private Button finishStepFour = new Button("weiter");
	private Checkbox confirmExclusiveMemberShipBox = new Checkbox();
	private Checkbox confirmDataUsageBox = new Checkbox();

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
	
	private final OnboardingDataService onboardingDataService;
	private final SubscriptionModelService subscriptionModelService;
	private final OnboardingTokenService onboardingTokenService;
	private final OnboardingQuestionService onboardingQuestionService;
	private final MemberDataService memberDataService;
	
	private final WaitingPersonService waitingPersonService;
	private final Optional<OnboardingToken> onboardingToken;
	
	private WaitingPerson personOnTheFly;
	private MemberData personData;
	
	public OnboardingWizzard(OnboardingDataService onboardingDataService,
			SubscriptionModelService subscriptionModelService,
			OnboardingTokenService onboardingTokenService,
			OnboardingQuestionService onboardingQuestionService,
			MemberDataService memberDataService,
			WaitingPersonService waitingPersonService,
			Optional<OnboardingToken> onboardingToken,
			Optional<Integer> associationId) {
		
		addClassNames(LumoUtility.Margin.NONE, LumoUtility.Padding.NONE, LumoUtility.AlignItems.CENTER);
		setWidthFull();
		setHeightFull();
		
		this.onboardingDataService = onboardingDataService;
		this.subscriptionModelService = subscriptionModelService;
		this.onboardingTokenService = onboardingTokenService;
		this.onboardingQuestionService = onboardingQuestionService;
		this.memberDataService = memberDataService;
		this.waitingPersonService = waitingPersonService;
		this.onboardingToken = onboardingToken;
		
		associationId.ifPresent(id -> this.associationId = id);
		
		if(onboardingToken.isEmpty()) {
			personOnTheFly = new WaitingPerson();
			personOnTheFly.setDateOfRegistration(LocalDate.now());
			
			personData = new MemberData();
			personData.setDateOfRegistration(LocalDate.now());
		}
		
		createWizzard();
	}
	
	private void createWizzard() {
			
		createBegin();
		createStepOneLayout();
		createStepTwoLayout();
		createStepThreeLayout();
		createStepFourLayout();
		createQuestionsTab();
		createEnd();
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

		Button startButton = new Button("Los geht's");
		startButton.addClassName("save-button");
		startButton.addClickListener(e -> {
			tabStepOne.setEnabled(true);
			setSelectedTab(tabStepOne);
			remove(tabBegin);
		});

		innerLayout.add(introduction, startButton);
		beginWrapper.add(innerLayout);

		tabBegin = new Tab("Start");
		tabBegin = add(tabBegin, beginWrapper);
	}

	private void createQuestionsTab() {

		List<OnboardingQuestion> questions = onboardingQuestionService.findAllByAssociation(
				onboardingToken.isPresent() ? onboardingToken.get().getAssociationId() : associationId);
		Map<OnboardingQuestion, TextArea> inputs = new HashMap<>();

		//only show tab if questions exist
		if (!questions.isEmpty()) {
			FormLayout innerLayout = new FormLayout();
			questions.forEach(question -> {

				H3 qHeader = new H3(question.getQuestion());
				TextArea answerField = new TextArea("Antwort");
				innerLayout.add(qHeader, answerField);
				inputs.put(question, answerField);
			});

			Button continueButton = new Button("Onboarding abschließen");
			continueButton.addClassName("save-button");
			continueButton.addClickListener(e -> {
				tabEnd = new Tab("Erfolgreich");

				add(tabEnd, endWrapper);
				setSelectedTab(tabEnd);
				remove(tabStepOne);
				remove(tabStepTwo);
				remove(tabStepThree);
				remove(tabStepFour);
				remove(tabQuestions);

				finishOnboardingDataInputProcess(inputs);
				sendNextStepsEmailToWaitingPerson();
			});
			questionsWrapper.add(innerLayout, continueButton);

			tabQuestions = new Tab("Fragen des Vereins");
			tabQuestions.setEnabled(false);
			add(tabQuestions, questionsWrapper);
		}
	}

	private void createStepFourLayout() {
		stepFourWrapper.addClassNames(LumoUtility.AlignItems.CENTER);
		
		H1 stepThreeHeading = new H1("Abo auswählen");
		stepThreeHeading.addClassName("customheader");
		
		VerticalLayout buttonWrapper = new VerticalLayout();
		buttonWrapper.addClassNames(LumoUtility.AlignItems.CENTER);	
		
		finishStepFour.setEnabled(false);
		finishStepFour.addClickListener(e -> {
			tabQuestions.setEnabled(true);
			setSelectedTab(tabQuestions);
		});
		
		finishStepFour.addClassName("save-button");
		buttonWrapper.add(finishStepFour);
		
		stepFourWrapper.add(createPricingModelsLayout(), buttonWrapper);
		
		tabStepFour = new Tab("Abomodelle");
		tabStepFour.setEnabled(false);
		add(tabStepFour, stepFourWrapper);
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
			if(validateInputStepThree()) {				
				tabStepFour.setEnabled(true);
				setSelectedTab(tabStepFour);
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
		
		tabStepThree = new Tab("Schritt 3");
		tabStepThree.setEnabled(false);
		add(tabStepThree, stepThreeWrapper);
	}
	
	private void createStepTwoLayout() {
		
		stepTwoWrapper.addClassNames(LumoUtility.Margin.NONE, LumoUtility.AlignItems.CENTER);
		stepTwoWrapper.setMaxWidth(1000, Unit.PIXELS);

		H1 stepTwoHeading = new H1("Angaben zu deinem Wohnort");
		stepTwoHeading.addClassName("customheader");

		FormLayout dataLayout = new FormLayout();

		streetName = new TextField("Straße");
		streetName.setPlaceholder("Musterstraße");
		streetNumber = new TextField("Hausnummer");
		streetNumber.setPlaceholder("1");
		postalCode = new TextField("PLZ");
		postalCode.setPlaceholder("93049");
		city = new TextField("Ort");
		city.setPlaceholder("Musterstadt");

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
			if (validateInputAddressData()) {
				tabStepThree.setEnabled(true);
				setSelectedTab(tabStepThree);
			}
		});

		VerticalLayout buttonWrapper = new VerticalLayout();

		buttonWrapper.addClassNames(LumoUtility.AlignItems.END);
		buttonWrapper.add(buttonConfirmStepTwo);

		stepTwoWrapper.add(stepTwoHeading, dataLayout, buttonWrapper);
		
		tabStepTwo = new Tab("Schritt 2");
		tabStepTwo.setEnabled(false);
		add(tabStepTwo, stepTwoWrapper);
	}
	
	private void createStepOneLayout() {

		stepOneWrapper.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.Margin.NONE);
		stepOneWrapper.setMaxWidth(1000, Unit.PIXELS);
		H1 stepOneHeading = new H1("Angaben zu deiner Person");
		stepOneHeading.addClassName("customheader");
		
		FormLayout dataLayout = new FormLayout();
		
		firstName = new TextField("Vorname");
		firstName.setPlaceholder("Max");
		lastName = new TextField("Nachname");
		lastName.setPlaceholder("Mustermann");
		email = new TextField("Email");
		email.setPlaceholder("max.mustermann@beispiel.de");
		phone = new TextField("Telefonnummer");
        phone.setAllowedCharPattern("[0-9/]");
        phone.setPlaceholder("0941420420");
		dateOfBirth = new DatePicker("Geburtstag");
		dateOfBirth.setOverlayClassName("waiting-list-view-date-picker-1");
		dateOfBirth.addClassName("waiting-list-view-date-picker-1");
		dateOfBirth.setPlaceholder("01.01.2000");		
		dateOfBirth.setLocale(Locale.GERMANY);
		
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
			if(validateInputGeneralData()) {
				tabStepTwo.setEnabled(true);
				setSelectedTab(tabStepTwo);
			}
		});
		
		VerticalLayout buttonWrapper = new VerticalLayout();		
		buttonWrapper.addClassNames(LumoUtility.AlignItems.END);
		buttonWrapper.add(buttonConfirmStepOne);	
		
		stepOneWrapper.add(stepOneHeading, dataLayout, buttonWrapper);	
		
		tabStepOne = new Tab("Schritt 1");
		tabStepOne.setEnabled(false);
		add(tabStepOne, stepOneWrapper);
	}
	
	private Component createPricingModelsLayout() {
		HorizontalLayout modelsLayout = new HorizontalLayout();
		modelsLayout.setWidthFull();
		
		List<SubscriptionModel> models = subscriptionModelService.findAllByAssociation(onboardingToken.isPresent() ? onboardingToken.get().getAssociationId() : this.associationId);
		
		models.forEach(model -> {
			//Show model only if active (e.g. online)
			if (model.isOnline()) {
				modelsLayout.add(createModelCardComponent(model.getName(), model.getDescription(),
						String.valueOf(model.getAmount())));
			}
		});
		
		return modelsLayout;
	}
	
	private Component createModelCardComponent(String titleValue, String descValue, String priceValue) {

		VerticalLayout modelCardWrapper = new VerticalLayout();
		Span amountPerMonth = new Span();
		Span title = new Span();

		modelCardWrapper.setWidthFull();
		modelCardWrapper.setHeightFull();
		modelCardWrapper.addClassNames(LumoUtility.JustifyContent.CENTER, LumoUtility.AlignItems.CENTER);

		VerticalLayout card = new VerticalLayout();
		card.setMinHeight(400, Unit.PIXELS);
		card.setMinWidth(400, Unit.PIXELS);
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
		descWrapper.addClassNames(LumoUtility.Margin.Bottom.NONE, LumoUtility.Padding.Bottom.XSMALL);
		Span descriptionOfModel = new Span();
		descriptionOfModel.setText(descValue);
				
		if(descValue.length() > 100) {
			descriptionOfModel.addClassName("desc-membership-small");
		} else if(descValue.length() < 20) {
			descriptionOfModel.addClassName("desc-membership-big");
		} else {			
			descriptionOfModel.addClassNames("desc-membership-medium");
		}

		descWrapper.add(descriptionOfModel);

		VerticalLayout buttonWrapper = new VerticalLayout();
		buttonWrapper.addClassNames(LumoUtility.JustifyContent.CENTER, LumoUtility.AlignItems.CENTER);
		Button choiceButton = new Button("Auswählen");
		choiceButton.addClassName("save-button");

		choiceButton.addClickListener(e -> {
			resetCards();
			card.removeClassName("onboarding-model-box-grey");
			card.addClassNames("onboarding-model-box");
			finishStepFour.setEnabled(true);
		});

		buttonWrapper.add(choiceButton);
		card.add(priceWrapper, titleWrapper, descWrapper, buttonWrapper);
		modelCardWrapper.add(card);

		return modelCardWrapper;
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
		Span law = new Span(
				"(3) Als Mitglied in einer Anbauvereinigung darf nur aufgenommen werden, wer gegenüber der Anbauvereinigung\r\n"
						+ "schriftlich oder elektronisch versichert, dass er oder sie kein Mitglied in einer anderen Anbauvereinigung ist. Die\r\n"
						+ "Selbstauskunft nach Satz 1 ist von der Anbauvereinigung drei Jahre aufzubewahren.");

		VerticalLayout content = new VerticalLayout(name, law);
		content.setSpacing(false);
		content.setPadding(false);

		Details details = new Details("Mitgliegschaft nach dem CanG", content);
		details.setOpened(false);
		return details;
	}
	
	private void resetCards() {
		cards.forEach(card -> {
			card.removeClassName("onboarding-model-box");
			card.addClassNames("onboarding-model-box-grey");
		});
	}

	private boolean validateInputStepThree() {

		if (confirmExclusiveMemberShipBox.getValue()) {
			confirmExclusiveMemberShipBox.removeClassName("red");
		} else {
			confirmExclusiveMemberShipBox.addClassName("red");
			return false;
		}

		return true;
	}

	private boolean validateInputGeneralData() {

		if (firstName.getValue().equals(firstName.getEmptyValue())) {
			firstName.setInvalid(true);
			firstName.setHelperText("Der Vorname muss angegeben werden");
			firstName.getStyle().set("--vaadin-input-field-invalid-background", "--lumo-error-color-10pct");
			return false;
		} else {
			firstName.setInvalid(false);
			firstName.setHelperText("");
			firstName.getStyle().set("--vaadin-input-field-invalid-background", "--lumo-contrast-10pct");
		}

		if (lastName.getValue().equals(lastName.getEmptyValue())) {
			lastName.setInvalid(true);
			lastName.setHelperText("Der Nachname muss angegeben werden");
			lastName.getStyle().set("--vaadin-input-field-invalid-background", "--lumo-error-color-10pct");
			return false;
		} else {
			lastName.setInvalid(false);
			lastName.setHelperText("");
			lastName.getStyle().set("--vaadin-input-field-invalid-background", "--lumo-contrast-10pct");
		}

		if (dateOfBirth.getValue().equals(dateOfBirth.getEmptyValue())) {
			dateOfBirth.setInvalid(true);
			dateOfBirth.setHelperText("Dein Geburtsdatum muss ebenso angegeben sein");
			dateOfBirth.getStyle().set("--vaadin-input-field-invalid-background", "--lumo-error-color-10pct");
			return false;
		} else {
			dateOfBirth.setInvalid(false);
			dateOfBirth.setHelperText("");
			dateOfBirth.getStyle().set("--vaadin-input-field-invalid-background", "--lumo-contrast-10pct");
		}
		
		if (phone.getValue().equals(phone.getEmptyValue())) {
			phone.setInvalid(true);
			phone.setHelperText("Deine Telefonnummer muss angegeben sein");
			phone.getStyle().set("--vaadin-input-field-invalid-background", "--lumo-error-color-10pct");
			return false;
		} else {
			phone.setInvalid(false);
			phone.setHelperText("");
			phone.getStyle().set("--vaadin-input-field-invalid-background", "--lumo-contrast-10pct");
		}


		if (email.getValue().equals(email.getEmptyValue())) {
			email.setInvalid(true);
			email.setHelperText("Deine Email muss angegeben sein");
			email.getStyle().set("--vaadin-input-field-invalid-background", "--lumo-error-color-10pct");
			return false;
		} else {
			email.setInvalid(false);
			email.setHelperText("");
			email.getStyle().set("--vaadin-input-field-invalid-background", "--lumo-contrast-10pct");
		}

		if (!confirmAgeBox.getValue()) {
			confirmAgeBox.addClassName("red");
			return false;
		} else {
			confirmAgeBox.removeClassName("red");
		}

		return confirmGeneralDataBox.getValue();
	}

	private boolean validateInputAddressData() {

		if (streetName.getValue().equals(streetName.getEmptyValue())) {
			streetName.setInvalid(true);
			streetName.setHelperText("Der Name der Straße muss angegeben werden");
			streetName.getStyle().set("--vaadin-input-field-invalid-background", "--lumo-error-color-10pct");
			return false;
		} else {
			streetName.setInvalid(false);
			streetName.setHelperText("");
			streetName.getStyle().set("--vaadin-input-field-invalid-background", "--lumo-contrast-10pct");
		}

		if (streetNumber.getValue().equals(streetNumber.getEmptyValue())) {
			streetNumber.setInvalid(true);
			streetNumber.setHelperText("Die Hausnummer muss angegeben werden");
			streetNumber.getStyle().set("--vaadin-input-field-invalid-background", "--lumo-error-color-10pct");
			return false;
		} else {
			streetNumber.setInvalid(false);
			streetNumber.setHelperText("");
			streetNumber.getStyle().set("--vaadin-input-field-invalid-background", "--lumo-contrast-10pct");
		}

		if (postalCode.getValue().equals(postalCode.getEmptyValue())) {
			postalCode.setInvalid(true);
			postalCode.setHelperText("Die Postleitzahl muss angegeben werden");
			postalCode.getStyle().set("--vaadin-input-field-invalid-background", "--lumo-error-color-10pct");
			return false;
		} else {
			postalCode.setInvalid(false);
			postalCode.setHelperText("");
			postalCode.getStyle().set("--vaadin-input-field-invalid-background", "--lumo-contrast-10pct");
		}

		if (city.getValue().equals(city.getEmptyValue())) {
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

	private void finishOnboardingDataInputProcess(Map<OnboardingQuestion, TextArea> inputs) {
		
		OnboardingData data = new OnboardingData();
		
		// If we do quick onboarding, we create a new waiting person directly
		if(personOnTheFly != null) {
			
			//create waiting person + member data and persist
			setValuesOfPersonAndPersist();			
			
		} else {

			// Initialize OnboardingData
			data.setAssociationId(onboardingToken.get().getAssociationId());
			data.setDateOfBirth(dateOfBirth.getValue());
			data.setEmail(email.getValue());
			data.setFirstName(firstName.getValue());
			data.setLastName(lastName.getValue());
			data.setPhone(phone.getValue());

			// If onboarding token exists, a waiting person must already exist
			data.setMemberNumber(onboardingToken.get().getWaintingPerson().getId());

			// Initialize MemberData
			MemberData memberData = new MemberData();
			memberData.setCityName(city.getValue());
			memberData.setDateOfRegistration(LocalDate.now());
			memberData.setPostalCode(Integer.valueOf(postalCode.getValue()));
			memberData.setStreetName(streetName.getValue());
			memberData.setStreetNumber(streetNumber.getValue());

			// persist MemberData
			memberData = memberDataService.update(memberData);
			Optional<MemberData> optMemberData = memberDataService.findById(memberData.getId());

			// add MemberData to OnboardingData
			optMemberData.ifPresentOrElse(e -> {
				data.setMemberData(e);
			}, () -> {
				Notification notification = Notification
						.show("Fehler beim Senden der E-Mail - kontaktiere am Besten den Support!");
				notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
			});
			
			// Add answers to OnboardingData
			inputs.keySet().forEach(question -> {
				OnboardingAnswer answer = new OnboardingAnswer();
				answer.setAssociationId(onboardingToken.get().getAssociationId());
				answer.setQuestion(question);
				answer.setAnswer(inputs.get(question).getValue());
				tmpAnswers.add(answer);
			});
			
			data.setAnswers(tmpAnswers);
			
			// persist OnboardingData
			onboardingDataService.update(data);
			
			// delete OnboardingToken since it has no use anymore
			if (onboardingToken.isPresent()) {
				onboardingTokenService.delete(onboardingToken.get().getId());
			}
			
			// update OnboardingStatus of WaitingPerson
			Optional<WaitingPerson> optWaitingPerson = waitingPersonService
					.get(onboardingToken.get().getWaintingPerson().getId());
			optWaitingPerson.ifPresent(person -> {
				person.setOnboardingStatus(OnboardingStatus.DATA_PROVIDED);
				waitingPersonService.update(person);
			});
		}

	}
	
	private void setValuesOfPersonAndPersist() {
		
		//fill object with data from wizard
		personOnTheFly.setDateOfBirth(this.dateOfBirth.getValue());
		personOnTheFly.setEmail(this.email.getValue());
		personOnTheFly.setFirstName(this.firstName.getValue());
		personOnTheFly.setLastName(this.lastName.getValue());
		personOnTheFly.setPhone(this.phone.getValue());
		
		//before waiting person can be saved, a MemberData object must exist
		personData.setCityName(this.city.getValue());
		personData.setPostalCode(Integer.valueOf(this.postalCode.getValue()));
		personData.setStreetName(this.streetName.getValue());
		personData.setStreetNumber(this.streetNumber.getValue());
		
		//set MemberData
		personOnTheFly.setMemberData(personData);
		personOnTheFly.setAssociationId(associationId);
		personOnTheFly.setOnboardingStatus(OnboardingStatus.CAN_BE_MEMBER);
		
		//persist waiting person
		personOnTheFly = waitingPersonService.update(personOnTheFly);
		
	}

	private void sendNextStepsEmailToWaitingPerson() {

		WaitingPerson waintingPerson = onboardingToken.isPresent() ? onboardingToken.get().getWaintingPerson() : personOnTheFly;
		String to = waintingPerson.getEmail();
		String subject = "Onboarding -" + " Nächste Schritte";

		EmailService emailService = new EmailService();
		try {
			emailService.sendOnboardingFinishedEmail(to, subject, waintingPerson.getFirstName());
		} catch (Exception e) {
			Notification notification = Notification.show("Fehler beim Senden der E-Mail: " + e.getMessage());
			notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
		}
	}

	public void resetWizzard() {
		
		firstName.setValue(firstName.getEmptyValue());
		lastName.setValue(lastName.getEmptyValue());
		email.setValue(email.getEmptyValue());
		phone.setValue(phone.getEmptyValue());
		dateOfBirth.setValue(dateOfBirth.getEmptyValue());
		confirmAgeBox.setValue(false);		
		streetName.setValue(streetName.getEmptyValue());
		streetNumber.setValue(streetNumber.getEmptyValue());
		postalCode.setValue(postalCode.getEmptyValue());
		city.setValue(city.getEmptyValue());
		confirmAddressBox.setValue(false);
		confirmGeneralDataBox.setValue(false);
		
		//if onboarding was finished, we need to add begin tab
		if (tabEnd != null) {
			add(tabBegin, beginWrapper);
			setSelectedTab(tabBegin);
			remove(0);
		}
		
		//if onboarding was not finished, remove tabs
		if (!tabBegin.isSelected()) {
			add(tabBegin, beginWrapper);
			setSelectedTab(tabBegin);
			
			remove(tabStepOne);
			remove(tabStepTwo);
			remove(tabStepThree);
			remove(tabStepFour);
			remove(tabQuestions);
		}
		
		//add tabs again to get them in the right order
		add(tabStepOne, stepOneWrapper);
		tabStepOne.setEnabled(false);

		add(tabStepTwo, stepTwoWrapper);
		tabStepTwo.setEnabled(false);

		add(tabStepThree, stepThreeWrapper);
		tabStepThree.setEnabled(false);

		add(tabStepFour, stepFourWrapper);
		tabStepFour.setEnabled(false);
		
		//if questions exist
		if (tabQuestions != null) {
			add(tabQuestions, questionsWrapper);
			tabQuestions.setEnabled(false);
		}
		
		setSelectedIndex(-1);
		setSelectedIndex(0);
	}
	
}
