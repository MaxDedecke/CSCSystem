package com.css.one.views.waitinglist;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.vaadin.lineawesome.LineAwesomeIcon;

import com.css.one.components.CompareDataComponent;
import com.css.one.components.OnboardingWizzard;
import com.css.one.data.AssociationSettings;
import com.css.one.data.MemberData;
import com.css.one.data.MemberSubscription;
import com.css.one.data.OnboardingData;
import com.css.one.data.OnboardingToken;
import com.css.one.data.PasswordResetToken;
import com.css.one.data.Person;
import com.css.one.data.User;
import com.css.one.data.WaitingPerson;
import com.css.one.data.enums.AssociationRole;
import com.css.one.data.enums.EmailType;
import com.css.one.data.enums.ExpirationTime;
import com.css.one.data.enums.OnboardingStatus;
import com.css.one.data.enums.Role;
import com.css.one.services.AssociationSettingsService;
import com.css.one.services.EmailService;
import com.css.one.services.MemberDataService;
import com.css.one.services.MemberSubscriptionService;
import com.css.one.services.OnboardingDataService;
import com.css.one.services.OnboardingQuestionService;
import com.css.one.services.OnboardingTokenService;
import com.css.one.services.PasswordResetTokenService;
import com.css.one.services.PersonService;
import com.css.one.services.SubscriptionModelService;
import com.css.one.services.UserService;
import com.css.one.services.WaitingPersonService;
import com.css.one.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.PermitAll;

@PageTitle("Wartebereich")
@PermitAll
@Route(value = "wartebereich/", layout = MainLayout.class)
@RouteAlias(value = "wartebereich", layout = MainLayout.class)

public class WaitingListView extends FlexLayout {

	/**
	* 
	*/
	private static final long serialVersionUID = -5000521119703456571L;

	private final Grid<WaitingPerson> grid = new Grid<>(WaitingPerson.class, false);

	private ComboBox<AssociationRole> comboBox = new ComboBox<AssociationRole>("Rolle");

	private TextField firstName;
	private TextField lastName;
	private TextField email;
	private TextField phone;
	private DatePicker dateOfBirth;
	private TextField nameOfFutureMember = new TextField("Name der Person");

	private TextField streetName;
	private TextField streetNumber;
	private TextField postalCode;
	private TextField city;

	private OnboardingWizzard wizzard;

	private Button save = new Button("speichern");

	private H3 memberCount;
	private H3 memberCountNumber;
	private H2 headerPersonInfo;

	private Dialog newMemberDialog = new Dialog();
	private Dialog personInfoDialog = new Dialog();
	private Dialog quickOnboardingDialog = new Dialog();
	private Dialog compareDataDialog = new Dialog();

	private CompareDataComponent compareDataComponent;

	private WaitingPerson waitingPerson;
	private PersonService personService;
	private MemberSubscriptionService subscriptionService;
	private MemberDataService memberDataService;
	private OnboardingTokenService onboardingTokenService;
	private AssociationSettingsService associationSettingsService;
	private OnboardingDataService onboardingDataService;

	private SubscriptionModelService subscriptionModelService;
	private OnboardingQuestionService onboardingQuestionService;
	private WaitingPersonService waitingPersonService;
	private UserService userService;
	private PasswordResetTokenService passwordResetTokenService;

	private int associationId;
	private boolean isNewPerson = true;

	private EmailService emailService;

	public WaitingListView(WaitingPersonService waitingPersonService, PersonService personService,
			MemberSubscriptionService subscriptionService, MemberDataService memberDataService,
			OnboardingTokenService onboardingTokenService, AssociationSettingsService associationSettingsService,
			SubscriptionModelService subscriptionModelService, OnboardingDataService onboardingDataService,
			OnboardingQuestionService onboardingQuestionService, UserService userService, PasswordResetTokenService passwordResetTokenService) {

		this.waitingPersonService = waitingPersonService;
		this.personService = personService;
		this.subscriptionService = subscriptionService;
		this.memberDataService = memberDataService;
		this.onboardingTokenService = onboardingTokenService;
		this.associationSettingsService = associationSettingsService;
		this.onboardingDataService = onboardingDataService;
		this.subscriptionModelService = subscriptionModelService;
		this.onboardingQuestionService = onboardingQuestionService;
		this.userService = userService;
		this.passwordResetTokenService = passwordResetTokenService;
		
		addClassNames("waitinglist-view");

		// Create UI
		associationId = MainLayout.getAssociationId();
		
		emailService = new EmailService();

		VerticalLayout mainWrapper = new VerticalLayout();
		mainWrapper.addClassNames(LumoUtility.Margin.NONE, LumoUtility.Margin.Bottom.MEDIUM, LumoUtility.Padding.NONE);
		createMainLayout(mainWrapper);

		createAddPersonDialog();
		createNewMemberDialog();
		createCompareDataDialog();
		createQuickOnboardingDialog();

		add(mainWrapper);
	}

