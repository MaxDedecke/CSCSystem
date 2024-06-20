package com.css.one.views.mitglieder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import org.springframework.orm.ObjectOptimisticLockingFailureException;

import com.css.one.data.AssociationRole;
import com.css.one.data.Person;
import com.css.one.services.PersonService;
import com.css.one.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

@PageTitle("Mitglieder")
@Route(value = "mitglieder/:samplePersonID?/:action?(edit)", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class MitgliederView extends Div implements BeforeEnterObserver {

    private static final long serialVersionUID = -4968177781532500867L;
	private final String SAMPLEPERSON_ID = "samplePersonID";
    private final String SAMPLEPERSON_EDIT_ROUTE_TEMPLATE = "mitglieder/%s/edit";

    private final Grid<Person> grid = new Grid<>(Person.class, false);

    private TextField firstName;
    private TextField lastName;
    private TextField email;
    private TextField phone;
    private DatePicker dateOfBirth;
    private ComboBox<AssociationRole> role;
    private Checkbox important;

    private final Button cancel = new Button("Abbrechen");
    private final Button save = new Button("Speichern");

    private final BeanValidationBinder<Person> binder;

    private Person samplePerson;

    private final PersonService samplePersonService;
    
    private int associationId;

    public MitgliederView(PersonService samplePersonService) {
        this.samplePersonService = samplePersonService;
        addClassNames("mitglieder-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        
        associationId = MainLayout.getAssociationId();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid

        grid.addColumn(p -> p.getFirstName() + " " + p.getLastName()).setAutoWidth(true).setHeader("Name");
        grid.addColumn(p -> p.getEmail()).setAutoWidth(true).setHeader("Email");
        grid.addColumn(p -> p.getPhone()).setAutoWidth(true).setHeader("Telefonnummer");
        grid.addColumn(p -> p.getAssociationRole().getLabel()).setAutoWidth(true).setHeader("Rolle").setSortable(true);
        grid.addColumn(p -> renderDate(p.getDateOfRegistration())).setAutoWidth(true).setHeader("Beitrittsdatum").setSortable(true);

        LitRenderer<Person> importantRenderer = LitRenderer.<Person>of(
                "<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>")
                .withProperty("icon", important -> important.isImportant() ? "check" : "minus").withProperty("color",
                        important -> important.isImportant()
                                ? "var(--lumo-primary-text-color)"
                                : "var(--lumo-disabled-text-color)");

        grid.addColumn(importantRenderer).setHeader("Relevant").setAutoWidth(true).setSortable(true);
       
        grid.addComponentColumn(item -> new Button("Löschen", click -> {
        	samplePersonService.delete(item.getId());
            refreshGrid();
        }));
        
        
        grid.setItems(samplePersonService.findAllByAssociation(associationId));
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(SAMPLEPERSON_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(MitgliederView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Person.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

		save.addClickListener(e -> {
			try {

				if (role.getValue() != null) {
					if (this.samplePerson == null) {
						this.samplePerson = new Person();
					}
					samplePerson.setAssociationId(associationId);
					samplePerson.setAssociationRole(role.getValue());

					if (role.getValue() != AssociationRole.MEMBER) {
						samplePerson.setDateOfHigherRole(LocalDate.now());
					}
					binder.writeBean(this.samplePerson);
					samplePersonService.update(this.samplePerson);
					clearForm();
					refreshGrid();
					Notification.show("Mitglied hinzugefügt");
					UI.getCurrent().navigate(MitgliederView.class);
				} else {
					Notification n = Notification.show("Eine neue Person muss eine Rolle haben!");
					n.setPosition(Position.MIDDLE);
					n.addThemeVariants(NotificationVariant.LUMO_ERROR);
				}
			} catch (ObjectOptimisticLockingFailureException exception) {
				Notification n = Notification.show(
						"Error updating the data. Somebody else has updated the record while you were making changes.");
				n.setPosition(Position.MIDDLE);
				n.addThemeVariants(NotificationVariant.LUMO_ERROR);
			} catch (ValidationException validationException) {
				Notification.show("Failed to update the data. Check again that all values are valid");
			}
		});
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> samplePersonId = event.getRouteParameters().get(SAMPLEPERSON_ID).map(Long::parseLong);
        if (samplePersonId.isPresent()) {
            Optional<Person> samplePersonFromBackend = samplePersonService.get(samplePersonId.get());
            if (samplePersonFromBackend.isPresent()) {
                populateForm(samplePersonFromBackend.get());
                role.setValue(samplePersonFromBackend.get().getAssociationRole());
            } else {
                Notification.show(
                        String.format("The requested samplePerson was not found, ID = %s", samplePersonId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(MitgliederView.class);
            }
        }
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
        role = new ComboBox<AssociationRole>("Rolle im Verein");
        role.setItems(Arrays.asList(AssociationRole.values()));
        role.setItemLabelGenerator(e -> e.getLabel());
        
        important = new Checkbox("Relevant");
        formLayout.add(firstName, lastName, email, phone, dateOfBirth, role, important);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.setItems(samplePersonService.findAllByAssociation(associationId));
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Person value) {
        this.samplePerson = value;
        
        if(value == null) {
        	role.setValue(null);
        	binder.readBean(this.samplePerson);

        } else {        	
        	binder.readBean(this.samplePerson);
        }
    }
}
