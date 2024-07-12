package com.css.one.views.arbeitsplanung;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.orm.ObjectOptimisticLockingFailureException;

import com.css.one.data.Person;
import com.css.one.data.WorkingUnit;
import com.css.one.data.WorkingUnitCategory;
import com.css.one.services.PersonService;
import com.css.one.services.WorkingUnitCategoryService;
import com.css.one.services.WorkingUnitService;
import com.css.one.views.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.PermitAll;

@PageTitle("Arbeitsplanung")
@Route(value = "planing/:workingUnitID?/:action?(edit)", layout = MainLayout.class)
@PermitAll
public class ArbeitsplanungView extends Div implements BeforeEnterObserver {
	
    private static final long serialVersionUID = 6706685729965294297L;
    
    private final String WORKINGUNIT_ID = "workingUnitID";
	private final String WORKINGUNIT_EDIT_ROUTE_TEMPLATE = "planing/%s/edit";

    private final Grid<WorkingUnit> grid = new Grid<>(WorkingUnit.class, false);
    
    private final WorkingUnitService workingUnitService;
    private final PersonService samplePersonService;
    private final WorkingUnitCategoryService workingUnitCategoryService;
    
    private TextField note;
    private DateTimePicker startWork;
    private DateTimePicker stopWork;
    
    private ComboBox<WorkingUnitCategory> category;
    private ComboBox<Person> worker;
    private Checkbox optionalEndBox;
    
    private WorkingUnit workingUnit;

    private final Button cancel = new Button("Abbrechen");
    private final Button save = new Button("Erfassen");
    
    List<Button> categoriesButtonList = new ArrayList<>();
    
    private int associationId;

    public ArbeitsplanungView(WorkingUnitService workingUnitService, PersonService samplePersonService, WorkingUnitCategoryService workingUnitCategoryService) {
    	this.workingUnitService = workingUnitService;
    	this.samplePersonService = samplePersonService;
    	this.workingUnitCategoryService = workingUnitCategoryService;
    	
        addClassNames("arbeitsplanung-view");
        
     // Create UI
        SplitLayout splitLayout = new SplitLayout();
        
        associationId = MainLayout.getAssociationId();
        createGridLayout(splitLayout); 
        createEditorLayout(splitLayout);
        
        add(splitLayout);  
        
        grid.addColumn(w -> w.getPersonName()).setAutoWidth(true).setHeader("Name");
        grid.addColumn(w -> w.getCategory().getName()).setAutoWidth(true).setHeader("Arbeitsbereich");
        grid.addColumn(w -> resolveWorkingHours(w.getWorkingHours())).setAutoWidth(true).setHeader("Arbeitszeit");
        grid.addColumn(w -> w.getNote()).setAutoWidth(true).setHeader("Notiz");
        grid.addColumn(w -> renderDate(w.getBegin())).setAutoWidth(true).setHeader("Datum");
        grid.addColumn(w -> w.getHourBegin() + ":" + w.getMinuteBegin()).setAutoWidth(true).setHeader("Start");
        grid.addColumn(w -> resolveEndTime(w)).setAutoWidth(true).setHeader("Ende");

        grid.addComponentColumn(item -> new Button("Löschen", click -> {
        	workingUnitService.delete(item.getId());
            refreshGrid();
        }));
        

        grid.setItems(workingUnitService.findAllByAssociation(associationId));
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
                        
        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
            	this.workingUnit = event.getValue();
				UI.getCurrent().navigate(String.format(WORKINGUNIT_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
				this.save.setText("Update");
            } else {
            	this.save.setText("Erfassen");
                clearForm();
                UI.getCurrent().navigate(ArbeitsplanungView.class);
            }
        });

        cancel.addClickListener(e -> {
        	this.save.setText("Erfassen");
            clearForm();
            refreshGrid();
        });
        
        save.addClickListener(e -> {
            try {
				if (this.workingUnit == null) {
					this.workingUnit = new WorkingUnit();
				}
				if (worker.getValue() == null) {
					Notification.show("Es muss ein Mitglied ausgewählt werden.");
				} else {
					workingUnit.setPersonId(worker.getValue().getId());
					workingUnit.setPersonName(worker.getValue().getFirstName() + " " + worker.getValue().getLastName());
					workingUnit.setBegin(startWork.getValue().toLocalDate());
					workingUnit.setMinuteBegin(startWork.getValue().getMinute());
					workingUnit.setHourBegin(startWork.getValue().getHour());
					workingUnit.setAssociationId(associationId);
					if (optionalEndBox.getValue()) {
						workingUnit.setWorkingHours(
								(int) ChronoUnit.MINUTES.between(startWork.getValue(), stopWork.getValue()));
						workingUnit.setEnd(stopWork.getValue().toLocalDate());
						workingUnit.setHourEnd(stopWork.getValue().getHour());
						workingUnit.setMinuteEnd(stopWork.getValue().getMinute());
					} else {
						workingUnit.setWorkingHours(0);
					}

					workingUnit.setCategory(category.getValue());

					workingUnitService.update(this.workingUnit);
					clearForm();
					refreshGrid();
					Notification.show("Data updated");
					UI.getCurrent().navigate(ArbeitsplanungView.class);
					this.save.setText("Erfassen");
					this.workingUnit = null;
				}

			} catch (ObjectOptimisticLockingFailureException exception) {
				Notification n = Notification.show(
						"Error updating the data. Somebody else has updated the record while you were making changes.");
				n.setPosition(Position.MIDDLE);
				n.addThemeVariants(NotificationVariant.LUMO_ERROR);
			}
        });
        