	private void createQuickOnboardingDialog() {

		VerticalLayout headerWrapper = new VerticalLayout();
		headerWrapper.setWidthFull();
		H3 headerQuickOnboarding = new H3("Quick Onboarding");
		headerWrapper.add(headerQuickOnboarding);

		VerticalLayout wizzardWrapper = new VerticalLayout();

		wizzard = new OnboardingWizzard(onboardingDataService, subscriptionModelService, onboardingTokenService,
				onboardingQuestionService, memberDataService, waitingPersonService, Optional.empty(),
				Optional.of(associationId));

		wizzardWrapper.add(wizzard);

		quickOnboardingDialog.add(headerWrapper, wizzardWrapper);

		Button closeButton = new Button("Prozess abbrechen");
		closeButton.addClassName("cancel-button");
		closeButton.addClickListener(e -> {
			wizzard.resetWizzard();
			quickOnboardingDialog.close();
			refreshLayout();
			closeButton.setText("Prozess abbrechen");
		});

		wizzard.setActionToPerform(() -> {
			closeButton.setText("zurück");
		});

		quickOnboardingDialog.addDialogCloseActionListener(e -> {
			wizzard.resetWizzard();
		});

		quickOnboardingDialog.setWidth("80%");
		quickOnboardingDialog.setHeight("90%");
		quickOnboardingDialog.getFooter().add(closeButton);
	}

	private void createCompareDataDialog() {
		// compare data given before the process with given by person
		compareDataComponent = new CompareDataComponent();
		compareDataDialog.add(compareDataComponent);

		Button closeButton = new Button("zurück");
		closeButton.addClassName("cancel-button");
		closeButton.addClickListener(e -> {
			compareDataComponent.clearComponent();
			compareDataDialog.close();
		});

		Button saveButton = new Button("abschließen");
		saveButton.addClassName("save-button");
		saveButton.addClickListener(e -> {

			WaitingPerson returnedPersonInfo = compareDataComponent.returnPersonWithFinalInfo();
			waitingPersonService.update(returnedPersonInfo);

			compareDataDialog.close();
			compareDataComponent.clearComponent();

			refreshLayout();

			Notification notification = Notification.show("Daten wurden aktualisiert.");
			notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
		});

		compareDataDialog.getFooter().add(closeButton, saveButton);
		compareDataDialog.setMaxWidth("65%");
		compareDataDialog.setMaxHeight("75%");
	}

	private void createAddPersonDialog() {
		VerticalLayout mainWrapper = new VerticalLayout();

		headerPersonInfo = new H2("Person hinzufügen");
		headerPersonInfo.addClassName("customheader");

		mainWrapper.add(headerPersonInfo, createAddPersonContent(), new Hr(), createAdditionalDataContent());

		save = new Button("hinzufügen");
		save.addClassNames("save-button");

		save.addClickListener(e -> {

			if (validateData()) {
				savePersonData();

				if (isNewPerson) {
					Notification notification = Notification.show("Person wurde auf die Warteliste gesetzt.");
					notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
				} else {
					Notification notification = Notification.show("Infos aktualisiert");
					notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
				}

				isNewPerson = true;
			}
		});

		Button buttonCancel = new Button("abbrechen");
		buttonCancel.addClassNames("cancel-button");

		buttonCancel.addClickListener(e -> {
			clearPersonInfoDialog();
			personInfoDialog.close();
		});

		personInfoDialog.addDialogCloseActionListener(e -> {
			clearPersonInfoDialog();
			personInfoDialog.close();
		});

		personInfoDialog.getFooter().add(buttonCancel, save);
		personInfoDialog.add(mainWrapper);
	}

