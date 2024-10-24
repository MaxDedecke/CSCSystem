package com.css.one.views.waitinglist;

import java.time.LocalDate;
import java.util.Optional;

import org.vaadin.lineawesome.LineAwesomeIcon;

import com.css.one.data.AssociationRole;
import com.css.one.data.MemberData;
import com.css.one.data.MemberSubscription;
import com.css.one.data.Person;
import com.css.one.data.WaitingPerson;
import com.css.one.services.EmailService;
import com.css.one.services.MemberSubscriptionService;
import com.css.one.services.PersonService;
import com.css.one.services.WaitingPersonService;
import com.css.one.views.MainLayout;
import com.css.one.views.mitglieder.MitgliederView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.PermitAll;

@PageTitle("Wartebereich")
@PermitAll
public class WaitingListView extends Div implements BeforeEnterObserver {

	/**
	* 
	*/
	private static final long serialVersionUID = -5000521119703456571L;

	private WaitingPersonService waitingPersonService;
	private final String WAITINGPERSON_ID = "waitingPersonID";
	private final String WAITINGPERSON_EDIT_ROUTE_TEMPLATE = "waitinglist/%s/edit";

	private final Grid<WaitingPerson> grid = new Grid<>(WaitingPerson.class, false);

	private TextField firstName;
	private TextField lastName;
	private TextField email;
	private TextField phone;
	private DatePicker dateOfBirth;
	
	private TextField streetName;
	private TextField streetNumber;
	private TextField postalCode;
	private TextField city;

	private Button save = new Button("speichern");	
	private Dialog newMemberDialog = new Dialog();
	private Text memberCount;
	
	private Dialog personInfoDialog = new Dialog();

	private WaitingPerson waitingPerson;
	private PersonService personService;
    private MemberSubscriptionService subscriptionService;

	private int associationId;

