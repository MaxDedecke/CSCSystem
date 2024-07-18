package com.css.one.views.warenlager;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.vaadin.addons.MoneyField;

import com.css.one.data.GrowStatus;
import com.css.one.data.Location;
import com.css.one.data.Output;
import com.css.one.data.Person;
import com.css.one.data.Strain;
import com.css.one.services.LocationService;
import com.css.one.services.OutputService;
import com.css.one.services.PersonService;
import com.css.one.services.StrainService;
import com.css.one.views.MainLayout;
import com.vaadin.flow.component.Component;
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
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
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
    private LocationService locationService;
    
    private int associationId;
    
    Dialog addStrainDialog;
    Dialog addOutputDialog;
    
	Grid<Strain> strainGrid = new Grid<Strain>();
	Grid<Output> outputGrid = new Grid<Output>();
	
	ComboBox<GrowStatus> statusBox = new ComboBox<GrowStatus>("Status");
	Checkbox box;
	DateTimePicker dateHarvested;
	ComboBox<Location> locationBox;
	
	ComboBox<Person> responsiblePersonOne;
	ComboBox<Person> responsiblePersonTwo;
	
	H2 amount = new H2("0 Gramm");
	H2 amount2 = new H2("0 Gramm");

	List<Strain> allByAssociation = new ArrayList<>();
    List<Output> outputAssociation = new ArrayList<>();
    
    Dialog changeStrainStatusDialog = new Dialog();
    
    Strain changeStrain;
    
    private Upload uploadCertificate;
    
    private TextField nameField;
    private DateTimePicker date;
    private DateTimePicker dateAvailable;
    private NumberField strainInfoThc;
    private NumberField strainInfoAmount;
    private TextField numberField;
    private TextField amountOfPlantsField;
    private MoneyField amountPerGramm;

	public WarenlagerView(StrainService strainService, OutputService outputService, PersonService personService, LocationService locationService) {
		this.strainService = strainService;
		this.outputService = outputService;
		this.personService = personService;
		this.locationService = locationService;
		
        addClassNames("warenlager-view");         
        associationId = MainLayout.getAssociationId();
        
        createChangeStatusDialog();
        
        TabSheet tabSheet = new TabSheet();
        tabSheet.addClassNames(LumoUtility.Margin.NONE);
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
		
		VerticalLayout wrapper = new VerticalLayout();
		wrapper.setHeightFull();	
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		
		VerticalLayout layoutButton = new VerticalLayout();
		layoutButton.addClassNames(LumoUtility.Padding.Left.NONE);

		Button addStrainButton = new Button();
		addStrainButton.addClassNames("button-layout-common");
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
		strainGrid.addColumn(p -> p.getStrainNumber()).setHeader("Nummer");
		strainGrid.addColumn(p -> p.getName()).setHeader("Name").setAutoWidth(true).setSortable(true);
		strainGrid.addColumn(p -> renderDate(p.getDatePlanted())).setHeader("Gepflanzt am").setAutoWidth(true).setSortable(true);
		strainGrid.addColumn(p -> renderDate(p.getDateFinished())).setHeader("Geerntet am").setAutoWidth(true).setSortable(true);
		strainGrid.addColumn(p -> p.getAmountGramm() == 0 ? "-" : p.getAmountGramm() + " Gramm").setHeader("Vorhandene Menge").setAutoWidth(true).setSortable(true);
		strainGrid.addColumn(p -> p.getStatus().getLabel()).setHeader("Status").setAutoWidth(true).setSortable(true);
		
		strainGrid.addComponentColumn(item -> new Button("Status aktualisieren", click -> {
			changeStrain = item;
			statusBox.setValue(changeStrain.getStatus());
			if (changeStrain.getDateFinished() != null) {
				dateHarvested.setValue(LocalDateTime.of(changeStrain.getDateFinished(), LocalTime.now()));
			}
			changeStrainStatusDialog.open();
        })).setAutoWidth(true);
		
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
		HorizontalLayout headlineLayout = new HorizontalLayout();
		
		H2 header = new H2("Neue Sorte hinzufügen");
		box = new Checkbox("Bereits geerntet");
		box.addValueChangeListener(e -> {
			if(e.getValue()) {				
				dateAvailable.setEnabled(true);
				statusBox.setItems(GrowStatus.values());
				responsiblePersonOne.setEnabled(true);
				responsiblePersonTwo.setEnabled(true);
				strainInfoAmount.setEnabled(true);
				strainInfoThc.setEnabled(true);
				amountPerGramm.setEnabled(true);
			} else {
				dateAvailable.setEnabled(false);
				statusBox.setItems(Arrays.asList(GrowStatus.NEW, GrowStatus.GROWING, GrowStatus.READY));
				responsiblePersonOne.setEnabled(false);
				responsiblePersonTwo.setEnabled(false);
				strainInfoAmount.setEnabled(false);
				strainInfoThc.setEnabled(false);
				amountPerGramm.setEnabled(false);
			}
		});
		
		box.addClassNames(LumoUtility.Padding.Top.XSMALL);
		headlineLayout.add(header, box);
		
		Hr hr = new Hr();		
		headerLayout.add(headlineLayout, hr);
		
		HorizontalLayout layout = new HorizontalLayout();
		layout.addClassNames(LumoUtility.Margin.Left.MEDIUM, LumoUtility.Margin.Right.MEDIUM);
		FormLayout formLayout = createFirstComponent();
		FormLayout harvestedLayout = createSecondComponent();
		
		layout.add(formLayout, harvestedLayout, createUploadComponent());
		addStrainDialog.add(headerLayout);
		addStrainDialog.add(layout);
		
		Button saveButton = new Button("Hinzufügen", e -> {

			if (!nameField.isEmpty()) {
				
				addNewStrain(nameField.getValue(), date.getValue(), dateAvailable.getValue(), strainInfoAmount, strainInfoThc, statusBox);
				addStrainDialog.close();

			} else { 
				Notification.show("Die Sorte muss einen Namen haben !");
			}
		});
		saveButton.addClassName("save-button");
		
		Button cancelButton = new Button("Abbrechen", e -> addStrainDialog.close());
		cancelButton.addClassNames("cancel-button");
		
		addStrainDialog.getFooter().add(cancelButton);
		addStrainDialog.getFooter().add(saveButton);
		addStrainDialog.open();
	}
	
	private Component createUploadComponent() {
		uploadCertificate = new Upload();
		uploadCertificate.setDropAllowed(false);
		uploadCertificate.setMaxFiles(1);

		UploadI18N i18n = new UploadI18N();
        i18n.setDropFiles(new UploadI18N.DropFiles().setOne("Datei hierhin ziehen...").setMany("Dateien hierhin ziehen..."));
        i18n.setAddFiles(new UploadI18N.AddFiles().setOne("Zertifikat auswählen").setMany("Zertifikate auswählen"));
        i18n.setError(new UploadI18N.Error().setTooManyFiles("Zu viele Dateien.").setFileIsTooBig("Datei ist zu groß."));
        i18n.setUploading(new UploadI18N.Uploading().setStatus(new UploadI18N.Uploading.Status().setConnecting("Verbinden...").setStalled("Stillstand.").setProcessing("Verarbeiten der Datei..."))
                        .setRemainingTime(new UploadI18N.Uploading.RemainingTime().setPrefix("verbleibende Zeit: ").setUnknown("unbekannte verbleibende Zeit"))
                        .setError(new UploadI18N.Uploading.Error().setServerUnavailable("Server nicht verfügbar").setUnexpectedServerError("Unerwarteter Serverfehler").setForbidden("Verboten")));

        uploadCertificate.setI18n(i18n);
        
        Button uploadButton = (Button) uploadCertificate.getUploadButton();
        uploadButton.setText("Zertifikat auswählen");
		uploadButton.setEnabled(false);
		uploadCertificate.setVisible(false);
		
		return uploadCertificate;
	}

	private FormLayout createSecondComponent() {
		FormLayout formLayout = new FormLayout();
		formLayout.setMaxWidth(500, Unit.PIXELS);
		
		dateAvailable = new DateTimePicker();
		dateAvailable.setLabel("Geerntet am");
		dateAvailable.setStep(Duration.ofSeconds(1));
		dateAvailable.setValue(LocalDateTime.now());
		dateAvailable.setEnabled(false);
		dateAvailable.setWidthFull();

		List<Person> allMembers = personService.findAllByAssociation(associationId);
		responsiblePersonOne = new ComboBox<>("Gewogen durch");
		responsiblePersonOne.setItems(allMembers);
		responsiblePersonOne.setItemLabelGenerator(e -> e.getFirstName() + " " + e.getLastName());
		responsiblePersonOne.setEnabled(false);
		
		responsiblePersonTwo = new ComboBox<>("Gewogen durch");
		responsiblePersonTwo.setItems(allMembers);
		responsiblePersonTwo.setItemLabelGenerator(e -> e.getFirstName() + " " + e.getLastName());
		responsiblePersonTwo.setEnabled(false);
		
		strainInfoAmount = new NumberField("Menge in Gramm");
		strainInfoAmount.setEnabled(false);
		
		strainInfoThc = new NumberField("THC Gehalt in Prozent");		
		strainInfoThc.setEnabled(false);
		
		amountPerGramm = new MoneyField();
		amountPerGramm.setEnabled(false);
		amountPerGramm.setLabel("Preis pro Gramm");
		amountPerGramm.setCurrency("EUR");
		
		formLayout.add(dateAvailable, responsiblePersonOne, responsiblePersonTwo, strainInfoAmount, strainInfoThc, amountPerGramm);
		
		return formLayout;
	}

	private FormLayout createFirstComponent() {
		
		FormLayout formLayout = new FormLayout();
		formLayout.setMaxWidth(500, Unit.PIXELS);
		
		numberField = new TextField("Nummer");
		numberField.setValue(String.valueOf(strainService.getFreeMemberNumber(associationId)));
		numberField.setEnabled(false);
		
		nameField = new TextField("Name");
		
		date = new DateTimePicker();
		date.setLabel("Gepflanzt am");
		date.setStep(Duration.ofSeconds(1));
		date.setValue(LocalDateTime.now());
		
		statusBox = new ComboBox<GrowStatus>("Status");
		
		statusBox.setItems(Arrays.asList(GrowStatus.NEW, GrowStatus.GROWING, GrowStatus.READY));
		statusBox.setItemLabelGenerator(e -> e.getLabel());
		statusBox.setValue(GrowStatus.NEW);
		
		statusBox.addValueChangeListener(e -> {
			
			if(e.getValue() == GrowStatus.VERIFYING || e.getValue() == GrowStatus.OUTPUT_READY) {					
				uploadCertificate.setDropAllowed(true);
				Button uploadButton = (Button) uploadCertificate.getUploadButton();
				uploadButton.setEnabled(true);
				uploadCertificate.setVisible(true);

			} else {
				uploadCertificate.setDropAllowed(false);
				Button uploadButton = (Button) uploadCertificate.getUploadButton();
				uploadButton.setEnabled(false);
				uploadCertificate.setVisible(false);
			}
		});
		
		locationBox = new ComboBox<>("Standort");
		locationBox.setItems(locationService.findAllByAssociation(associationId));
		locationBox.setValue(locationBox.getListDataView().getItem(0));
		locationBox.setItemLabelGenerator(e -> e.getName());
		
		amountOfPlantsField = new TextField("Anzahl Pflanzen");
		amountOfPlantsField.setAllowedCharPattern("[0-9/]");
		
		formLayout.add(numberField, nameField, amountOfPlantsField, date, locationBox, statusBox);
		
//		formLayout.setColspan(nameField, 2);
		return formLayout;
	}

	private void addNewStrain(String name, LocalDateTime dateBegin, LocalDateTime dateEnd, NumberField strainInfoAmount, NumberField strainInfoThc, ComboBox<GrowStatus> statusBox) {
		
		Strain newStrain = new Strain();
		newStrain.setStrainNumber(Integer.valueOf(numberField.getValue()));
		newStrain.setName(name);
		newStrain.setDatePlanted(dateBegin.toLocalDate());
		newStrain.setAssociationId(associationId);
		newStrain.setStatus(statusBox.getValue()); 
		
		if(box.getValue()) {			
			newStrain.setDateFinished(dateEnd.toLocalDate());
			newStrain.setAmountGramm(strainInfoAmount.getValue());
			newStrain.setThc(strainInfoThc.getValue());
			newStrain.setWeighedByMembers(Arrays.asList(responsiblePersonOne.getValue(), responsiblePersonTwo.getValue()));
			newStrain.setAmountOfPlants(Integer.valueOf(amountOfPlantsField.getValue()));
			newStrain.setPricePerGram(amountPerGramm.getValue().getNumber().doubleValue());
			newStrain.setGrowLocation(locationBox.getValue());
			
			if(statusBox.getValue().ordinal() > 3) {
				//TODO
				newStrain.setPathOfCertificate("");
			}
		}
		 
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
			generalAmount = generalAmount + strain.getAmountGramm();
		}
		
		this.amount.setText(String.valueOf(generalAmount) + " Gramm");
		this.amount2.setText(String.valueOf(generalAmount) + " Gramm");
	}
}
