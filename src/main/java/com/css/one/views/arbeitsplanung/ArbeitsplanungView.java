package com.css.one.views.arbeitsplanung;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.vaadin.lineawesome.LineAwesomeIcon;

import com.css.one.data.Person;
import com.css.one.data.WorkingUnit;
import com.css.one.services.PersonService;
import com.css.one.services.WorkingUnitService;
import com.css.one.views.MainLayout;
import com.css.one.views.mitglieder.MitgliederView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
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
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;

import jakarta.annotation.security.PermitAll;

@PageTitle("Arbeitsplanung")
@Route(value = "planing", layout = MainLayout.class)
@PermitAll
public class ArbeitsplanungView extends Div implements BeforeEnterObserver {
	
    private static final long serialVersionUID = 6706685729965294297L;

	private final BeanValidationBinder<WorkingUnit> binder;
    
    private final String WORKINGUNIT_ID = "workingUnitID";
//    private final String WORKINGUNIT_EDIT_ROUTE_TEMPLATE = "planing/%s/edit";

    private final Grid<WorkingUnit> grid = new Grid<>(WorkingUnit.class, false);
    
    private final WorkingUnitService workingUnitService;
    private final PersonService samplePersonService;

    private TextField note;
    private DateTimePicker startWork;
    private DateTimePicker stopWork;
    
    private ComboBox<String> category;
    private ComboBox<Person> worker;
    
    private WorkingUnit workingUnit;

    private final Button cancel = new Button("Abbrechen");
    private final Button save = new Button("Erfassen");
    
    private Button gardeningButton;
    private Button operatingServiceButton;
    private Button salesServiceButton;
    private Button allServices;
    
    String categoryOne = "Gärtnereiarbeiten";
    String categoryTwo = "Verwaltungsdienst";
    String categoryThree = "Einkaufsaufwände";
    
    private int associationId;

    public ArbeitsplanungView(WorkingUnitService workingUnitService, PersonService samplePersonService) {
    	this.workingUnitService = workingUnitService;
    	this.samplePersonService = samplePersonService;
    	
        addClassNames("arbeitsplanung-view");
        
     // Create UI
        SplitLayout splitLayout = new SplitLayout();
        
        associationId = MainLayout.getAssociationId();
        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);
        
        add(splitLayout);  
        
        grid.addColumn(w -> w.getPersonName()).setAutoWidth(true).setHeader("Name");
        grid.addColumn(w -> w.getCategory()).setAutoWidth(true).setHeader("Arbeitsbereich");
        grid.addColumn(w -> resolveWorkingHours(w.getWorkingHours())).setAutoWidth(true).setHeader("Arbeitszeit");
        grid.addColumn(w -> w.getNote()).setAutoWidth(true).setHeader("Notiz");
        grid.addColumn(w -> renderDate(w.getBegin())).setAutoWidth(true).setHeader("Begin der Arbeitszeit");
        grid.addColumn(w -> renderDate(w.getEnd())).setAutoWidth(true).setHeader("Ende der Arbeitszeit");

        grid.addComponentColumn(item -> new Button("Löschen", click -> {
        	workingUnitService.delete(item.getId());
            refreshGrid();
        }));
        

        grid.setItems(workingUnitService.findAllByAssociation(associationId));
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
                
