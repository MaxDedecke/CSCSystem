package com.css.one.views.warenlager;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.css.one.data.GrowStatus;
import com.css.one.data.Output;
import com.css.one.data.Strain;
import com.css.one.services.OutputService;
import com.css.one.services.PersonService;
import com.css.one.services.StrainService;
import com.css.one.views.MainLayout;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

@PageTitle("Warenlager")
@Route(value = "waren", layout = MainLayout.class)
@AnonymousAllowed
public class WarenlagerView extends Div {

    private static final long serialVersionUID = 5652277988730640569L;
    private OutputService outputService;
    private PersonService personService;
    private StrainService strainService;
    private int associationId;
    
    Dialog addStrainDialog;
    Dialog addOutputDialog;
    
	Grid<Strain> strainGrid = new Grid<Strain>();
	Grid<Output> outputGrid = new Grid<Output>();
	
	ComboBox<GrowStatus> statusBox = new ComboBox<GrowStatus>("Status");
	Checkbox box;
	DateTimePicker dateHarvested;
	
	H2 amount = new H2("0 Gramm");
	H2 amount2 = new H2("0 Gramm");

	List<Strain> allByAssociation = new ArrayList<>();
    List<Output> outputAssociation = new ArrayList<>();
    
    Dialog changeStrainStatusDialog = new Dialog();
    
    Strain changeStrain;
    
	public WarenlagerView(StrainService strainService, OutputService outputService, PersonService personService) {
		this.strainService = strainService;
		this.outputService = outputService;
		this.personService = personService;
		
        addClassNames("warenlager-view");        
        associationId = MainLayout.getAssociationId();
        
        createChangeStatusDialog();
        
        TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeFull();
        
        createStrainsLayout(tabSheet);
        createCuttingsLayout(tabSheet);
        
        add(tabSheet);
    }

	private void createCuttingsLayout(TabSheet tabSheet) {
		Div wrapper = new Div();
		wrapper.setClassName("grid-wrapper");
		
		
		tabSheet.add("Stecklinge", wrapper);
	}

	private void createStrainsLayout(TabSheet tabSheet) {
		
		Div wrapper = new Div();
		wrapper.setClassName("grid-wrapper");
			
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		
		VerticalLayout layoutButton = new VerticalLayout();
		Button addStrainButton = new Button();
		addStrainButton.setHeight(75, Unit.PIXELS);
		addStrainButton.setWidth(175, Unit.PIXELS);
		
		addStrainButton.setText("+ Sorte hinzufügen");
		
		addStrainButton.addClickListener(e -> openAddStrainDialog());
		
		layoutButton.setAlignItems(Alignment.CENTER);
		layoutButton.add(addStrainButton);
		
		H2 balance = new H2("Kontingent:");

		VerticalLayout layout = new VerticalLayout();
		layout.add(balance);
		layout.add(amount);
		layout.setAlignItems(Alignment.CENTER);

		horizontalLayout.add(layoutButton);
		horizontalLayout.add(layout);
		wrapper.add(horizontalLayout);
		
		strainGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
		strainGrid.addColumn(p -> p.getName()).setHeader("Name").setAutoWidth(true).setSortable(true);
		strainGrid.addColumn(p -> renderDate(p.getDatePlanted())).setHeader("Gepflanzt am").setAutoWidth(true).setSortable(true);
		strainGrid.addColumn(p -> renderDate(p.getDateFinished())).setHeader("Geerntet am").setAutoWidth(true).setSortable(true);
		strainGrid.addColumn(p -> p.getAmount() + " Gramm").setHeader("Vorhandene Menge").setAutoWidth(true).setSortable(true);
		strainGrid.addColumn(p -> p.getThc() + "%").setHeader("THC").setAutoWidth(true).setSortable(true);
		strainGrid.addColumn(p -> p.getStatus().getLabel()).setHeader("Status").setAutoWidth(true).setSortable(true);
		
		strainGrid.addComponentColumn(item -> new Button("Status aktualisieren", click -> {
			changeStrain = item;
			statusBox.setValue(changeStrain.getStatus());
			if (changeStrain.getDateFinished() != null) {
				dateHarvested.setValue(LocalDateTime.of(changeStrain.getDateFinished(), LocalTime.now()));
			}
			changeStrainStatusDialog.open();
        }));
		
		refreshGrid();
		
		wrapper.add(strainGrid);
		
		tabSheet.add("Sorten", wrapper);
	}
	