	private boolean validateData() {

		if (dateOfBirth.getValue() == null) {
			return false;
		}

		if (email.getValue().equals(email.getEmptyValue())) {
			return false;
		}

		if (firstName.getValue().equals(firstName.getEmptyValue())) {
			return false;
		}

		if (lastName.getValue().equals(lastName.getEmptyValue())) {
			return false;
		}

		if (phone.getValue().equals(phone.getEmptyValue())) {
			return false;
		}

		if (streetName.getValue().equals(streetName.getEmptyValue())) {
			return false;
		}

		if (streetNumber.getValue().equals(streetNumber.getEmptyValue())) {
			return false;
		}

		if (city.getValue().equals(city.getEmptyValue())) {
			return false;
		}

		if (postalCode.getValue().equals(postalCode.getEmptyValue())) {
			return false;
		}

		return true;
	}

	private Component createAdditionalDataContent() {

		VerticalLayout wrapper = new VerticalLayout();
		H3 h3 = new H3("Addressdaten");
		h3.addClassName("customheader");

		FormLayout additionalFormLayout = new FormLayout();

		streetName = new TextField("Straße");
		streetName.setAllowedCharPattern("[a-zA-ZäöüÄÖÜß\\-\\s']");

		streetNumber = new TextField("Hausnummer");
		streetNumber.setAllowedCharPattern("[0-9a-zA-Z]");

		postalCode = new TextField("PLZ");
		postalCode.setAllowedCharPattern("\\d");

		city = new TextField("Ort");
		city.setAllowedCharPattern("[a-zA-ZäöüÄÖÜß\\-\\s']");

		additionalFormLayout.add(streetName, streetNumber, postalCode, city);

		wrapper.addClassNames(LumoUtility.Padding.NONE, LumoUtility.Margin.NONE);
		wrapper.add(h3, additionalFormLayout);
		return wrapper;
	}

	private void savePersonData() {

		// input validation
		if (checkPersonDataForWaitingList()) {

			Optional<MemberData> memberDataOptional = Optional.empty();

			// new person
			if (this.waitingPerson == null) {
				this.waitingPerson = new WaitingPerson();
			} else {
				// if person already exists, look for its MemberData
				memberDataOptional = memberDataService.findByMember(this.waitingPerson);
			}

			this.waitingPerson.setAssociationId(associationId);
			this.waitingPerson.setDateOfBirth(dateOfBirth.getValue());
			this.waitingPerson.setDateOfRegistration(LocalDate.now());
			this.waitingPerson.setEmail(email.getValue());
			this.waitingPerson.setFirstName(firstName.getValue());
			this.waitingPerson.setLastName(lastName.getValue());
			this.waitingPerson.setPhone(phone.getValue());

			MemberData memberData;

			// if MemberData exists, use it
			if (memberDataOptional.isPresent()) {
				memberData = memberDataOptional.get();
			} else {
				memberData = new MemberData();
			}

			memberData.setStreetName(streetName.getValue());
			memberData.setCityName(city.getValue());
			memberData.setPostalCode(Integer.parseInt(postalCode.getValue()));
			memberData.setStreetNumber(streetNumber.getValue());

			waitingPerson.setMemberData(memberData);

			// persist person with its info and MemberData
			waitingPersonService.update(this.waitingPerson);

			// clear and refresh
			clearPersonInfoDialog();
			refreshLayout();
			personInfoDialog.close();
			this.waitingPerson = null;

		} else {
			Notification notification = Notification.show("Das war leider nicht erfolgreich...");
			notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
		}
	}

	private void createSingleSubscriptionForNewMember(Person member) {

		LocalDate now = LocalDate.now();
		MemberSubscription subscription = new MemberSubscription();
		subscription.setAssociationId(associationId);
		subscription.setMonth(now.getMonthValue());
		subscription.setYear(now.getYear());
		subscription.setPersonId(member.getId().intValue());
		subscription.setPayed(false);

		subscriptionService.update(subscription);
	}

