package com.css.one.views.waitinglist;

import java.time.LocalDate;
import java.util.Optional;

import com.css.one.data.AssociationRole;
import com.css.one.data.Person;
import com.css.one.data.WaitingPerson;
import com.css.one.services.PersonService;
import com.css.one.services.WaitingPersonService;
import com.css.one.views.MainLayout;
import com.css.one.views.mitglieder.MitgliederView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Warteliste")
@Route(value = "waitinglist/:waitingPersonID?/:action?(edit)", layout = MainLayout.class)
@AnonymousAllowed
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

	private final Button cancel = new Button("Abbrechen");
	private final Button save = new Button("Speichern");
	private final Button levelup = new Button("Zu Mitglied machen");
	
	private Dialog newMemberDialog = new Dialog();

	private final BeanValidationBinder<WaitingPerson> binder;

	private WaitingPerson waitingPerson;
	private PersonService personService;
	
	private int associationId;

	public WaitingListView(WaitingPersonService waitingPersonService, PersonService personService) {
		this.waitingPersonService = waitingPersonService;
		this.personService = personService;
		
		addClassNames("waitinglist-view");

		// Create UI
		SplitLayout splitLayout = new SplitLayout();

		associationId = MainLayout.getAssociationId();

		createGridLayout(splitLayout);
		createEditorLayout(splitLayout);

		splitLayout.setSplitterPosition(70);
		add(splitLayout);

		// Configure Grid

		grid.addColumn(p -> p.getFirstName() + " " + p.getLastName()).setAutoWidth(true).setHeader("Name");
		grid.addColumn(p -> p.getEmail()).setAutoWidth(true).setHeader("Email");
		grid.addColumn(p -> p.getPhone()).setAutoWidth(true).setHeader("Telefonnummer");
		grid.addColumn(p -> renderDate(p.getDateOfRegistration())).setAutoWidth(true).setHeader("Auf Warteliste seit").setSortable(true);

		grid.addComponentColumn(item -> new Button("Löschen", click -> {
			waitingPersonService.delete(item.getId());
			refreshGrid();
		}));

		grid.setItems(waitingPersonService.findAllByAssociation(associationId));
		grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

		// when a row is selected or deselected, populate form
		grid.asSingleSelect().addValueChangeListener(event -> {
			if (event.getValue() != null) {
				UI.getCurrent().navigate(String.format(WAITINGPERSON_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
			} else {
				clearForm();
				UI.getCurrent().navigate(WaitingListView.class);
			}
		});

		// Configure Form
		binder = new BeanValidationBinder<>(WaitingPerson.class);

		// Bind fields. This is where you'd define e.g. validation rules

		binder.bindInstanceFields(this);

		cancel.addClickListener(e -> {
			clearForm();
			refreshGrid();
		});

		save.addClickListener(e -> {
			try {

				if (checkPersonDataForWaitingList()) {

					if (this.waitingPerson == null) {
						this.waitingPerson = new WaitingPerson();
					}

					this.waitingPerson.setAssociationId(associationId);
					this.waitingPerson.setDateOfBirth(dateOfBirth.getValue());
					this.waitingPerson.setDateOfRegistration(LocalDate.now());

					binder.writeBean(this.waitingPerson);
					waitingPersonService.update(this.waitingPerson);
					clearForm();
					refreshGrid();
					Notification.show("Person wurd auf die Warteliste gesetzt.");
					UI.getCurrent().navigate(WaitingListView.class);
				} else {

				}
			} catch (ValidationException exception) {
				Notification.show("Failed to update the data. Check again that all values are valid");
			}
		});
		
		levelup.addClickListener(e -> {
			newMemberDialog = new Dialog();
			if(this.waitingPerson != null) {				
				createNewMemberDialog();
				newMemberDialog.open();
			}
		});
	}
	
	private boolean checkPersonDataForWaitingList() {
		boolean isDataOk = true;
		
		
		if(firstName.getValue().equals("")) {
			isDataOk = false;
			Notification.show("Die Person braucht einen Vornamen");
			return isDataOk;
		}
		
		if(lastName.getValue().equals("")) {
			isDataOk = false;
			Notification.show("Die Person braucht einen Nachnamen");
			return isDataOk;
		}
		
		if(email.getValue().equals("")) {
			isDataOk = false;
			Notification.show("Die Person braucht eine Email");
			return isDataOk;
		}
		
		if(phone.getValue().equals("")) {
			isDataOk = false;
			Notification.show("Die Person braucht eine Telefonnummer");
			return isDataOk;
		}
		
		if(dateOfBirth.getValue() == null) {
			isDataOk = false;
			Notification.show("Die Person braucht ein Geburtsdatum");
			return isDataOk;
		}
		
		LocalDate date = dateOfBirth.getValue();
		LocalDate now = LocalDate.now();
		
		if (!(now.getYear() - date.getYear() > 17)) {
			isDataOk = false;
			Notification.show("Die Person ist noch nicht volljährig!");
			return isDataOk;
		} else {
			if (now.getYear() - date.getYear() == 18) {
				if (!(now.getMonth().getValue() >= date.getMonth().getValue())) {
					isDataOk = false;
					Notification.show("Die Person ist noch nicht volljährig!");
					return isDataOk;
				} else {
					if (now.getMonth().getValue() == date.getMonth().getValue()) {
						if (!(now.getDayOfMonth() >= date.getDayOfMonth())) {
							isDataOk = false;
							Notification.show("Es fehlen nur noch ein paar Tage !");
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

		dialogLayout.add(new H2("Zu Mitgliedern hinzufügen"));
		dialogLayout.add(new Hr());

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
				person.setDateOfRegistration(LocalDate.now());
				person.setEmail(waitingPerson.getEmail());
				person.setPhone(waitingPerson.getPhone());
				person.setFirstName(waitingPerson.getFirstName());
				person.setLastName(waitingPerson.getLastName());
				person.setImportant(false);

				person = personService.update(person);

				waitingPersonService.delete(waitingPerson.getId());
				this.waitingPerson = null;

				newMemberDialog.close();
				clearForm();
				refreshGrid();

				Notification.show("Neues Mitglied hinzugefügt.");
			} else {
				Notification.show("Es muss feststehen, welche Rolle die Person haben wird.");
			}
		});

		Button cancelButton = new Button("Zurück", e -> newMemberDialog.close());

		newMemberDialog.getFooter().add(cancelButton);
		newMemberDialog.getFooter().add(saveButton);
	}

	private boolean checkBeforeSave(ComboBox<AssociationRole> comboBox, TextField nameOfPerson) {
		boolean returnValue = true;
		
		if(comboBox.getValue() != null) {
			returnValue = false;
			Notification.show("Einer Person muss eine Rolle zugewiesen werden");
			return returnValue;
		}
		if(personService.count() <= 500) {
			returnValue = false;
			Notification.show("Die maximale Anzahl an Mitgliedern ist bereits erreicht !");
			return returnValue;
		}
		if(nameOfPerson.getValue().equals("")) {
			returnValue = false;
			Notification.show("Die Person braucht noch einen Namen !");
			return returnValue;
		}
		
		return returnValue;
	}

	private void refreshGrid() {
		grid.select(null);
		grid.setItems(waitingPersonService.findAllByAssociation(associationId));
	}

	private void clearForm() {
		populateForm(null);
	}

	private void populateForm(WaitingPerson value) {
		this.waitingPerson = value;
		binder.readBean(this.waitingPerson);
	}

	private void createEditorLayout(SplitLayout splitLayout) {
		Div editorLayoutDiv = new Div();
		editorLayoutDiv.setClassName("editor-layout");

		Div editorDiv = new Div();
		editorDiv.setClassName("editor");
		editorLayoutDiv.add(editorDiv);

		FormLayout formLayout = new FormLayout();
		firstName = new TextField("Vorname");
		lastName = new TextField("Nachname");
		email = new TextField("Email");
		phone = new TextField("Telefonnummer");
		dateOfBirth = new DatePicker("Geburtstag");

		formLayout.add(firstName, lastName, email, phone, dateOfBirth);

		editorDiv.add(formLayout);
		createButtonLayout(editorLayoutDiv);

		splitLayout.addToSecondary(editorLayoutDiv);
	}

	private void createButtonLayout(Div editorLayoutDiv) {
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setClassName("button-layout");
		cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		levelup.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		buttonLayout.add(save, levelup, cancel);
		editorLayoutDiv.add(buttonLayout);
	}

	private void createGridLayout(SplitLayout splitLayout) {
		Div wrapper = new Div();
		wrapper.setClassName("grid-wrapper");
		splitLayout.addToPrimary(wrapper);
		wrapper.add(grid);
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		  Optional<Long> waitingPersonId = event.getRouteParameters().get(WAITINGPERSON_ID).map(Long::parseLong);
	        if (waitingPersonId.isPresent()) {
	            Optional<WaitingPerson> waitingPersonFromBackend = waitingPersonService.get(waitingPersonId.get());
	            if (waitingPersonFromBackend.isPresent()) {
	            	populateForm(waitingPersonFromBackend.get());
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