	private void createChangeStatusDialog() {
		
		VerticalLayout layout = new VerticalLayout();
		H2 title = new H2("Status aktualisieren");
		Hr hr = new Hr();
		
		statusBox.setItems(GrowStatus.values());
		statusBox.setItemLabelGenerator(e -> e.getLabel());
		statusBox.setWidthFull();
		
		dateHarvested = new DateTimePicker("Geerntet am");
		dateHarvested.setEnabled(false);
		
		statusBox.addValueChangeListener(e -> {
			if (e.getValue() == GrowStatus.HARVESTED || e.getValue() == GrowStatus.VERIFYING
					|| e.getValue() == GrowStatus.OUTPUT_READY) {

				if (changeStrain.getDateFinished() == null) {
					dateHarvested.setValue(LocalDateTime.now());
					dateHarvested.setEnabled(true);
				}
			} else {
				dateHarvested.setEnabled(false);
			}
		});
		
		Button saveStatusButton = new Button("Aktualisieren", e -> {		
			changeStrain.setStatus(statusBox.getValue());
			
			if(dateHarvested.isEnabled() && changeStrain.getDateFinished() == null) {				
				changeStrain.setDateFinished(dateHarvested.getValue().toLocalDate());
			}
			strainService.update(changeStrain);
			refreshGrid();
			changeStrainStatusDialog.close();
		});
		
		Button cancelSaveStatusButton = new Button("Abbrechen", e -> changeStrainStatusDialog.close());

		layout.add(title, hr, statusBox, dateHarvested);
		
		changeStrainStatusDialog.add(layout);
		changeStrainStatusDialog.getFooter().add(cancelSaveStatusButton);
		changeStrainStatusDialog.getFooter().add(saveStatusButton);
	}
	
	private String renderDate(LocalDate datePlanted) {
		String day = "";
		String month = "";
		
		if (datePlanted != null) {
			if (datePlanted.getDayOfMonth() < 10) {
				day = "0" + String.valueOf(datePlanted.getDayOfMonth());
			} else {
				day = String.valueOf(datePlanted.getDayOfMonth());
			}

			if (datePlanted.getMonthValue() < 10) {
				month = "0" + String.valueOf(datePlanted.getMonthValue());
			} else {
				month = String.valueOf(datePlanted.getMonthValue());
			}

			return day + "." + month + "." + datePlanted.getYear();
		} else {
			return "-";
		}
	}