	private boolean checkPersonDataForWaitingList() {
		boolean isDataOk = true;

		if (firstName.getValue().equals("")) {
			isDataOk = false;
			Notification notification = Notification.show("Die Person braucht einen Vornamen");
			notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
			return isDataOk;
		}

		if (lastName.getValue().equals("")) {
			isDataOk = false;
			Notification notification = Notification.show("Die Person braucht einen Nachnamen");
			notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
			return isDataOk;
		}

		if (email.getValue().equals("")) {
			isDataOk = false;
			Notification notification = Notification.show("Die Person braucht eine Email");
			notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
			return isDataOk;
		}

		if (phone.getValue().equals("")) {
			isDataOk = false;
			Notification notification = Notification.show("Die Person braucht eine Telefonnummer");
			notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
			return isDataOk;
		}

		if (dateOfBirth.getValue() == null) {
			isDataOk = false;
			Notification notification = Notification.show("Die Person braucht ein Geburtsdatum");
			notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
			return isDataOk;
		}

		LocalDate date = dateOfBirth.getValue();
		LocalDate now = LocalDate.now();

		if (!(now.getYear() - date.getYear() > 20)) {
			isDataOk = false;
			Notification notification = Notification.show("Die Person ist noch nicht volljährig!");
			notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
			return isDataOk;
		} else {
			if (now.getYear() - date.getYear() == 21) {
				if (!(now.getMonth().getValue() >= date.getMonth().getValue())) {
					isDataOk = false;
					Notification notification = Notification.show("Die Person ist noch nicht 21 Jahre alt!");
					notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
					return isDataOk;
				} else {
					if (now.getMonth().getValue() == date.getMonth().getValue()) {
						if (!(now.getDayOfMonth() >= date.getDayOfMonth())) {
							isDataOk = false;
							Notification notification = Notification.show("Es fehlen nur noch ein paar Tage!");
							notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
							return isDataOk;
						}
					}
				}
			}
		}
		return isDataOk;
	}

	private String renderDate(LocalDate date) {
		String day = "";
		String month = "";

		if (date.getDayOfMonth() < 10) {
			day = "0" + String.valueOf(date.getDayOfMonth());
		} else {
			day = String.valueOf(date.getDayOfMonth());
		}

		if (date.getMonthValue() < 10) {
			month = "0" + String.valueOf(date.getMonthValue());
		} else {
			month = String.valueOf(date.getMonthValue());
		}

		return day + "." + month + "." + date.getYear();
	}

	private void createNewMemberDialog() {

		VerticalLayout dialogLayout = new VerticalLayout();
		H2 h2 = new H2("Zu Mitgliedern hinzufügen");
		h2.addClassNames("customheader");
		dialogLayout.add(h2);

		FormLayout formLayout = new FormLayout();

		comboBox.setItems(AssociationRole.values());
		comboBox.setItemLabelGenerator(e -> e.getLabel());

		formLayout.add(nameOfFutureMember, comboBox);
		dialogLayout.add(formLayout);

		newMemberDialog.add(dialogLayout);

		Button saveButton = new Button("Hinzufügen", e -> {

			if (checkBeforeSave(comboBox, nameOfFutureMember)) {
				
				Person person = createNewPersonObject(comboBox);

				Optional<MemberData> optMemberData = memberDataService.findByMember(waitingPerson);
				
				if (optMemberData.isPresent()) {
					person.setMemberData(createNewMemberDataObject(optMemberData.get()));
					personService.update(person);
				}

				if (waitingPerson.getId() != null) {
					waitingPersonService.delete(waitingPerson.getId());
				}
				this.waitingPerson = null;

				// create SubscriptionModel
				createSingleSubscriptionForNewMember(person);

				// create account data
				createUserAccount(person);

				newMemberDialog.close();
				refreshLayout();

				Notification notification = Notification.show("Neues Mitglied hinzugefügt.");
				notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

				nameOfFutureMember.setValue("");
			}
		});
		saveButton.addClassNames("save-button");

		Button cancelButton = new Button("Zurück", e -> newMemberDialog.close());
		cancelButton.addClassNames("cancel-button");

		newMemberDialog.getFooter().add(cancelButton);
		newMemberDialog.getFooter().add(saveButton);
	}

	private MemberData createNewMemberDataObject(MemberData memberData) {
		
		MemberData newData = new MemberData();
		
		newData.setCityName(memberData.getCityName());
		newData.setDateOfRegistration(memberData.getDateOfRegistration());
		newData.setPostalCode(memberData.getPostalCode());
		newData.setStreetName(memberData.getStreetName());
		newData.setStreetNumber(memberData.getStreetNumber());
		
		newData = memberDataService.update(newData);
		
		return newData;
	}

	private Person createNewPersonObject(ComboBox<AssociationRole> comboBox) {
		
		Person person = new Person();
		person.setAssociationId(associationId);
		person.setAssociationRole(comboBox.getValue());
		person.setDateOfBirth(waitingPerson.getDateOfBirth());
		person.setEmail(waitingPerson.getEmail());
		person.setPhone(waitingPerson.getPhone());
		person.setFirstName(waitingPerson.getFirstName());
		person.setLastName(waitingPerson.getLastName());
		person.setMemberNumber(personService.getFreeMemberNumber(associationId));
		person = personService.update(person);
		
		return person;
	}