        refreshGridWithCategory(null);
    }
    
	private String resolveEndTime(WorkingUnit w) {		
		if(w.getEnd() != null) {
			return w.getHourEnd() + ":" + w.getMinuteEnd();
		} else {
			return "-";
		}
	}

	private String renderDate(LocalDate date) {
		String day = "";
		String month = "";

		if (date != null) {

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
		} else {
			return "-";
		}
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
		
		splitLayout.setSplitterPosition(75);
		splitLayout.addToPrimary(wrapper);
		
		wrapper.add(horizontalLayout);
		wrapper.add(new Hr());
		wrapper.add(grid);
	}

	private void addComponentsForWorkingCategories(HorizontalLayout horizontalLayout) {
		
		List<WorkingUnitCategory> allByAssociation = workingUnitCategoryService.findAllByAssociation(associationId);
		VerticalLayout layout = new VerticalLayout();
		
		for(WorkingUnitCategory category : allByAssociation) {
			
			layout = new VerticalLayout();
			layout.add(new Text(category.getName()));
			layout.setAlignItems(Alignment.CENTER);
			Button buttonCategory = new Button(layout);
			buttonCategory.setHeight(100, Unit.PIXELS);
			buttonCategory.setMinWidth(150, Unit.PIXELS); 
			buttonCategory.addClassNames(LumoUtility.Border.ALL, LumoUtility.BorderColor.PRIMARY_50);
			buttonCategory.addClickListener(e -> {
				refreshGridWithCategory(category);
				refreshButtonBorders();
				buttonCategory.addClassNames(LumoUtility.Border.ALL, LumoUtility.BorderColor.PRIMARY_50);
			});
			
			categoriesButtonList.add(buttonCategory);
			horizontalLayout.add(buttonCategory);
		}
	}
	
	private void refreshButtonBorders() {
		categoriesButtonList.forEach(e -> {
			e.removeClassNames(LumoUtility.Border.ALL, LumoUtility.BorderColor.PRIMARY_50);
		});	
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
        
        category = new ComboBox<WorkingUnitCategory>("Kategorie");
        category.setItems(workingUnitCategoryService.findAllByAssociation(associationId));
        category.setItemLabelGenerator(e -> e.getName());
        category.setValue(category.getListDataView().getItem(0));
        
        startWork = new DateTimePicker();
        startWork.setLabel("Arbeitsbegin");
        startWork.setStep(Duration.ofSeconds(1));
        startWork.setValue(LocalDateTime.now());
        
        optionalEndBox = new Checkbox("Schicht nachträglich beenden");
        optionalEndBox.setValue(false);
        optionalEndBox.addValueChangeListener(e -> {
        	stopWork.setEnabled(e.getValue());
        });
        
        stopWork = new DateTimePicker();
        stopWork.setLabel("Optional - Arbeitsende");
        stopWork.setStep(Duration.ofSeconds(1));
        stopWork.setValue(LocalDateTime.now());
        stopWork.setEnabled(false);
        
        note = new TextField("Notiz");
        formLayout.add(worker, category, startWork, optionalEndBox, stopWork, note);
        
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
	
	private void refreshGridWithCategory(WorkingUnitCategory category) {
		if(category == null) {
			grid.setItems(workingUnitService.findAllByAssociation(associationId));
		} else {		
			grid.setItems(workingUnitService.findByCategory(category, associationId));
		}			
	}
	
	private void populateForm(WorkingUnit value) {
		this.workingUnit = value;
		
		if(workingUnit.getNote() != null) {
			
			this.note.setValue(workingUnit.getNote());
		}
		
		this.startWork.setValue(LocalDateTime.of(workingUnit.getBegin(), LocalTime.of(workingUnit.getHourBegin(), workingUnit.getMinuteBegin())));
		if(workingUnit.getEnd() != null) {			
			this.stopWork.setValue(LocalDateTime.of(workingUnit.getEnd(), LocalTime.of(workingUnit.getHourEnd(), workingUnit.getMinuteEnd())));
		}
		this.optionalEndBox.setValue(workingUnit.getEnd() != null);
		this.category.setValue(workingUnit.getCategory());
		
		Optional<Person> optional = samplePersonService.get(workingUnit.getPersonId());
		optional.ifPresent(e -> this.worker.setValue(e));
		
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		
		 Optional<Long> workingUnitId = event.getRouteParameters().get(WORKINGUNIT_ID).map(Long::parseLong);
	        if (workingUnitId.isPresent()) {
	        	this.save.setText("Update");
	            Optional<WorkingUnit> samplePersonFromBackend = workingUnitService.get(workingUnitId.get());
	            if (samplePersonFromBackend.isPresent()) {
	                populateForm(samplePersonFromBackend.get());
	            } else {
	                Notification.show(
	                        String.format("The requested workUnit was not found, ID = %s", workingUnitId.get()), 3000,
	                        Notification.Position.BOTTOM_START);
	                // when a row is selected but the data is no longer available,
	                // refresh grid
	                refreshGrid();
	                event.forwardTo(ArbeitsplanungView.class);
	            }
	        }
	}

}