	private void openAddStrainDialog() {
		
		addStrainDialog = new Dialog();
		VerticalLayout headerLayout = new VerticalLayout();
		
		H2 header = new H2("Neue Sorte hinzufügen");
		Hr hr = new Hr();		
		headerLayout.add(header, hr);
		
		FormLayout formLayout = new FormLayout();
		formLayout.setMaxWidth(1000, Unit.PIXELS);
		
		TextField nameField = new TextField("Name");
		
		DateTimePicker date = new DateTimePicker();
		date.setLabel("Gepflanzt am");
		date.setStep(Duration.ofSeconds(1));
		date.setValue(LocalDateTime.now());
		
		DateTimePicker dateAvailable = new DateTimePicker();
		dateAvailable.setLabel("Geerntet am");
		dateAvailable.setStep(Duration.ofSeconds(1));
		dateAvailable.setValue(LocalDateTime.now());
		dateAvailable.setEnabled(false);
		dateAvailable.setWidthFull();
		dateAvailable.addClassNames(LumoUtility.Padding.NONE, LumoUtility.Margin.NONE);
		
		ComboBox<GrowStatus> statusBox = new ComboBox<GrowStatus>("Status");
		
		VerticalLayout availableLayout = new VerticalLayout();
		availableLayout.addClassNames(LumoUtility.Padding.NONE, LumoUtility.Padding.Top.LARGE);
		availableLayout.setWidthFull();
		box = new Checkbox("Bereits geerntet");
		box.addValueChangeListener(e -> {
			if(e.getValue()) {				
				dateAvailable.setEnabled(true);	
				statusBox.setItems(GrowStatus.values());
			} else {
				dateAvailable.setEnabled(false);
				statusBox.setItems(Arrays.asList(GrowStatus.NEW, GrowStatus.GROWING, GrowStatus.READY));
			}
		});
		
		availableLayout.add(box, dateAvailable);
		
		NumberField strainInfoThc = new NumberField("THC Gehalt in Prozent");
		NumberField strainInfoAmount = new NumberField("Menge in Gramm");
		
		statusBox.setItems(Arrays.asList(GrowStatus.NEW, GrowStatus.GROWING, GrowStatus.READY));
		statusBox.setItemLabelGenerator(e -> e.getLabel());
		statusBox.setValue(GrowStatus.NEW);
		
		TextField amountOfPlantsField = new TextField("Anzahl Pflanzen");
		amountOfPlantsField.setAllowedCharPattern("[0-9/]");
		
		formLayout.add(nameField, strainInfoThc, strainInfoAmount, amountOfPlantsField, date, statusBox, availableLayout);
		
		formLayout.setColspan(nameField, 2);
		addStrainDialog.add(headerLayout);
		addStrainDialog.add(formLayout);
		
		Button saveButton = new Button("Hinzufügen", e -> {

			if (!nameField.isEmpty()) {
				
				addNewStrain(nameField.getValue(), date.getValue(), dateAvailable.getValue(), strainInfoAmount, strainInfoThc, statusBox);
				addStrainDialog.close();

			} else {
				Notification.show("Die Sorte muss einen Namen haben !");
			}
		});

		Button cancelButton = new Button("Abbrechen", e -> addStrainDialog.close());

		addStrainDialog.getFooter().add(cancelButton);
		addStrainDialog.getFooter().add(saveButton);
		addStrainDialog.open();
	}

	private void addNewStrain(String name, LocalDateTime dateBegin, LocalDateTime dateEnd, NumberField strainInfoAmount, NumberField strainInfoThc, ComboBox<GrowStatus> statusBox) {
		
		Strain newStrain = new Strain();
		
		newStrain.setName(name);
		newStrain.setDatePlanted(dateBegin.toLocalDate());
		
		if(box.getValue()) {			
			newStrain.setDateFinished(dateEnd.toLocalDate());
		}
		newStrain.setAmount(strainInfoAmount.getValue());
		newStrain.setThc(strainInfoThc.getValue());
		newStrain.setAssociationId(associationId);
		newStrain.setStatus(statusBox.getValue());
		
		int amountPerMember = strainInfoAmount.getValue().intValue() / personService.findAllByAssociation(associationId).size();
		newStrain.setAmountPerMember(amountPerMember);
		
		strainService.update(newStrain);
		refreshGrid();
	}
	
	private void refreshGrid() {
		
		if(allByAssociation.isEmpty()) {			
			allByAssociation = strainService.findAllByAssociation(associationId);
		}
		
		if(outputAssociation.isEmpty()) {
			outputAssociation = outputService.findAllByAssociation(associationId);
		}
		
		this.strainGrid.setItems(allByAssociation);
		this.outputGrid.setItems(outputAssociation.stream().filter(e -> !e.isOutdated()).toList());
		
		double generalAmount = 0;
		for(Strain strain : allByAssociation) {
			generalAmount = generalAmount + strain.getAmount();
		}
		
		this.amount.setText(String.valueOf(generalAmount) + " Gramm");
		this.amount2.setText(String.valueOf(generalAmount) + " Gramm");
	}
}