	private void createUserAccount(Person person) {

		// create a new account for the login of a member
		User newUser = new User();
		newUser.setAssociationId(associationId);
		newUser.setProfilePicture(null);
		newUser.setName(person.getFirstName() + " " + person.getLastName());
		newUser.setHashedPassword(BCrypt.hashpw("123456", BCrypt.gensalt()));
		newUser.setUsername(person.getFirstName().charAt(0) + person.getLastName());
		newUser.setEntityId(person.getId());

		newUser.setRoles(setRoles());

		// save new account
		newUser = userService.update(newUser);

		String to = person.getEmail();
		String subject = "Jetzt ist es offiziell - " + " Herzlich willkommen in deinem Cannabis Social Club!";

		try {	
			
			emailService.sendFirstPasswordEmail(to, subject, newUser.getName(), generateTokenForPasswordReset(newUser));
		} catch (Exception e) {
			
			Notification note = new Notification("Fehler beim Versenden einer Email an das Mitglied. Bitte kontaktiere den Support!");
			note.addThemeVariants(NotificationVariant.LUMO_ERROR);
			
			e.printStackTrace();
		} 

	}

	private Set<Role> setRoles() {
		
		HashSet<Role> rolesOfUser = new HashSet<Role>();
		
		if(comboBox.getValue().equals(AssociationRole.BOARD)) {
			rolesOfUser.add(Role.ADMIN);
		}
		
		if(comboBox.getValue().equals(AssociationRole.ACCOUNTANT)) {
			rolesOfUser.add(Role.FINANCE_OFFICER);
		}
		
		if(comboBox.getValue().equals(AssociationRole.PREVENTION)) {
			rolesOfUser.add(Role.MEMBER);
		}
		
		if(comboBox.getValue().equals(AssociationRole.MEMBER)) {
			rolesOfUser.add(Role.MEMBER);
		}
		
		return rolesOfUser;
	}

	private String generateTokenForPasswordReset(User user) {

		String token = passwordResetTokenService.generateToken();
		
		PasswordResetToken resetToken = new PasswordResetToken();
		resetToken.setAssociationId(associationId);
		resetToken.setExpirationDate(LocalDate.now().plusMonths(1));
		resetToken.setToken(token);
		resetToken.setUser(user);	
		
		resetToken = passwordResetTokenService.update(resetToken);
		return token;
	}

	private boolean checkBeforeSave(ComboBox<AssociationRole> comboBox, TextField nameOfPerson) {
		boolean returnValue = true;

		if (comboBox.getValue() == null) {
			Notification notification = Notification.show("Einer Person muss eine Rolle zugewiesen werden");
			notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
			return false;
		}
		if (personService.count() >= 500) {
			Notification notification = Notification.show("Die maximale Anzahl an Mitgliedern ist bereits erreicht !");
			notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
			return false;
		}
		if (nameOfPerson.getValue().equals("")) {
			Notification notification = Notification.show("Die Person braucht noch einen Namen !");
			notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
			return false;
		}

		return returnValue;
	}

	private void refreshLayout() {
		grid.select(null);
		List<WaitingPerson> allWaitingPeople = waitingPersonService.findAllByAssociation(associationId);
		grid.setItems(allWaitingPeople);

		// refresh counter
		memberCountNumber.setText("Wartendene Personen: " + String.valueOf(allWaitingPeople.size()));
		
	}

	private Component createAddPersonContent() {

		VerticalLayout wrapper = new VerticalLayout();
		H3 h3 = new H3("Allgemeine Angaben");
		h3.addClassName("customheader");

		FormLayout formLayout = new FormLayout();
		firstName = new TextField("Vorname");
		firstName.setAllowedCharPattern("[a-zA-ZäöüÄÖÜß\\-']");
		
		lastName = new TextField("Nachname");
		lastName.setAllowedCharPattern("[a-zA-ZäöüÄÖÜß\\-']");

		email = new TextField("Email");
		email.setAllowedCharPattern("[a-zA-Z0-9._%+-@]");

		phone = new TextField("Telefonnummer");
		phone.setAllowedCharPattern("[+\\d]");
		
		dateOfBirth = new DatePicker("Geburtstag");
		dateOfBirth.setOverlayClassName("waiting-list-view-date-picker-1");
		dateOfBirth.addClassName("waiting-list-view-date-picker-1");
		dateOfBirth.setAllowedCharPattern("[0-9.]");

		formLayout.add(firstName, lastName, phone, dateOfBirth, email);
		formLayout.setColspan(email, 2);

		wrapper.addClassNames(LumoUtility.Padding.NONE, LumoUtility.Margin.NONE);
		wrapper.add(h3, formLayout);
		return wrapper;
	}