	public WaitingListView(WaitingPersonService waitingPersonService, PersonService personService, MemberSubscriptionService subscriptionService) {
		this.waitingPersonService = waitingPersonService;
		this.personService = personService;
		this.subscriptionService = subscriptionService;
		
		addClassNames("waitinglist-view");

		// Create UI
		SplitLayout splitLayout = new SplitLayout();

		associationId = MainLayout.getAssociationId();

		createGridLayout(splitLayout);
		createAddPersonDialog();
		
		splitLayout.setSplitterPosition(70);
		add(splitLayout);
	}
	
private void createAddPersonDialog() {
		VerticalLayout mainWrapper = new VerticalLayout();
		
		H2 header = new H2("Person hinzufügen");
		header.addClassName("customheader");
		
		mainWrapper.add(header, createAddPersonContent(), new Hr(), createAdditionalDataContent());

		save = new Button("hinzufügen");
		save.addClassNames("save-button");

		save.addClickListener(e -> {
			savePersonData();
		});
		
		Button buttonCancel = new Button("abbrechen");
		buttonCancel.addClassNames("cancel-button");
		
		buttonCancel.addClickListener(e -> {
			personInfoDialog.close();
		});
		
		personInfoDialog.getFooter().add(buttonCancel, save);
		personInfoDialog.add(mainWrapper);
	}

private Component createAdditionalDataContent() {
	
	VerticalLayout wrapper = new VerticalLayout();
	H3 h3 = new H3("Addressdaten");
	h3.addClassName("customheader");
	
	FormLayout additionalFormLayout = new FormLayout();
	streetName = new TextField("Straße");	
	streetNumber = new TextField("Hausnummer");
	postalCode = new TextField("PLZ");
	city = new TextField("Ort");
		
	additionalFormLayout.add(streetName, streetNumber, postalCode, city);
	
	wrapper.addClassNames(LumoUtility.Padding.NONE, LumoUtility.Margin.NONE);
	wrapper.add(h3, additionalFormLayout);
	return wrapper;
}

private void savePersonData() {
	
	if (checkPersonDataForWaitingList()) {

		if (this.waitingPerson == null) {
			this.waitingPerson = new WaitingPerson();
		}

		this.waitingPerson.setAssociationId(associationId);
		this.waitingPerson.setDateOfBirth(dateOfBirth.getValue());
		this.waitingPerson.setDateOfRegistration(LocalDate.now());
		this.waitingPerson.setOnboaring(false);
		this.waitingPerson.setEmail(email.getValue());
		this.waitingPerson.setFirstName(firstName.getValue());
		this.waitingPerson.setLastName(lastName.getValue());
		this.waitingPerson.setOnboaring(false);
		this.waitingPerson.setPhone(phone.getValue());
		
		if(this.waitingPerson.getMemberData() == null) {
			MemberData memberData = new MemberData();
			memberData.setCityName("");
			memberData.setPostalCode(1);
			memberData.setStreetName("");
			memberData.setStreetNumber("");
			memberData.setDateOfRegistration(LocalDate.now());
			waitingPerson.setMemberData(memberData);
		} else {
			MemberData memberData = waitingPerson.getMemberData();
			memberData.setStreetName(streetName.getValue());	
			memberData.setCityName(city.getValue());
			memberData.setPostalCode(Integer.parseInt(postalCode.getValue()));
			memberData.setStreetNumber(streetNumber.getValue());
			waitingPerson.setMemberData(memberData);
		}
		
		waitingPersonService.update(this.waitingPerson);
		clearPersonInfoDialog();
		refreshGrid();
		
		Notification notification = Notification.show("Person wurd auf die Warteliste gesetzt.");
		notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
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

		if(firstName.getValue().equals("")) {
			isDataOk = false;
			Notification notification = Notification.show("Die Person braucht einen Vornamen");
			notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
			return isDataOk;
		}
		
		if(lastName.getValue().equals("")) {
			isDataOk = false;
			Notification notification = Notification.show("Die Person braucht einen Nachnamen");
			notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
			return isDataOk;
		}
		
		if(email.getValue().equals("")) {
			isDataOk = false;
			Notification notification = Notification.show("Die Person braucht eine Email");
			notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
			return isDataOk;
		}
		
		if(phone.getValue().equals("")) {
			isDataOk = false;
			Notification notification = Notification.show("Die Person braucht eine Telefonnummer");
			notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
			return isDataOk;
		}
		
		if(dateOfBirth.getValue() == null) {
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
		TextField nameOfPerson = new TextField("Name");
		nameOfPerson.setValue(this.waitingPerson.getFirstName() + " " + this.waitingPerson.getLastName());
		nameOfPerson.setEnabled(false);
		
		ComboBox<AssociationRole> comboBox = new ComboBox<AssociationRole>("Rolle");
		comboBox.setItems(AssociationRole.values());
		comboBox.setItemLabelGenerator(e -> e.getLabel());
		
		formLayout.add(nameOfPerson, comboBox);
		dialogLayout.add(formLayout);
		
		newMemberDialog.add(dialogLayout);
		Button saveButton = new Button("Hinzufügen", e -> {

			if (checkBeforeSave(comboBox, nameOfPerson)) {

				Person person = new Person();
				person.setAssociationId(associationId);
				person.setAssociationRole(comboBox.getValue());
				person.setDateOfBirth(waitingPerson.getDateOfBirth());
//TODO				person.setDateOfRegistration(LocalDate.now());
				person.setEmail(waitingPerson.getEmail());
				person.setPhone(waitingPerson.getPhone());
				person.setFirstName(waitingPerson.getFirstName());
				person.setLastName(waitingPerson.getLastName());
				person.setMemberNumber(personService.getFreeMemberNumber(associationId));
				person = personService.update(person);

				if(waitingPerson.getId() != null) {				
					waitingPersonService.delete(waitingPerson.getId());
				}
				this.waitingPerson = null;
				
				createSingleSubscriptionForNewMember(person);
				
				newMemberDialog.close();
				refreshGrid();

				Notification notification = Notification.show("Neues Mitglied hinzugefügt.");
				notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
			}
		});
		saveButton.addClassNames("save-button");
		
		Button cancelButton = new Button("Zurück", e -> newMemberDialog.close());
		cancelButton.addClassNames("cancel-button");

		newMemberDialog.getFooter().add(cancelButton);
		newMemberDialog.getFooter().add(saveButton);
	}

	private boolean checkBeforeSave(ComboBox<AssociationRole> comboBox, TextField nameOfPerson) {
		boolean returnValue = true;
		
		if(comboBox.getValue() == null) {
			returnValue = false;
			Notification notification = Notification.show("Einer Person muss eine Rolle zugewiesen werden");
			notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
			return returnValue;
		}
		if(personService.count() >= 500) {
			returnValue = false;
			Notification notification = Notification.show("Die maximale Anzahl an Mitgliedern ist bereits erreicht !");
			notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
			return returnValue;
		}
		if(nameOfPerson.getValue().equals("")) {
			returnValue = false;
			Notification notification = Notification.show("Die Person braucht noch einen Namen !");
			notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
			return returnValue;
		}
		
		return returnValue;
	}

	private void refreshGrid() {
		grid.select(null);
		grid.setItems(waitingPersonService.findAllByAssociation(associationId));
	}
	
	private Component createAddPersonContent() {
		
		VerticalLayout wrapper = new VerticalLayout();
		H3 h3 = new H3("Allgemeine Angaben");
		h3.addClassName("customheader");

		FormLayout formLayout = new FormLayout();
		firstName = new TextField("Vorname");
		lastName = new TextField("Nachname");
		email = new TextField("Email");
		phone = new TextField("Telefonnummer");
        phone.setAllowedCharPattern("[0-9/]");
		dateOfBirth = new DatePicker("Geburtstag");
		dateOfBirth.setOverlayClassName("waiting-list-view-date-picker-1");
		dateOfBirth.addClassName("waiting-list-view-date-picker-1");

		formLayout.add(firstName, lastName, phone, dateOfBirth, email);
		formLayout.setColspan(email, 2);
		
		wrapper.addClassNames(LumoUtility.Padding.NONE, LumoUtility.Margin.NONE);
		wrapper.add(h3, formLayout);
		return wrapper;
	}

	private void createGridLayout(SplitLayout splitLayout) {
		
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.addClassNames(LumoUtility.Padding.NONE);
		
		HorizontalLayout bottomLayout = new HorizontalLayout();
		bottomLayout.setWidth("100%");
		bottomLayout.addClassNames(LumoUtility.Padding.NONE, LumoUtility.Padding.Bottom.SMALL,
				LumoUtility.Padding.Left.MEDIUM, LumoUtility.JustifyContent.CENTER);
		memberCount = new Text("Wartendene Personen: " + waitingPersonService.count());

		bottomLayout.add(memberCount);
		
		createGrid();

		mainLayout.add(createFirstComponent(), grid, bottomLayout);
		splitLayout.addToPrimary(mainLayout);
	}

	private Component createFirstComponent() {
		HorizontalLayout wrapper = new HorizontalLayout();
		wrapper.addClassNames("header-bar-custom");
		wrapper.setWidthFull();
		
		
		Button buttonAddPerson = new Button("Person hinzufügen");
		buttonAddPerson.setIcon(LineAwesomeIcon.USER_PLUS_SOLID.create());
		buttonAddPerson.addClassName("button-neutral");
		
		buttonAddPerson.addClickListener(e -> {
			personInfoDialog.open();
		});
		
		wrapper.add(buttonAddPerson);
		return wrapper;
	}

	private void createGrid() {
		// Configure Grid
		grid.addColumn(p -> p.getFirstName() + " " + p.getLastName()).setAutoWidth(true).setHeader("Name");
		grid.addColumn(p -> p.getEmail()).setAutoWidth(true).setHeader("Email");
		grid.addColumn(p -> p.getPhone()).setAutoWidth(true).setHeader("Telefonnummer");
		grid.addColumn(p -> renderDate(p.getDateOfRegistration())).setAutoWidth(true).setHeader("Auf Warteliste seit")
				.setSortable(true);

		grid.addComponentColumn(person -> {

			MenuBar menuBar = new MenuBar();
			menuBar.setOverlayClassName("warenlager-view-menu-bar-1");
			menuBar.addClassNames("warenlager-view-menu-bar-1", "customheader");

			menuBar.addItem("bearbeiten", event -> {
				//TODO
				this.waitingPerson = person;
				initPersonInfoDialog();
			});

			menuBar.addItem("Person löschen", event -> {
				waitingPersonService.delete(person.getId());
				refreshGrid();
			});

			menuBar.addItem("Zum Mitglied machen", event -> {
				this.waitingPerson = person;
				newMemberDialog = new Dialog();
				createNewMemberDialog();
				newMemberDialog.open();
			});

			if (!person.isOnboaring()) {
				menuBar.addItem("Onboarding starten", event -> {
					sendOnboardingEmail();
				});
			}

			return menuBar;
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

	private void initPersonInfoDialog() {

		this.firstName.setValue(waitingPerson.getFirstName());
		this.lastName.setValue(waitingPerson.getLastName());
		this.email.setValue(waitingPerson.getEmail());
		this.phone.setValue(waitingPerson.getPhone());
		
		if(waitingPerson.getMemberData() != null) {
			MemberData memberData = waitingPerson.getMemberData();
			this.city.setValue(memberData.getCityName());
			this.streetName.setValue(memberData.getStreetName());
			this.streetNumber.setValue(memberData.getStreetNumber());
			this.postalCode.setValue(String.valueOf(memberData.getPostalCode()));
		}
		
		this.save.setAriaLabel("aktualisieren");
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
		this.save.setAriaLabel("speichern");
	}

	private void sendOnboardingEmail() {
		EmailService emailService = new EmailService();
		
		String to = "jm.dedecke@gmail.com";
		String subject = "First email of the system";
		String body = "Don't worry, be happy !";
		
		try {
			emailService.sendSimpleMessage(to, subject, body);
            Notification notification = Notification.show("E-Mail erfolgreich gesendet");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
        	Notification notification = Notification.show("Fehler beim Senden der E-Mail: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

        }
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		  Optional<Long> waitingPersonId = event.getRouteParameters().get(WAITINGPERSON_ID).map(Long::parseLong);
	        if (waitingPersonId.isPresent()) {
	            Optional<WaitingPerson> waitingPersonFromBackend = waitingPersonService.get(waitingPersonId.get());
	            if (waitingPersonFromBackend.isPresent()) {
	            } else {
	                Notification.show(
	                        String.format("The requested samplePerson was not found, ID = %s", waitingPersonId.get()), 3000,
	                        Notification.Position.BOTTOM_START);
	                // when a row is selected but the data is no longer available,
	                // refresh grid
	                refreshGrid();
	                event.forwardTo(MitgliederView.class);
	            }
	        }
	}
}