        binder = new BeanValidationBinder<>(WorkingUnit.class);
        binder.bindInstanceFields(this);
        
        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
            	
            } else {
                clearForm();
                UI.getCurrent().navigate(ArbeitsplanungView.class);
            }
        });

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });
        
        save.addClickListener(e -> {
            try {
                this.workingUnit = new WorkingUnit();
                
                if(worker.getValue() == null) {
                	Notification.show("Es muss ein Mitglied ausgewählt werden.");
                } else {
                	workingUnit.setPersonId(worker.getValue().getId());
                	workingUnit.setPersonName(worker.getValue().getFirstName() + " " + worker.getValue().getLastName());
                	workingUnit.setBegin(startWork.getValue().toLocalDate());
                	workingUnit.setEnd(stopWork.getValue().toLocalDate());
                	workingUnit.setWorkingHours((int)ChronoUnit.MINUTES.between(startWork.getValue(), stopWork.getValue()));
                	
                	binder.writeBean(this.workingUnit);
                	workingUnitService.update(this.workingUnit);
                	clearForm();
                	refreshGrid();
                	Notification.show("Data updated");
                	UI.getCurrent().navigate(ArbeitsplanungView.class);
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

	private String resolveWorkingHours(int workingHours) {
		
		int hours = workingHours/60;
		int minutes = workingHours%60;
		
		return hours + " h " + minutes + " min";
	}

	private void createGridLayout(SplitLayout splitLayout) {
		
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setMargin(true);
		horizontalLayout.setAlignItems(Alignment.CENTER);
		addComponentsForWorkingCategories(horizontalLayout);
		horizontalLayout.setWidth(1000, Unit.PIXELS);
		
		Div wrapper = new Div();
		wrapper.setClassName("grid-wrapper");
		
		splitLayout.setSplitterPosition(70);
		splitLayout.addToPrimary(wrapper);
		
		wrapper.add(horizontalLayout);
		wrapper.add(new Hr());
		wrapper.add(grid);
	}

	private void addComponentsForWorkingCategories(HorizontalLayout horizontalLayout) {
		
		VerticalLayout layout = new VerticalLayout();
		layout.add(LineAwesomeIcon.OBJECT_GROUP.create());
		layout.add(new H2("Alle"));
		layout.setAlignItems(Alignment.CENTER);
		allServices = new Button(layout);
		allServices.setHeight(100, Unit.PIXELS);
		allServices.setWidth(250, Unit.PIXELS);
		allServices.addClickListener(e -> {
			refreshGridWithCategory(null);
		});
					
		horizontalLayout.add(allServices);
		
		layout = new VerticalLayout();
		layout.add(LineAwesomeIcon.CANNABIS_SOLID.create());
		layout.add(new H2(categoryOne));
		layout.setAlignItems(Alignment.CENTER);
		gardeningButton = new Button(layout);
		gardeningButton.setHeight(100, Unit.PIXELS);
		gardeningButton.setWidth(250, Unit.PIXELS);
		gardeningButton.addClickListener(e -> {
			refreshGridWithCategory(categoryOne);
		});
					
		horizontalLayout.add(gardeningButton);
		
		layout = new VerticalLayout();
		layout.add(LineAwesomeIcon.ADDRESS_BOOK.create());
		layout.add(new H2(categoryTwo));
		layout.setAlignItems(Alignment.CENTER);
		operatingServiceButton = new Button(layout);
		operatingServiceButton.setHeight(100, Unit.PIXELS);
		operatingServiceButton.setWidth(250, Unit.PIXELS);
		
		operatingServiceButton.addClickListener(e -> {
			refreshGridWithCategory(categoryTwo);
		});
					
		horizontalLayout.add(operatingServiceButton);
		
		layout = new VerticalLayout();
		layout.add(LineAwesomeIcon.MONEY_BILL_ALT_SOLID.create());
		layout.add(new H2(categoryThree));
		layout.setAlignItems(Alignment.CENTER);
		salesServiceButton = new Button(layout);
		salesServiceButton.setHeight(100, Unit.PIXELS);
		salesServiceButton.setWidth(250, Unit.PIXELS);
		salesServiceButton.addClickListener(e -> {
			refreshGridWithCategory(categoryThree);
		});
					
		horizontalLayout.add(salesServiceButton);
	}
	
	private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        
        worker = new ComboBox<Person>("Mitglied");
        worker.setItems(samplePersonService.findAllByAssociation(associationId));
        worker.setItemLabelGenerator(e -> e.getFirstName() + " " + e.getLastName());
        
        category = new ComboBox<String>("Kategorie");
        category.setItems(Arrays.asList("Gärtnereiarbeiten", "Verwaltungsdienst", "Einkaufsaufwände"));
        category.setValue(category.getListDataView().getItem(0));
        
        startWork = new DateTimePicker();
        startWork.setLabel("Arbeitsbegin");
        startWork.setStep(Duration.ofSeconds(1));
        startWork.setValue(LocalDateTime.now());
        
        stopWork = new DateTimePicker();
        stopWork.setLabel("Arbeitsende");
        stopWork.setStep(Duration.ofSeconds(1));
        stopWork.setValue(LocalDateTime.now());
        
        note = new TextField("Notiz");
        formLayout.add(worker, category, startWork, stopWork, note);
        
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

	private void clearForm() {
		worker.setValue(null);
		category.setValue(category.getListDataView().getItem(0));
		startWork.setValue(LocalDateTime.now());
        stopWork.setValue(LocalDateTime.now());
        note.setValue("");
	}

	private void refreshGrid() {
		grid.select(null);
		grid.setItems(workingUnitService.findAllByAssociation(associationId));
	}
	
	private void refreshGridWithCategory(String category) {
		if(category == null) {
			grid.setItems(query -> workingUnitService.list(
	                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
	                .stream());
		} else {			
			grid.setItems(workingUnitService.findByCategory(category, associationId));
		}			
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		
		 Optional<Long> workingUnitId = event.getRouteParameters().get(WORKINGUNIT_ID).map(Long::parseLong);
	        if (workingUnitId.isPresent()) {
	            Optional<Person> samplePersonFromBackend = samplePersonService.get(workingUnitId.get());
	            if (samplePersonFromBackend.isPresent()) {
//	                populateForm(samplePersonFromBackend.get());
	            } else {
	                Notification.show(
	                        String.format("The requested workUnit was not found, ID = %s", workingUnitId.get()), 3000,
	                        Notification.Position.BOTTOM_START);
	                // when a row is selected but the data is no longer available,
	                // refresh grid
	                refreshGrid();
	                event.forwardTo(MitgliederView.class);
	            }
	        }
	}

}