	private void createMainLayout(VerticalLayout wrapper) {

		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.addClassNames(LumoUtility.Padding.NONE, LumoUtility.Padding.NONE);

		createGrid();

		mainLayout.add(createButtonAndInfoComponent(), grid);
		wrapper.add(mainLayout);
	}

	private Component createButtonAndInfoComponent() {
		HorizontalLayout wrapper = new HorizontalLayout();
		wrapper.setWidthFull();
		wrapper.addClassNames("header-bar-custom");

		Button buttonAddPerson = new Button("Person hinzufügen");
		buttonAddPerson.setIcon(LineAwesomeIcon.USER_PLUS_SOLID.create());
		buttonAddPerson.addClassName("button-neutral");

		buttonAddPerson.addClickListener(e -> {
			headerPersonInfo.setText("Person hinzufügen");
			this.waitingPerson = null;
			personInfoDialog.open();
		});

		Button buttonStartPersonOnboarding = new Button("Quick Onboarding");
		buttonStartPersonOnboarding.setIcon(LineAwesomeIcon.CLONE_SOLID.create());
		buttonStartPersonOnboarding.addClassName("button-neutral");

		buttonStartPersonOnboarding.addClickListener(e -> {
			quickOnboardingDialog.open();
		});

		FlexLayout flexWrapper = new FlexLayout();
		flexWrapper.addClassNames(LumoUtility.AlignItems.END);
		flexWrapper.setWidthFull();

		VerticalLayout secondWrapper = new VerticalLayout();
		secondWrapper.addClassNames(LumoUtility.JustifyContent.END, LumoUtility.AlignContent.END);
		secondWrapper.setSpacing(false);
		secondWrapper.setPadding(false);

		VerticalLayout innerWrapper = new VerticalLayout();
		innerWrapper.addClassNames(LumoUtility.Border.LEFT, LumoUtility.BorderRadius.NONE, "padding-extra-top");
		innerWrapper.setSpacing(false);
		innerWrapper.setPadding(false);

		HorizontalLayout bottomLayout = new HorizontalLayout();
		bottomLayout.addClassNames(LumoUtility.Padding.NONE, LumoUtility.Padding.Left.MEDIUM, LumoUtility.Margin.NONE);
		memberCount = new H3("Statistik");
		memberCount.addClassNames("header-statistics", "no-extra-space");
		bottomLayout.add(memberCount);

		HorizontalLayout statisticsLayout = new HorizontalLayout();
		statisticsLayout.addClassNames(LumoUtility.Padding.NONE, LumoUtility.Padding.Left.MEDIUM,
				LumoUtility.Margin.NONE, "header-statistics-item");
		memberCountNumber = new H3(
				"Wartendene Personen: " + waitingPersonService.findAllByAssociation(associationId).size());
		memberCountNumber.addClassName("no-extra-space");
		statisticsLayout.add(memberCountNumber);

		innerWrapper.add(bottomLayout, statisticsLayout);
		flexWrapper.add(secondWrapper, innerWrapper);

		wrapper.add(buttonAddPerson, buttonStartPersonOnboarding, flexWrapper);
		flexWrapper.setFlexGrow(3, secondWrapper);
		flexWrapper.setFlexGrow(2, innerWrapper);
		return wrapper;
	}

	private void createGrid() {
		// Configure Grid
		grid.addColumn(p -> p.getFirstName() + " " + p.getLastName()).setAutoWidth(true).setHeader("Name");
		grid.addColumn(p -> p.getEmail()).setAutoWidth(true).setHeader("Email");
		grid.addColumn(p -> p.getPhone()).setAutoWidth(true).setHeader("Telefonnummer");
		grid.addColumn(p -> renderDate(p.getDateOfRegistration())).setAutoWidth(true).setHeader("Auf Warteliste seit")
				.setSortable(true);

		grid.addComponentColumn(status -> {
			VerticalLayout wrapper = new VerticalLayout();

			if (status.getOnboardingStatus() != null) {
				ProgressBar bar = new ProgressBar();

				NativeLabel progressBarLabelText;

				if (status.getOnboardingStatus() == OnboardingStatus.STARTED) {
					bar.addClassNames("onboarding-started");
					bar.setValue(0.3);
					progressBarLabelText = new NativeLabel("Onboarding gestartet");
					progressBarLabelText.setId("pblabel");
					bar.getElement().setAttribute("aria-labelledby", "pblabel");
				} else if (status.getOnboardingStatus() == OnboardingStatus.DATA_PROVIDED) {
					bar.addClassNames("onboarding-data-exists");
					bar.setValue(0.6);
					progressBarLabelText = new NativeLabel("Selbstauskunft ausgefüllt");
					progressBarLabelText.setId("pblabel");
					bar.getElement().setAttribute("aria-labelledby", "pblabel");
				} else {
					// Status finished
					bar.addClassNames("onboarding-finished");
					bar.setValue(1.0);
					progressBarLabelText = new NativeLabel("Onboarding abgeschlossen");
					progressBarLabelText.setId("pblabel");
					bar.getElement().setAttribute("aria-labelledby", "pblabel");
				}

				HorizontalLayout progressBarLabel = new HorizontalLayout(progressBarLabelText);
				progressBarLabel.setJustifyContentMode(JustifyContentMode.BETWEEN);

				wrapper.add(progressBarLabel, bar);
				return wrapper;
			} else {
				wrapper.add(new Span("-"));
				return wrapper;
			}
		}).setWidth("75px").setAutoWidth(false);

		grid.addComponentColumn(person -> {
			return createMenuForPerson(person);
		}).setWidth("100px").setFlexGrow(0);

		grid.setItems(waitingPersonService.findAllByAssociation(associationId));
		grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

		// when a row is selected or deselected, populate form
		grid.asSingleSelect().addValueChangeListener(event -> {
			if (event.getValue() != null) {
				this.waitingPerson = event.getValue();
			} else {
				this.waitingPerson = null;
			}
		});

	}

	private Component createMenuForPerson(WaitingPerson person) {
		MenuBar menuBar = new MenuBar();
		menuBar.setOverlayClassName("warenlager-view-menu-bar-1");
		menuBar.addClassNames("warenlager-view-menu-bar-1", "customheader");

		menuBar.addItem("bearbeiten", event -> {
			this.waitingPerson = person;
			isNewPerson = false;
			headerPersonInfo.setText("Infos zur Person");
			initPersonInfoDialog();
			personInfoDialog.open();
		});

		menuBar.addItem("Person löschen", event -> {
			Optional<OnboardingToken> optToken = onboardingTokenService.findByWaitingPerson(person);
			optToken.ifPresent(e -> {

				// delete OnboardingData if exists
				Optional<OnboardingData> dataByToken = onboardingDataService.findByToken(e.getId());
				dataByToken.ifPresent(data -> onboardingDataService.delete(data.getId()));

				// delete OnboardingToken if exists
				onboardingTokenService.delete(e.getId());
			});

			// delete person on waiting list after all other objects referencing are deleted
			waitingPersonService.delete(person.getId());
			Notification notification = Notification.show("Person von der Warteliste gelöscht");
			notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

			refreshLayout();
		});

		if (person.getOnboardingStatus() == null) {

			menuBar.addItem("Onboarding starten", event -> {
				if (person.getEmail() != null) {
					sendOnboardingEmail(person);
				}
			});
		} else {

			if (person.getOnboardingStatus() != OnboardingStatus.CAN_BE_MEMBER) {
				menuBar.addItem("Einladung erneut senden", event -> {

					if (person.getOnboardingStatus() == OnboardingStatus.STARTED) {
						if (person.getEmail() != null) {
							Optional<OnboardingToken> optToken = onboardingTokenService.findByWaitingPerson(person);

							if (optToken.isPresent()) {

								// delete old OnboardingData if already existing
								Optional<OnboardingData> dataByToken = onboardingDataService
										.findByToken(optToken.get().getId());
								dataByToken.ifPresent(e -> onboardingDataService.delete(e.getId()));

								// then delete old token
								onboardingTokenService.delete(optToken.get().getId());

								// send new email and generate new token
								sendOnboardingEmail(person);
							}
						}
					} else if (person.getOnboardingStatus() == OnboardingStatus.DATA_PROVIDED) {

						// delete OnboardingData and send new email
						Optional<OnboardingData> dataOfPerson = onboardingDataService.findByMemberId(person.getId());
						dataOfPerson.ifPresentOrElse(data -> {
							onboardingDataService.delete(data.getId());

							sendOnboardingEmail(person);
						}, () -> {
							Notification notification = Notification.show("Fehler: Erneutes Onboarding nicht möglich!");
							notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
						});

					} else {
						Notification notification = Notification.show("Fehler: Problem mit Onboarding Token!");
						notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
					}
				});
			}

			// if person has already provided his information
			if (person.getOnboardingStatus() == OnboardingStatus.DATA_PROVIDED) {

				menuBar.addItem("Angaben vergleichen", event -> {
					// prepare data and open pop up
					Optional<OnboardingData> dataByMemberId = onboardingDataService.findByMemberId(person.getId());

					if (dataByMemberId.isPresent()) {
						compareDataComponent.initDataLeftSide(person);
						compareDataComponent.initDataRightSide(dataByMemberId.get());
						compareDataDialog.open();
					} else {
						Notification notification = Notification
								.show("Fehler beim Laden der Daten der Person. Kontaktiere bitte den Support !");
						notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
					}
				});
			}

			if (person.getOnboardingStatus() == OnboardingStatus.CAN_BE_MEMBER) {

				menuBar.addItem("Zum Mitglied machen", event -> {
					this.waitingPerson = person;
					nameOfFutureMember.setValue(waitingPerson.getFirstName() + " " + waitingPerson.getLastName());
					newMemberDialog.open();
				});
			}
		}

		return menuBar;
	}

	private void initPersonInfoDialog() {

		this.firstName.setValue(waitingPerson.getFirstName());
		this.lastName.setValue(waitingPerson.getLastName());
		this.email.setValue(waitingPerson.getEmail());
		this.phone.setValue(waitingPerson.getPhone());
		this.dateOfBirth.setValue(waitingPerson.getDateOfBirth());

		if (waitingPerson.getMemberData() != null) {
			memberDataService.findByMember(waitingPerson).ifPresent(memberData -> {

				this.city.setValue(memberData.getCityName());
				this.streetName.setValue(memberData.getStreetName());
				this.streetNumber.setValue(memberData.getStreetNumber());
				this.postalCode.setValue(String.valueOf(memberData.getPostalCode()));
			});
		}

		this.save.setText("aktualisieren");
	}

	private void clearPersonInfoDialog() {

		this.firstName.setValue("");
		this.lastName.setValue("");
		this.email.setValue("");
		this.phone.setValue("");
		this.city.setValue("");
		this.streetName.setValue("");
		this.streetNumber.setValue("");
		this.postalCode.setValue("");
		this.dateOfBirth.setValue(dateOfBirth.getEmptyValue());

		this.save.setText("hinzufügen");
	}

	private void sendOnboardingEmail(WaitingPerson person) {

		String to = person.getEmail();
		String subject = "Onboarding gestartet - " + " Herzlich willkommen " + person.getFirstName() + " "
				+ person.getLastName() + "!";

		try {
			String token = emailService.sendOnboardingEmail(to, subject, person.getFirstName(), EmailType.ONBOARING,
					onboardingTokenService.generateToken());
			saveToken(token, person);

			Notification notification = Notification.show("E-Mail erfolgreich gesendet");
			notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
			person.setOnboardingStatus(OnboardingStatus.STARTED);
			waitingPersonService.update(person);
			refreshLayout();
		} catch (Exception e) {
			Notification notification = Notification.show("Fehler beim Senden der E-Mail: " + e.getMessage());
			notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
		}
	}

	private void saveToken(String token, WaitingPerson person) {
		OnboardingToken onboardingToken = new OnboardingToken();
		onboardingToken.setToken(token);
		onboardingToken.setWaintingPerson(person);
		onboardingToken.setExpirationDate(createExpirationDate());
		onboardingToken.setAssociationId(associationId);
		onboardingTokenService.update(onboardingToken);
	}

	private LocalDate createExpirationDate() {
		Optional<AssociationSettings> optOnboardingSettings = associationSettingsService
				.findAllByAssociation(associationId);

		if (optOnboardingSettings.isPresent()) {
			if (optOnboardingSettings.get().getOnboardingTokenExpirationTime() != null) {
				return calculateExpirationDate(optOnboardingSettings.get().getOnboardingTokenExpirationTime());
			} else {
				return optOnboardingSettings.get().getOnboardingTokenExpirationDate();
			}
		} else {
			return LocalDate.now().plusDays(14);
		}
	}

	private LocalDate calculateExpirationDate(ExpirationTime expirationTime) {
		return LocalDate.now().plusDays(expirationTime.getDayUntilExpiration());
	}
}
