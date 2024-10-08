package com.css.one.views.warenlager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;

import org.vaadin.addons.MoneyField;
import org.vaadin.lineawesome.LineAwesomeIcon;

import com.css.one.data.Blossom;
import com.css.one.data.Charge;
import com.css.one.data.Cutting;
import com.css.one.data.EntityWrapper;
import com.css.one.data.GrowStatus;
import com.css.one.data.Location;
import com.css.one.data.Output;
import com.css.one.data.Person;
import com.css.one.data.Plant;
import com.css.one.data.Seed;
import com.css.one.services.BlossomService;
import com.css.one.services.ChargeService;
import com.css.one.services.CuttingService;
import com.css.one.services.LocationService;
import com.css.one.services.OutputService;
import com.css.one.services.PersonService;
import com.css.one.services.PlantService;
import com.css.one.services.SeedService;
import com.css.one.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.PermitAll;
import jakarta.xml.bind.annotation.XmlElementDecl.GLOBAL;

@PageTitle("Bestand")
@Route(value = "waren", layout = MainLayout.class)
@PermitAll
public class WarenlagerView extends Div {
	
    private static final long serialVersionUID = 5652277988730640569L;
    private OutputService outputService;
    private PersonService personService;
    private BlossomService blossomService;
    private LocationService locationService;
    private CuttingService cuttingService;	
    private SeedService seedService;
    private ChargeService chargeService;
    private PlantService plantService;
    
    private int associationId;
    
	Grid<Blossom> blossomGrid = new Grid<Blossom>();
	Grid<Output> outputGrid = new Grid<Output>();
	Grid<Cutting> cuttingsGrid = new Grid<Cutting>();
	Grid<Seed> seedsGrid = new Grid<Seed>();
	Grid<Plant> plantGrid = new Grid<Plant>();

	TreeGrid<EntityWrapper> chargeGrid = new TreeGrid<>();

	ComboBox<GrowStatus> statusBox = new ComboBox<GrowStatus>("Status");
	Checkbox isFreshCuttingBox;
	Checkbox isFreshSeedBox;
	Checkbox singlePlantBox;
	Checkbox setCustomSettingsBox = new Checkbox("Erweitert");

	DatePicker dateHarvested;
	ComboBox<Location> locationBox;
	
	ComboBox<Person> responsiblePersonOne;
	ComboBox<Person> responsiblePersonTwo;
	ComboBox<Plant> comboboxCuttingsOrigins = new ComboBox<Plant>("Mutterpflanze");
	ComboBox<Plant> comboboxSeedsOrigins = new ComboBox<Plant>("Mutterpflanze");
	ComboBox<Plant> comboboxBlossomOrigins = new ComboBox<Plant>("Mutterpflanze");
	ComboBox<Person> comboBoxResponsibleForSeed = new ComboBox<Person>("Verantwortlicher");

	ComboBox<GrowStatus> comboxCuttingStatus = new ComboBox<GrowStatus>("Status");
	ComboBox<Person> comboBoxResponsibleForCutting = new ComboBox<Person>("Verantwortlicher");
	ComboBox<Location> comboBoxLocationCutting = new ComboBox<Location>("Ort");
	ComboBox<Location> comboBoxLocationSeed = new ComboBox<Location>("Lagerort");

	H2 amount = new H2("0 Gramm");
	H2 amountCuttings = new H2("0 Stück");
	H2 amountSeeds = new H2("0 Stück");
	H2 headerClonePlant = new H2("Pflanze " + "Pflanze1" + " klonen");

	List<Blossom> allStrainsByAssociation = new ArrayList<>();
    List<Output> outputAssociation = new ArrayList<>();
	List<Plant> tmpPlants = new ArrayList<>();

	Dialog addBlossomDialog;
	Dialog addOutputDialog;
	Dialog clonePlantDialog = new Dialog();
	Dialog changeStatusDialog = new Dialog();
    Dialog addCuttingsDialog = new Dialog();
    Dialog addSeedsDialog = new Dialog();
    Dialog addChargeDialog = new Dialog();
    Dialog editSinglePlantDialog = new Dialog();
    Dialog convertPlantDialog = new Dialog();
    Dialog addPlantsDialog = new Dialog();
    
    Blossom changeBlossom;
    Cutting changeCutting;
    Seed changeSeed;
    Charge changeCharge;
    Plant editPlant;
    
    EntityWrapper changeExistingPlant;
    EntityWrapper statusEntity;
    EntityWrapper convertEntity;
    
	Button updatePlantButton = new Button("update");
	Button saveChargeButton;
	
    File pathToCertificate;
    InputStream streamCertificate;
    String directoryPath;
    
    private Upload uploadCertificate;
    
    private TextField nameField;
    private DatePicker dateBlossomHarvested;
    private NumberField strainInfoThc;
    private NumberField strainInfoAmount;
    private TextField numberField;
    private MoneyField amountPerGramm;
    
	private TextField chargeNameField = new TextField("Name");
	private DatePicker chargeRegDate = new DatePicker("Gepflanzt am");
	private NumberField chargeAmountPlantsField = new NumberField("Anzahl Pflanzen");
	private TextField chargeNumberField = new TextField("Nummer");
	private TextField plantNameField = new TextField("Name");
    private ComboBox<Location> plantLocationBox = new ComboBox<Location>("Standort");
    private ComboBox<GrowStatus> plantStatusBox = new ComboBox<GrowStatus>("Status");
    private ComboBox<GrowStatus> statBox = new ComboBox<GrowStatus>("Status");
    
	private TextField plantEditNameField = new TextField("Name der Pflanze");
	private TextField plantEditNumberField = new TextField("Nummer der Pflanze");
	private ComboBox<GrowStatus> plantEditStatusBox = new ComboBox<GrowStatus>("Status");
	private ComboBox<Location> plantEditLocationBox = new ComboBox<Location>("Standort");

    private TextField cuttingNumberField;
    private TextField cuttingNameField;
    private DatePicker cuttingPlantDate;
    private TextField cuttingsAmountField;
    private TextField cuttingsPriceField;
    private TextField patternName;
    
    private TextField seedNameField;
    private TextField seedNumberField;
    private TextField seedPriceField;
    private TextField seedsAmountField;
    
    private Button deleteBlossomButton;
    private Button deleteCuttingButton;
    private Button deleteSeedButton;
    
    private Button deleteChargeButton;
    
    public enum ViewStatus {
		CHARGE, STRAIN, CUTTING, SEED
	}
    
	public WarenlagerView(BlossomService strainService, OutputService outputService, PersonService personService, LocationService locationService,
			CuttingService cuttingService, SeedService seedService, ChargeService chargeService, PlantService plantService) {
		this.blossomService = strainService;
		this.outputService = outputService;
		this.personService = personService;
		this.locationService = locationService;
		this.cuttingService = cuttingService;
		this.seedService = seedService;
		this.chargeService = chargeService;
		this.plantService = plantService;
		
        addClassNames("warenlager-view");         
        associationId = MainLayout.getAssociationId();
        
        createChangeStatusDialog();
        createSinglePlantEditDialog();
        createConvertPlantDialog();
        createAddPlantsDialog();
        
        TabSheet tabSheet = new TabSheet();
        tabSheet.addClassNames(LumoUtility.Margin.NONE);
        tabSheet.setSizeFull();
        
        createChargeLayout(tabSheet);
        createBlossomLayout(tabSheet);
        createCuttingsLayout(tabSheet);
        createSeedsLayout(tabSheet);
        
        createAddBlossomDialog();
        createAddCuttingsDialog();
        createAddSeedsDialog();
        createAddChargeDialog();
        createClonePlantDialog();
        
        add(tabSheet);
    }

	private void createAddPlantsDialog() {
		
		addPlantsDialog.add(createAddPlantsDialogContent());
		
		Button cancelButton = new Button("zurück");
		cancelButton.addClassName("cancel-button");	
		cancelButton.addClickListener(e -> {
			clearAddPlantsDialog();
			addPlantsDialog.close();
		});
		
		Button addPlantsButton = new Button("hinzufügen");
		addPlantsButton.addClassName("save-button");
		addPlantsButton.addClickListener(e -> {
			savePlantsToCharge();
			clearAddPlantsDialog();
			addPlantsDialog.close();
			refreshGrid(ViewStatus.CHARGE);
		});
		
		addPlantsDialog.getFooter().add(cancelButton);
		addPlantsDialog.getFooter().add(addPlantsButton);
	}
	
	private void savePlantsToCharge() {
		
		Optional<Charge> charge = chargeService.findAllByAssociation(associationId).stream().filter(e -> e.getId().equals(statusEntity.getId())).findAny();
		if ((!tmpPlants.isEmpty()) && charge.isPresent()) {
			
			Charge tmpCharge = charge.get();
			List<Plant> finalPlants = new ArrayList<>();
			tmpPlants.forEach(e -> {
				finalPlants.add(plantService.update(e));
			});
			
			tmpCharge.setPlants(finalPlants);
			chargeService.update(tmpCharge);
		} else {
			Notification.show("Es muss mindestens eine Pflanze existieren.");
		}
	}

	private void clearAddPlantsDialog() {
		patternName.setValue(patternName.getEmptyValue());
		chargeAmountPlantsField.setValue(chargeAmountPlantsField.getEmptyValue());
		plantLocationBox.setValue(plantLocationBox.isEmpty() ? plantLocationBox.getEmptyValue() : plantLocationBox.getListDataView().getItem(0));
		plantStatusBox.setValue(plantStatusBox.getListDataView().getItem(0));	
		setCustomSettingsBox.setValue(false);
		tmpPlants = new ArrayList<Plant>();
	}

	private void createConvertPlantDialog() {
		
		VerticalLayout wrapper = new VerticalLayout();
		addChargeDialog.addClassNames(LumoUtility.MaxWidth.SCREEN_LARGE);
		
		VerticalLayout headerLayout = new VerticalLayout();
		HorizontalLayout headlineLayout = new HorizontalLayout();
		
		H2 header = new H2("In Blüten umwandeln");
		headlineLayout.add(header);

		Hr hr = new Hr();		
		headerLayout.add(headlineLayout, hr);
		headerLayout.addClassNames(LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
		
		HorizontalLayout layout = new HorizontalLayout();
		layout.addClassNames(LumoUtility.Margin.Left.MEDIUM, LumoUtility.Margin.Right.MEDIUM);
		
		FormLayout plantDetailsLayout = new FormLayout();
		
		layout.add(plantDetailsLayout);
		wrapper.add(headerLayout, layout);
		
		VerticalLayout convertPlantWrapper = new VerticalLayout();
		
		Button cancelButton = new Button("zurück");
		cancelButton.addClassName("cancel-button");	
		cancelButton.addClickListener(e -> {
//			clearPlantEditDialog();
		});
		
		Button convertButton = new Button("erstellen");
		convertButton.addClassName("save-button");
		
		convertPlantDialog.getFooter().add(cancelButton);
		convertPlantDialog.getFooter().add(convertButton);
		convertPlantDialog.add(wrapper, convertPlantWrapper);

	}

	private void createSinglePlantEditDialog() {
		VerticalLayout wrapper = new VerticalLayout();
		addChargeDialog.addClassNames(LumoUtility.MaxWidth.SCREEN_LARGE);
		
		VerticalLayout headerLayout = new VerticalLayout();
		HorizontalLayout headlineLayout = new HorizontalLayout();
		
		H2 header = new H2("Pflanze bearbeiten");
		
		headlineLayout.add(header);

		Hr hr = new Hr();		
		headerLayout.add(headlineLayout, hr);
		headerLayout.addClassNames(LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
		
		HorizontalLayout layout = new HorizontalLayout();
		layout.addClassNames(LumoUtility.Margin.Left.MEDIUM, LumoUtility.Margin.Right.MEDIUM);
		
		FormLayout plantDetailsLayout = new FormLayout();
		
		layout.add(plantDetailsLayout);
		wrapper.add(headerLayout, layout);
		
		VerticalLayout plantEditWrapper = new VerticalLayout();
		FormLayout plantLayout = new FormLayout();		
		
		plantEditNumberField.setEnabled(false);
		
		plantEditLocationBox.setItems(locationService.findAllByAssociation(associationId));
		plantEditLocationBox.setItemLabelGenerator(e -> e.getName());
		
		plantEditStatusBox.setItems(Arrays.asList(GrowStatus.NEW_PLANTED, GrowStatus.GROWING, GrowStatus.READY));
		plantEditStatusBox.setItemLabelGenerator(e -> e.getLabel());
		
		plantLayout.add(plantEditNameField, plantEditNumberField, plantEditStatusBox, plantEditLocationBox);
		plantEditWrapper.add(plantLayout);
		plantEditWrapper.addClassNames(LumoUtility.Margin.Top.NONE, LumoUtility.Padding.Top.NONE);
		
		editSinglePlantDialog.addDialogCloseActionListener(e -> {
			clearPlantEditDialog();
		});
		
		Button updatePlantButton = new Button("update");
		updatePlantButton.addClassName("save-button");
		updatePlantButton.addClickListener(e -> {	
			
			if(!plantEditNameField.isEmpty()) {
				if(!plantEditLocationBox.isEmpty()) {
					if(!plantEditStatusBox.isEmpty()) {
						if(!plantEditNumberField.isEmpty()) {
							plantService.get(changeExistingPlant.getId()).ifPresentOrElse(p -> {
								p.setGrowLocation(plantEditLocationBox.getValue());
								p.setStatus(plantEditStatusBox.getValue());
								p.setName(plantEditNameField.getValue());
								plantService.update(p);
								
								Notification show = Notification.show("Details der Pflanze " + p.getName() + " aktualisiert.");
								
								show.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
								clearPlantEditDialog();
								editSinglePlantDialog.close();
								
								refreshGrid(ViewStatus.CHARGE);
								
							}, () -> {
								Notification show = Notification.show("Die ursprüngliche Entität konnte nicht gefunden werden.");
								show.addThemeVariants(NotificationVariant.LUMO_ERROR);
							});
						} else {
							Notification show = Notification.show("Die Nummer einer Pflanze muss angegeben sein.");
							show.addThemeVariants(NotificationVariant.LUMO_ERROR);
						}
					} else {
						Notification show = Notification.show("Der Status einer Pflanze muss angegeben sein.");
						show.addThemeVariants(NotificationVariant.LUMO_ERROR);
					}
				} else {
					Notification show = Notification.show("Der Standort einer Pflanze muss angegeben sein.");
					show.addThemeVariants(NotificationVariant.LUMO_ERROR);
				}	
			} else {
				Notification show = Notification.show("Der Name einer Pflanze muss angegeben sein.");
				show.addThemeVariants(NotificationVariant.LUMO_ERROR);
			}
			clearPlantEditDialog();
		});
		
		Button cancelButton = new Button("zurück");
		cancelButton.addClassName("cancel-button");	
		cancelButton.addClickListener(e -> {
			clearPlantEditDialog();
		});
		
		editSinglePlantDialog.getFooter().add(cancelButton);
		editSinglePlantDialog.getFooter().add(updatePlantButton);
		editSinglePlantDialog.add(wrapper, plantEditWrapper);
	}

	private void clearPlantEditDialog() {
		this.plantEditLocationBox.setValue(plantEditLocationBox.getEmptyValue());
		this.plantEditNameField.setValue(plantEditNameField.getEmptyValue());
		this.plantEditNumberField.setValue(plantEditNameField.getEmptyValue());
		this.plantEditStatusBox.setValue(plantEditStatusBox.getEmptyValue());
		changeExistingPlant = null;
		editSinglePlantDialog.close();	
	}

	private void createAddChargeDialog() {
		VerticalLayout wrapper = new VerticalLayout();
		addChargeDialog.addClassNames(LumoUtility.MaxWidth.SCREEN_XXLARGE);
		
		VerticalLayout headerLayout = new VerticalLayout();		
		H2 header = new H2("Neue Charge hinzufügen");
		Hr hr = new Hr();		
		headerLayout.add(header, hr);
		
		HorizontalLayout layout = new HorizontalLayout();
		layout.addClassNames(LumoUtility.Margin.Left.MEDIUM, LumoUtility.Margin.Right.MEDIUM);
		
		layout.add(createChargeDialogComponent());
		wrapper.add(headerLayout, layout);
		
		saveChargeButton = new Button("Hinzufügen", e -> {
			if (!chargeNameField.isEmpty()) {
				if (!chargeNumberField.isEmpty()) {
					addNewCharge();
					addChargeDialog.close();
					refreshGrid(ViewStatus.CHARGE);
					clearChargeDialog();
				} else {
					Notification.show("Jede Charge braucht eine Nummer !");
				}
			} else {
				Notification.show("Jede Charge braucht einen Namen !");
			}
		});
		saveChargeButton.addClassNames("save-button");
		
		Button cancelButton = new Button("Abbrechen", e -> {
			clearChargeDialog();
			addChargeDialog.close();	
			deleteChargeButton.setEnabled(true);
		});
		cancelButton.addClassNames("cancel-button");
		
		deleteChargeButton = new Button("Löschen", e -> {
			if(changeCharge != null) {
			
			chargeService.delete(changeCharge.getId());
			changeCharge.getPlants().forEach(plant -> {
				plantService.delete(plant.getId());
			});
			
			addChargeDialog.close();
			Notification.show("Charge und Pflanzen gelöscht.");
			changeCharge = null;
			refreshGrid(ViewStatus.CHARGE);
			clearChargeDialog();
			} else {
				Notification.show("Es kann nur eine existierende Charge gelöscht werden!");
			}
		});
		
		addChargeDialog.addDialogCloseActionListener(e -> {
			clearChargeDialog();
		});
		
		deleteChargeButton.addClassNames("delete-button");
		
		addChargeDialog.getFooter().add(deleteChargeButton);
		addChargeDialog.getFooter().add(cancelButton);
		addChargeDialog.getFooter().add(saveChargeButton);
	
		addChargeDialog.add(wrapper);
	}

	private void addNewCharge() {

//		List<Plant> includedPlants = new ArrayList<>();
		
		Charge charge;
		
		if(changeCharge != null) {
			charge = changeCharge;
//			tmpPlants.forEach(e -> {
//				includedPlants.add(plantService.update(e));
//			});
		} else {
			charge = new Charge();	
//			tmpPlants.forEach(e -> {
//				e.setId(0L);
//				includedPlants.add(plantService.update(e));
//			});
		}
		
		charge.setAssociationId(associationId);
		charge.setDateOfExistense(chargeRegDate.getValue() != chargeRegDate.getEmptyValue() ? chargeRegDate.getValue() : LocalDate.now());
		charge.setName(chargeNameField.getValue());
		
		chargeService.update(charge);
	}

	private void clearChargeDialog() {
		this.chargeNameField.setValue(chargeNameField.getEmptyValue());
		this.chargeRegDate.setValue(LocalDate.now());
		this.chargeAmountPlantsField.setValue(chargeAmountPlantsField.getEmptyValue());
		refreshPlantsGrid(0);
	}
	
	private void refreshPlantsGrid(int amount) {
		
		int count;
    	List<Location> locationsByAssociation = locationService.findAllByAssociation(associationId);

		if (!tmpPlants.isEmpty()) {
			count = tmpPlants.size();
			if(amount < tmpPlants.size()) {
				//remove items from tmpPlants
				int diff = tmpPlants.size() - amount;	
				int size = tmpPlants.size();
				for(int i = tmpPlants.size()-1; i >= (size-diff); i--) {
					tmpPlants.remove(i);
				}
			} else {
				//add items to tmpPlants
				int diff = amount - tmpPlants.size();		
				for(int i = 0; i < diff; i++) {
					Plant p = new Plant();
					p.setAssociationId(associationId);
					p.setDateOfExistense(LocalDate.now());
					p.setName(chargeNameField.getValue() + "_" + (count + i));
					p.setId(Integer.toUnsignedLong(count + i));
					p.setStatus(GrowStatus.NEW_PLANTED);
					p.setGrowLocation(locationsByAssociation.get(0));
					tmpPlants.add(p);
				}
			}
			
		} else {
			count = plantService.count();
			for (int i = 0; i < amount; i++) {
				Plant p = new Plant();
				p.setAssociationId(associationId);
				p.setDateOfExistense(LocalDate.now());
				p.setName(chargeNameField.getValue() + "_" + (count + i));
				p.setId(Integer.toUnsignedLong(count + i));
				p.setStatus(GrowStatus.NEW_PLANTED);
				p.setGrowLocation(locationsByAssociation.get(0));
				tmpPlants.add(p);
			}
		}
		plantGrid.setItems(tmpPlants);
	}

	private Component createChargeDialogComponent() {
		
		HorizontalLayout mainLayout = new HorizontalLayout();
		mainLayout.addClassNames(LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
		
		VerticalLayout firstWrapper = new VerticalLayout();
		FormLayout layout = new FormLayout();
		layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
		
		chargeNumberField.setEnabled(false);
		chargeNumberField.setValue(String.valueOf((chargeService.count() + 1)));
		chargeRegDate.setValue(LocalDate.now());
			
		chargeNumberField.setWidthFull();
		chargeNameField.setWidthFull();
		chargeRegDate.setWidthFull();
		
		layout.add(chargeNumberField, chargeNameField, chargeRegDate);
		
		firstWrapper.add(layout);
		firstWrapper.setMaxWidth(400, Unit.PIXELS);
		mainLayout.add(firstWrapper);
		return mainLayout;
	}
	
	private Component createAddPlantsDialogContent() {

		VerticalLayout amountLayout = new VerticalLayout();
		amountLayout.addClassNames(LumoUtility.Margin.NONE, 
				LumoUtility.Padding.NONE, LumoUtility.FlexDirection.COLUMN, LumoUtility.Display.FLEX, LumoUtility.AlignItems.START,  "rechtliches-box", LumoUtility.Width.FULL, LumoUtility.Height.FULL);
		Button addPlantsForAmountButton = new Button("übernehmen");
		amountLayout.setHeight(450, Unit.PIXELS);
		addPlantsForAmountButton.addClassNames("save-button");
		ComboBox<Location> globalLocationBox = new ComboBox<Location>("Standort");
		globalLocationBox.setWidthFull();
		ComboBox<GrowStatus> globalStatusBox = new ComboBox<GrowStatus>("Status");
		globalStatusBox.setWidthFull();
		
		addPlantsForAmountButton.addClickListener(e -> {
			int amountOfPlants = 0;
			if(!chargeAmountPlantsField.isEmpty()) {
				amountOfPlants = chargeAmountPlantsField.getValue().intValue();
			}
			
			for(int i = 0; i < amountOfPlants; i++) {
				Plant plant = new Plant();
				plant.setName(patternName.isEmpty() ? "Pflanze_" + i : patternName.getValue());
				plant.setAssociationId(associationId);
				plant.setDateOfExistense(LocalDate.now());
				if (setCustomSettingsBox.getValue()) {
					plant.setGrowLocation(globalLocationBox.isEmpty() ? null : globalLocationBox.getValue());
					plant.setStatus(globalStatusBox.getValue());
				}
				tmpPlants.add(plant);
			}
			
			plantGrid.setItems(tmpPlants);
		});
		
		patternName = new TextField("Name der Pflanzen");
		patternName.setWidthFull();
		chargeAmountPlantsField.setWidthFull();
		addPlantsForAmountButton.setWidthFull();
		setCustomSettingsBox.addClassNames(LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
		
		amountLayout.add(chargeAmountPlantsField, patternName, setCustomSettingsBox, globalLocationBox, globalStatusBox, addPlantsForAmountButton);
		
		VerticalLayout secondLayout = new VerticalLayout();
		secondLayout.addClassNames(LumoUtility.Margin.NONE, 
				LumoUtility.Padding.NONE, LumoUtility.FlexDirection.COLUMN, LumoUtility.Display.FLEX, LumoUtility.AlignItems.START);
		secondLayout.setHeight("100%");
		
		HorizontalLayout globalSettingsLayout = new HorizontalLayout();
		globalSettingsLayout.addClassNames("rechtliches-box-horizontal-start");
		globalSettingsLayout.setWidthFull();
		
		setCustomSettingsBox.addClassNames(LumoUtility.Margin.Left.MEDIUM, LumoUtility.Margin.Top.SMALL);
		List<Location> locationsByAssociation = locationService.findAllByAssociation(associationId);
		globalLocationBox.setItems(locationsByAssociation);
		globalLocationBox.setValue(locationsByAssociation.size() > 0 ? globalLocationBox.getListDataView().getItem(0) : globalLocationBox.getEmptyValue());
		globalLocationBox.setItemLabelGenerator(e -> e.getName());
		globalLocationBox.setEnabled(false);
		globalLocationBox.addValueChangeListener(e -> {
			tmpPlants.forEach(p -> {
				p.setGrowLocation(e.getValue());
			});
			plantGrid.setItems(tmpPlants);
		});
	
		globalStatusBox.setItems(Arrays.asList(GrowStatus.SPROUTING, GrowStatus.NEW_PLANTED, GrowStatus.GROWING, GrowStatus.READY));
		globalStatusBox.setItemLabelGenerator(e -> e.getLabel());
		globalStatusBox.setValue(globalStatusBox.getListDataView().getItem(0));
		globalStatusBox.setEnabled(false);
		globalStatusBox.addValueChangeListener(e -> {
			tmpPlants.forEach(p -> {
				p.setStatus(e.getValue());
			});
			plantGrid.setItems(tmpPlants);
		});
		
		setCustomSettingsBox.addValueChangeListener(e -> {
			globalLocationBox.setEnabled(e.getValue());
			globalStatusBox.setEnabled(e.getValue());
		});
		
		HorizontalLayout plantWrapper = new HorizontalLayout();
		plantWrapper.addClassNames("rechtliches-box-horizontal");
		plantWrapper.setWidthFull();
		
		plantGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
		plantGrid.addColumn(e -> e.getName()).setAutoWidth(true);
		plantGrid.setMinWidth(300, Unit.PIXELS);
		plantGrid.setMinHeight(450, Unit.PIXELS);
		plantGrid.setSelectionMode(SelectionMode.SINGLE);
		
		plantGrid.addSelectionListener(e -> {
			
			e.getFirstSelectedItem().ifPresentOrElse(plant -> {
				plantNameField.setEnabled(true);
				updatePlantButton.setEnabled(true);
				plantNameField.setValue(plant.getName());
				plantLocationBox.setEnabled(true);
				plantLocationBox.setValue(plant.getGrowLocation() == null ? plantLocationBox.getEmptyValue() : plant.getGrowLocation());
				plantStatusBox.setEnabled(true);
				plantStatusBox.setValue(plant.getStatus() == null ? plantStatusBox.getEmptyValue() : plant.getStatus());
				editPlant = plant;
			}, () -> {
				plantNameField.setEnabled(false);
				plantNameField.setValue(plantNameField.getEmptyValue());
				plantLocationBox.setEnabled(false);
				plantStatusBox.setEnabled(false);
				editPlant = null;
			});
			
		});
		
//		globalSettingsLayout.add(checkBoxWrapper, globalLocationBox, globalStatusBox);
		
		H3 headerPlant = new H3("Details");
		VerticalLayout formLayoutWrapper = new VerticalLayout();
		FormLayout plantLayout = new FormLayout();
		plantLayout.addClassNames(LumoUtility.Margin.Right.XSMALL);
		plantNameField.setEnabled(false);
		formLayoutWrapper.addClassName("bottom-layout");
		
		plantStatusBox.setItems(GrowStatus.values());
		plantStatusBox.setItemLabelGenerator(e -> e.getLabel());
		
		plantLocationBox.setItems(locationService.findAllByAssociation(associationId));
		plantLocationBox.setItemLabelGenerator(e -> e.getName());
		
		updatePlantButton.setEnabled(false);
		updatePlantButton.addClassNames("save-button", LumoUtility.Margin.Top.MEDIUM);
		updatePlantButton.setWidthFull();
		updatePlantButton.addClickListener(e -> {
			Plant tmpPlant;
			
			if(changeCharge != null) {
				tmpPlant = editPlant;
			} else {	
				tmpPlant = new Plant();
				tmpPlant.setId(0L);
				tmpPlant.setDateOfExistense(LocalDate.now());
				tmpPlant.setAssociationId(associationId);
			}
			
			tmpPlant.setName(plantNameField.getValue());
			tmpPlant.setGrowLocation(plantLocationBox.getValue());
			tmpPlant.setStatus(plantStatusBox.getValue());
			if (editPlant.getId() != null) {
				tmpPlants.removeIf(p -> p.getNummer().equals(editPlant.getNummer()));
				tmpPlants.sort((o1, o2) -> Integer.compare(o1.getId().intValue(), o2.getId().intValue()));
			}
			tmpPlants.add(tmpPlant);
			
			plantGrid.setItems(tmpPlants);
			editPlant = null;
		});
		
		plantLayout.add(plantNameField, plantStatusBox, plantLocationBox);
		plantLayout.setMinWidth(300, Unit.PIXELS);
		plantLayout.setHeightFull();
		formLayoutWrapper.add(headerPlant, plantLayout, updatePlantButton);
		
		plantWrapper.add(plantGrid, formLayoutWrapper);
		secondLayout.add(plantWrapper);
		
		VerticalLayout finalMainWrapper = new VerticalLayout();
		HorizontalLayout mainAddPlantsWrapper = new HorizontalLayout();
		
		VerticalLayout stepOneWrapper = new VerticalLayout();
		stepOneWrapper.addClassNames(LumoUtility.Margin.NONE, 
				LumoUtility.Padding.NONE, LumoUtility.FlexDirection.COLUMN, LumoUtility.Display.FLEX, LumoUtility.AlignItems.START);
		
		HorizontalLayout innerWrapper = new HorizontalLayout();
		innerWrapper.addClassNames(LumoUtility.Margin.NONE, 
				LumoUtility.Padding.NONE);
		
		H3 stepOne = new H3("Stritt 1: Angaben zu den Pflanzen");
		stepOne.addClassName(LumoUtility.Margin.Left.SMALL);
		
		Icon icon = VaadinIcon.INFO_CIRCLE.create();
		Tooltip tooltip = Tooltip.forComponent(icon)
		        .withText("Dieser Schritt kann mehrmals wiederholt werden, um mehrere Konfigurationen einer Charge zuzuordnern.")
		        .withPosition(Tooltip.TooltipPosition.TOP_START);
		icon.setSize("16px");
		icon.addClassNames(LumoUtility.Margin.Top.SMALL);
		innerWrapper.add(stepOne, icon);
		stepOneWrapper.add(innerWrapper);
		stepOneWrapper.add(amountLayout);
		
		VerticalLayout stepTwoWrapper = new VerticalLayout();
		stepTwoWrapper.addClassNames(LumoUtility.Margin.NONE, 
				LumoUtility.Padding.NONE, LumoUtility.FlexDirection.COLUMN, LumoUtility.Display.FLEX, LumoUtility.AlignItems.START);
		
		H3 stepTwo = new H3("Schritt 2: Individuell anpassen");
		stepTwo.addClassName(LumoUtility.Margin.Left.SMALL);
		stepTwoWrapper.add(stepTwo);
		stepTwoWrapper.add(secondLayout);
		secondLayout.setHeight(450, Unit.PIXELS);
		
		mainAddPlantsWrapper.add(stepOneWrapper, stepTwoWrapper);
		
		H2 headerTitle = new H2("Pflanze(n) hinzufügen");
		headerTitle.addClassName(LumoUtility.Margin.Left.SMALL);
		
		finalMainWrapper.add(headerTitle, mainAddPlantsWrapper);
		
		return finalMainWrapper;
	}

	private void createChargeLayout(TabSheet tabSheet) {
		VerticalLayout wrapper = new VerticalLayout();
		wrapper.setClassName("grid-wrapper");
		wrapper.setHeightFull();
		
		HorizontalLayout horizontalLayout = new HorizontalLayout();		
		VerticalLayout layoutButton = new VerticalLayout();
		layoutButton.addClassNames(LumoUtility.Padding.Left.NONE);

		Button addChargeButton = new Button();
		addChargeButton.addClassNames("button-layout-common");
		addChargeButton.setText("Charge hinzufügen");
		addChargeButton.setIcon(VaadinIcon.CUBES.create());
		addChargeButton.addClickListener(e -> addChargeDialog.open());

		layoutButton.setAlignItems(Alignment.CENTER);
		layoutButton.add(addChargeButton);

		VerticalLayout layout = new VerticalLayout();
		layout.add(amountSeeds);
		layout.setAlignItems(Alignment.CENTER);

		horizontalLayout.add(layoutButton);
		horizontalLayout.add(layout);
		wrapper.add(horizontalLayout);
		
		chargeGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
		chargeGrid.addComponentHierarchyColumn(entity -> {
			Component avatar;
			if(entity.isCharge()) {
				avatar = VaadinIcon.CUBES.create();
			} else {
				avatar = LineAwesomeIcon.CANNABIS_SOLID.create();
			}
			avatar.getElement().setAttribute("tabindex", "-1");
						
			Span fullName = new Span("Name: " + entity.getName());
		    Span profession = new Span("Nummer: " + entity.getNummer());
		    profession.getStyle()
		            .set("color", "var(--lumo-secondary-text-color)")
		            .set("font-size", "var(--lumo-font-size-s)");
		    
			VerticalLayout column = new VerticalLayout(fullName, profession);
		    column.getStyle().set("line-height", "var(--lumo-line-height-m)");
		    column.setPadding(false);
		    column.setSpacing(false);
		    
			HorizontalLayout row = new HorizontalLayout(avatar, column);
		    row.setAlignItems(Alignment.CENTER);
		    row.setSpacing(true);
		    return row;
		});

		chargeGrid.addColumn(e -> renderDate(e.getErfasst())).setHeader("Gepflanzt am");		
		chargeGrid.addComponentColumn(entity -> {
			Span span = new Span(entity.getStatus() == null ? "" : entity.getStatus().getLabel());
			
			if (entity.getStatus() != null) {
				if (entity.getStatus() == GrowStatus.SPROUTING) {
					span.addClassName("span-sprout");
				} else if (entity.getStatus() == GrowStatus.NEW_PLANTED) {
					span.addClassName("span-new");
				} else if (entity.getStatus() == GrowStatus.GROWING) {
					span.addClassName("span-growing");
				} else if (entity.getStatus() == GrowStatus.READY) {
					span.addClassName("span-ready");
				} else {
					span.addClassName("span-harvested");
				}
			}
	        return span;
		});
		
		chargeGrid.addColumn(e -> e.getLocation() == null ? "" : e.getLocation().getName()).setHeader("Standort");
		
		chargeGrid.addComponentColumn(entity -> {
			MenuBar menuBar = new MenuBar();
			menuBar.setOverlayClassName("warenlager-view-menu-bar-1");
			menuBar.addClassName("warenlager-view-menu-bar-1");
			menuBar.setOverlayClassName("warenlager-view-menu-bar-1");
			menuBar.addClassName("warenlager-view-menu-bar-1");
			
			menuBar.addItem("Charge bearbeiten", event -> {
				setValuesInChargePopup(entity);
				addChargeDialog.open();
			});
			if (!entity.isCharge()) {
				menuBar.addItem("Pflanze bearbeiten", event -> {
					changeExistingPlant = entity;
					prepareSinglePlantDialog();
					editSinglePlantDialog.open();
				});
				menuBar.addItem("Status ändern", event -> {
					statusEntity = entity;
					prepareChangeStatusPopup(entity);
					changeStatusDialog.open();
				});
				if(entity.getStatus() == GrowStatus.READY || entity.getStatus() == GrowStatus.HARVESTED) {
					menuBar.addItem("Blüten ernten", event -> {
						convertEntity = entity;
						prepareNewBlossomStatusPopup(entity);
						addBlossomDialog.open();
					});
				}
				menuBar.addItem("Samen ernten", event -> {
					convertEntity = entity;
					setMotherPlantForSeed();
					addSeedsDialog.open();
				});

				menuBar.addItem("Klonen", event -> {
					convertEntity = entity;
					headerClonePlant.setText("Pflanze " + (convertEntity.getName() + "(" + convertEntity.getId() + ")")+ " klonen");
					clonePlantDialog.open();
				});

				menuBar.addItem("Stecklinge erfassen", event -> {
					convertEntity = entity;
					setMotherPlantForCuttings();
					addCuttingsDialog.open();
				});			
			} else {
				
				if(entity.hasElements()) {					
					menuBar.addItem("Status für alle ändern", event -> {
						statusEntity = entity;
						prepareChangeStatusPopup(entity);
						changeStatusDialog.open();
					});	
					
					menuBar.addItem("Pflanze(n) bearbeiten", event -> {
						statusEntity = entity;
						insertExistingValuesInAddPlantsDialog();
						addPlantsDialog.open();
					});
				} else {					
					menuBar.addItem("Pflanze(n) hinzufügen", event -> {
						statusEntity = entity;
						addPlantsDialog.open();
					});
				}
				
			}
			return menuBar;
		}).setWidth("100px").setFlexGrow(0);

		refreshGrid(ViewStatus.CHARGE);
		wrapper.add(chargeGrid);
		tabSheet.add("Pflanze(n)", wrapper);
	}
	
	private void setMotherPlantForCuttings() {
		Optional<Plant> optionalPlant = plantService.findAllByAssociation(associationId).stream().filter(e -> e.getId().equals(convertEntity.getId())).findAny();
		optionalPlant.ifPresent(e -> comboboxCuttingsOrigins.setValue(e));			
	}

	private void setMotherPlantForSeed() {
		Optional<Plant> optionalPlant = plantService.findAllByAssociation(associationId).stream().filter(e -> e.getId().equals(convertEntity.getId())).findAny();
		optionalPlant.ifPresent(e -> comboboxSeedsOrigins.setValue(e));		
	}

	private void createClonePlantDialog() {
		
		VerticalLayout cloneWrapper = new VerticalLayout();
		
		TextField renamePlantField = new TextField("Name der neuen Pflanze");
		renamePlantField.setWidthFull();
		ComboBox<Charge> chargeToCloneToBox = new ComboBox<Charge>("Klonen in Charge");
		chargeToCloneToBox.setWidthFull();
		chargeToCloneToBox.setItems(chargeService.findAllByAssociation(associationId));
		chargeToCloneToBox.setItemLabelGenerator(e -> e.getName());
		
		cloneWrapper.add(headerClonePlant, renamePlantField, chargeToCloneToBox);
		clonePlantDialog.add(cloneWrapper);
		
		Button cancelButton = new Button("zurück");
		cancelButton.addClassName("cancel-button");
		cancelButton.addClickListener(e -> {
			clonePlantDialog.close();
			renamePlantField.setValue("");
			chargeToCloneToBox.setValue(chargeToCloneToBox.getEmptyValue());
		});
		
		Button saveButton = new Button("klonen");
		saveButton.addClassName("save-button");
		saveButton.addClickListener(e -> {
			clonePlantDialog.close();
			
			if(!renamePlantField.isEmpty()) {
				if(!chargeToCloneToBox.isEmpty()) {
					Optional<Plant> optionalPlant = plantService.findAllByAssociation(associationId).stream().filter(p -> p.getId().equals(convertEntity.getId())).findAny();
					Plant tmpPlant = new Plant();
					tmpPlant.setAssociationId(associationId);
					tmpPlant.setDateOfExistense(LocalDate.now());
					tmpPlant.setGrowLocation(optionalPlant.isPresent() ? optionalPlant.get().getGrowLocation() : null);
					tmpPlant.setName(renamePlantField.getValue());
					tmpPlant.setStatus(GrowStatus.NEW_PLANTED);
					
					tmpPlant = plantService.update(tmpPlant);
					Charge chargeToCloneTo = chargeToCloneToBox.getValue();
					List<Plant> plants = chargeToCloneTo.getPlants();
					plants.add(tmpPlant);
					chargeToCloneTo.setPlants(plants);
					chargeService.update(chargeToCloneTo);
				} else {
					Notification.show("Es muss feststehen, in welche Charge die Pflanze integriert werden soll.");
				}
			} else {
				Notification.show("Die geklonte Pflanze braucht einen Namen");
			}
			
			refreshGrid(ViewStatus.CHARGE);
		});
		
		clonePlantDialog.getFooter().add(cancelButton, saveButton);
	}
	
	private void insertExistingValuesInAddPlantsDialog() {
		Optional<Charge> optionalCharge = chargeService.findAllByAssociation(associationId).stream().filter(e -> e.getId().equals(statusEntity.getId())).findAny();
		
		optionalCharge.ifPresentOrElse(e -> {
			this.tmpPlants = e.getPlants();	
			this.plantGrid.setItems(tmpPlants);
		}, () -> {
			Notification.show("Charge konnte nicht geladen werden.");
		});		
	}

	private void prepareNewBlossomStatusPopup(EntityWrapper entity) {
    	this.nameField.setValue("");
    	this.dateBlossomHarvested.setValue(LocalDate.now());
    	this.strainInfoAmount.setValue(0.0);
    	this.statusBox.setValue(GrowStatus.VERIFYING);
    	Optional<Plant> optionalBLossom = plantService.get(entity.getId());
    	optionalBLossom.ifPresent(e -> {    		
    		this.comboboxBlossomOrigins.setValue(e);
    	});
	}

	private void prepareSinglePlantDialog() {
		this.plantEditLocationBox.setValue(changeExistingPlant.getLocation());
		this.plantEditNameField.setValue(changeExistingPlant.getName());
		this.plantEditNumberField.setValue(changeExistingPlant.getNummer());
		this.plantEditStatusBox.setValue(changeExistingPlant.getStatus());
	}

	private void setValuesInChargePopup(EntityWrapper wrapper) { 
		
		setCustomSettingsBox.setEnabled(false);
		if(wrapper.isCharge()) {			
			this.chargeNameField.setValue(wrapper.getName());
			this.chargeNumberField.setValue(wrapper.getNummer());
			Optional<Charge> optionalCharge = chargeService.get(Long.valueOf(wrapper.getNummer()));
			optionalCharge.ifPresentOrElse(e -> {
				this.changeCharge = e;
				List<Plant> plants = e.getPlants();
				this.plantGrid.setItems(plants);
				tmpPlants = plants;
				this.chargeAmountPlantsField.setValue((double)plants.size());
			}, () -> {
				this.changeCharge = null;
			});
			
		} else {
			Optional<Plant> optionalPlant = plantService.get(Long.valueOf(wrapper.getNummer()));
			optionalPlant.ifPresentOrElse(e -> {
				Optional<Charge> chargeByPlant = chargeService.findChargeByPlant(associationId, e);
				if(chargeByPlant.isPresent()) {
					this.changeCharge = chargeByPlant.get();
					this.chargeNameField.setValue(chargeByPlant.get().getName());
					this.chargeNumberField.setValue(chargeByPlant.get().getNummer());
					this.chargeAmountPlantsField.setValue((double)chargeByPlant.get().getPlants().size());
					this.plantLocationBox.setValue(e.getGrowLocation());
					this.plantStatusBox.setValue(e.getStatus());
					this.plantGrid.setItems(chargeByPlant.get().getPlants());
					this.plantGrid.select(e);
					tmpPlants = chargeByPlant.get().getPlants();
				} else {
					this.changeCharge = null;
					this.chargeNameField.setValue("Es konnte keine Charge gefunden werden.");
					this.chargeNumberField.setValue("Es konnte keine Charge gefunden werden.");
					this.chargeAmountPlantsField.setValue((double)0);
				}
			}, () -> {
				this.changeCharge = null;
				this.chargeNameField.setValue("Es konnte keine Charge gefunden werden.");
				this.chargeNumberField.setValue("Es konnte keine Charge gefunden werden.");
				this.chargeAmountPlantsField.setValue((double)0);
			});
			this.plantNameField.setValue(wrapper.getName());
		}
	}
	
	private void prepareChangeStatusPopup(EntityWrapper entity) {
		List<GrowStatus> stati = new ArrayList<GrowStatus>(Arrays.asList(GrowStatus.values()));
		stati.removeIf(e -> e == GrowStatus.VERIFYING || e == GrowStatus.OUTPUT_READY || e == GrowStatus.DESTORYED);

		if (!entity.isCharge() && entity.getStatus() != null) {
			stati.removeIf(e -> (!(entity.getStatus().compareTo(e) < 0)));
			this.statBox.setItems(stati);
		} else {
			this.statBox.setItems(stati);
		}
	}

	private void createSeedsLayout(TabSheet tabSheet) {
		VerticalLayout wrapper = new VerticalLayout();
		wrapper.setClassName("grid-wrapper");
		wrapper.setHeightFull();	
		
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		VerticalLayout layoutButton = new VerticalLayout();
		layoutButton.addClassNames(LumoUtility.Padding.Left.NONE);

		Button addSeedsButton = new Button();
		addSeedsButton.addClassNames("button-layout-common");
		addSeedsButton.setText("+ Samen hinzufügen");

		addSeedsButton.addClickListener(e -> addSeedsDialog.open());

		layoutButton.setAlignItems(Alignment.CENTER);
		layoutButton.add(addSeedsButton);

		H2 balance = new H2("Samen:");

		VerticalLayout layout = new VerticalLayout();
		layout.add(balance);
		layout.add(amountSeeds);
		layout.setAlignItems(Alignment.CENTER);

		horizontalLayout.add(layoutButton);
		horizontalLayout.add(layout);
		wrapper.add(horizontalLayout);
		
		seedsGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
		seedsGrid.addColumn(p -> p.getSeedNumber()).setHeader("Nummer");
		seedsGrid.addColumn(p -> p.getName()).setHeader("Name").setAutoWidth(true).setSortable(true);
		seedsGrid.addColumn(p -> p.getResponsiblePerson().getFirstName() + " " + p.getResponsiblePerson().getLastName())
				.setHeader("Verantwortlicher").setAutoWidth(true).setSortable(true);
		seedsGrid
				.addColumn(p -> p.getMotherPlant() == null ? "-"
						: p.getMotherPlant().getName() + " (" + p.getMotherPlant().getNummer() + ")")
				.setHeader("Mutterpflanze").setAutoWidth(true).setSortable(true);
		seedsGrid.addColumn(p -> p.getAmountOfSeeds()).setHeader("Menge").setAutoWidth(true).setSortable(true);
		seedsGrid.addColumn(p -> p.getPrice()).setHeader("Preis").setAutoWidth(true).setSortable(true);

		seedsGrid.addComponentColumn(item ->  {
			Button button = new Button("Details");
			button.addClickListener(click -> {
				changeSeed = item;
				openSeedToEdit();
			});
			button.addClassNames("button-grid-green");
			return button;
		}).setAutoWidth(true);
		
		
		refreshGrid(ViewStatus.SEED);
		wrapper.add(seedsGrid);		
		tabSheet.add("Samen", wrapper);
	}
	
	private void openSeedToEdit() {
		
		this.seedNumberField.setValue(String.valueOf(changeSeed.getSeedNumber()));
		this.seedNameField.setValue(changeSeed.getName());
		this.seedPriceField.setValue(String.valueOf(changeSeed.getPrice()));
		this.seedsAmountField.setValue(String.valueOf(changeSeed.getAmountOfSeeds()));
		this.comboBoxResponsibleForSeed.setValue(changeSeed.getResponsiblePerson());
		this.comboBoxLocationSeed.setValue(changeSeed.getGrowLocation());
		this.comboboxSeedsOrigins.setValue(changeSeed.getMotherPlant() != null ? changeSeed.getMotherPlant() : comboboxSeedsOrigins.getEmptyValue());
		
		if(changeSeed.getMotherPlant() == null) {
			isFreshSeedBox.setValue(true);
			this.comboboxSeedsOrigins.setEnabled(false);
		} else {
			isFreshSeedBox.setValue(false);
			this.comboboxSeedsOrigins.setEnabled(true);
		}
		
		addSeedsDialog.open();
	}

	private void createAddSeedsDialog() {
		addSeedsDialog = new Dialog();
		addSeedsDialog.addClassNames(LumoUtility.MaxWidth.SCREEN_MEDIUM);
		
		VerticalLayout headerLayout = new VerticalLayout();
		HorizontalLayout headlineLayout = new HorizontalLayout();
		
		H2 header = new H2("Neue Samen hinzufügen");
		isFreshSeedBox = new Checkbox("Ohne eigene Mutterpflanze");
		isFreshSeedBox.addValueChangeListener(e -> {
			comboboxSeedsOrigins.setEnabled(!e.getValue());
		});
		
		isFreshSeedBox.addClassNames(LumoUtility.Padding.Top.XSMALL);
		headlineLayout.add(header, isFreshSeedBox);

		Hr hr = new Hr();		
		headerLayout.add(headlineLayout, hr);
		
		HorizontalLayout layout = new HorizontalLayout();
		layout.addClassNames(LumoUtility.Margin.Left.MEDIUM, LumoUtility.Margin.Right.MEDIUM);
		FormLayout formLayout = createSeedDialogComponent();
		
		layout.add(formLayout);
		addSeedsDialog.add(headerLayout);
		addSeedsDialog.add(layout);
		
		
		Button saveButton = new Button("Hinzufügen", e -> {

			if (!seedNameField.isEmpty()) {
				if (!comboboxSeedsOrigins.isEmpty() || isFreshSeedBox.getValue()) {
					if (!seedsAmountField.isEmpty()) {
						if (!seedPriceField.isEmpty()) {
							addNewSeeds();
							addSeedsDialog.close();
							clearSeedsDialog();
						} else {
							Notification.show("Was soll ein Samen kosten ?");
						}
					} else {
						Notification.show("Um wieviele Samen handelt es sich ?");
					}
				} else {
					Notification.show("Die Samen brauchen eine Mutterpflanze !");
				}
			} else {
				Notification.show("Die Stecklinge müssem einen Namen haben !");
			}

			deleteSeedButton.setEnabled(true);

		});
		saveButton.addClassName("save-button");
		
		Button cancelButton = new Button("Abbrechen", e -> {
			clearSeedsDialog();
			addSeedsDialog.close();	
			deleteSeedButton.setEnabled(true);
		});
		cancelButton.addClassNames("cancel-button");
		
		deleteSeedButton = new Button("Löschen", e -> {
			seedService.delete(changeSeed.getId());
			addSeedsDialog.close();
			Notification.show("Samen gelöscht.");
			deleteSeedButton.setEnabled(true);
			changeSeed = null;
			refreshGrid(ViewStatus.SEED);
		});
		
		addSeedsDialog.addDialogCloseActionListener(e -> {
			clearSeedsDialog();
		});
		
		deleteSeedButton.addClassNames("delete-button");
		
		addSeedsDialog.getFooter().add(deleteCuttingButton);
		addSeedsDialog.getFooter().add(cancelButton);
		addSeedsDialog.getFooter().add(saveButton);
	}

	private void addNewSeeds() {
		Seed seed;
    	
		if(changeSeed == null) {
			seed = new Seed();
		} else {
			seed = changeSeed;
		}
		
		seed.setAssociationId(associationId);
		seed.setAmountOfSeeds(Integer.valueOf(seedsAmountField.getValue()));
		seed.setSeedNumber(Integer.valueOf(seedNumberField.getValue()));
		seed.setMotherPlant(comboboxSeedsOrigins.getValue());
		seed.setName(seedNameField.getValue());		
		seed.setGrowLocation(comboBoxLocationSeed.getValue());
		seed.setPrice(Double.valueOf(seedPriceField.getValue()));
		seed.setResponsiblePerson(comboBoxResponsibleForSeed.getValue());
		
		seedService.update(seed);
		
		refreshGrid(ViewStatus.SEED);
		
	}

	private void clearSeedsDialog() {

		seedNumberField.setValue(String.valueOf(seedService.getFreeSeedNumber(associationId)));
		comboboxSeedsOrigins.setValue(comboboxSeedsOrigins.getEmptyValue());
		seedNameField.setValue("");
		seedsAmountField.setValue("");
		seedPriceField.setValue("");
		comboBoxResponsibleForSeed.setValue(comboBoxResponsibleForSeed.getEmptyValue());
		comboBoxLocationSeed.setValue(comboBoxLocationSeed.getEmptyValue());
		isFreshSeedBox.setValue(false);
		changeSeed = null;
	}

	private FormLayout createSeedDialogComponent() {
		FormLayout formLayout = new FormLayout();
		
		seedNumberField = new TextField("Eindeutige Nummer");
		seedNumberField.setEnabled(false);
		seedNumberField.setValue(String.valueOf(seedService.getFreeSeedNumber(associationId)));
		comboboxSeedsOrigins.setItems(plantService.findAllByAssociation(associationId));
		comboboxSeedsOrigins.setItemLabelGenerator(e -> e.getName() + " (" + e.getNummer() + ")");
		
		seedNameField = new TextField("Name");
		
		seedsAmountField = new TextField("Anzahl Samen");
		seedsAmountField.setAllowedCharPattern("[0-9/]");
		
		seedPriceField = new TextField("Preis pro Samen");
		seedPriceField.setAllowedCharPattern("[0-9/]");
		
		comboBoxResponsibleForSeed.setItems(personService.findAllByAssociation(associationId));
		comboBoxResponsibleForSeed.setItemLabelGenerator(e -> e.getFirstName() + " " + e.getLastName());
		comboBoxResponsibleForSeed.setValue(comboBoxResponsibleForSeed.isEmpty() ? comboBoxResponsibleForSeed.getEmptyValue() : comboBoxResponsibleForSeed.getListDataView().getItem(0));
		
		List<Location> allByAssociation = locationService.findAllByAssociation(associationId);
		comboBoxLocationSeed.setItems(allByAssociation);
		comboBoxLocationSeed.setItemLabelGenerator(e -> e.getName());
		comboBoxLocationSeed.setValue(allByAssociation.isEmpty() ? comboBoxLocationSeed.getEmptyValue() : comboBoxLocationSeed.getListDataView().getItem(0));
		
		formLayout.add(seedNumberField, seedNameField, comboboxSeedsOrigins,
				seedsAmountField, seedPriceField, comboBoxResponsibleForSeed, comboBoxLocationSeed);
		return formLayout;
	}

	private void createCuttingsLayout(TabSheet tabSheet) {
		VerticalLayout wrapper = new VerticalLayout();
		wrapper.setClassName("grid-wrapper");
		wrapper.setHeightFull();	
		HorizontalLayout horizontalLayout = new HorizontalLayout();

		VerticalLayout layoutButton = new VerticalLayout();
		layoutButton.addClassNames(LumoUtility.Padding.Left.NONE);

		Button addCuttingButton = new Button();
		addCuttingButton.addClassNames("button-layout-common");
		addCuttingButton.setText("+ Stecklinge hinzufügen");

		addCuttingButton.addClickListener(e -> {
			deleteCuttingButton.setEnabled(false);
			addCuttingsDialog.open();
		});

		layoutButton.setAlignItems(Alignment.CENTER);
		layoutButton.add(addCuttingButton);

		H2 balance = new H2("Stecklinge:");

		VerticalLayout layout = new VerticalLayout();
		layout.add(balance);
		layout.add(amountCuttings);
		layout.setAlignItems(Alignment.CENTER);

		horizontalLayout.add(layoutButton);
		horizontalLayout.add(layout);
		wrapper.add(horizontalLayout);
		
		cuttingsGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
		cuttingsGrid.addColumn(p -> p.getCuttingNumber()).setHeader("Nummer");
		cuttingsGrid.addColumn(p -> p.getName()).setHeader("Name").setAutoWidth(true).setSortable(true);
		cuttingsGrid.addColumn(p -> renderDate(p.getDatePlanted())).setHeader("Gepflanzt am").setAutoWidth(true)
				.setSortable(true);
		cuttingsGrid
				.addColumn(p -> p.getMotherPlant() == null ? "-"
						: p.getMotherPlant().getName() + " (" + p.getMotherPlant().getNummer() + ")")
				.setHeader("Mutterpflanze").setAutoWidth(true).setSortable(true);
		cuttingsGrid.addColumn(p -> p.getAmountOfCuttings()).setHeader("Menge").setAutoWidth(true).setSortable(true);
		cuttingsGrid.addColumn(p -> p.getStatus().getLabel()).setHeader("Status").setAutoWidth(true).setSortable(true);
		
		cuttingsGrid.addComponentColumn(item -> {
			Button button = new Button("Details");
			
			button.addClickListener(click -> {
				changeCutting = item;
				openCuttingToEdit();
			});
			
			button.addClassNames("button-grid-green");
			return button;
        }).setAutoWidth(true);
		
		refreshGrid(ViewStatus.CUTTING);
		
		wrapper.add(cuttingsGrid);

		tabSheet.add("Stecklinge", wrapper);
	}

	private void openCuttingToEdit() {
		
		this.cuttingNumberField.setValue(String.valueOf(changeCutting.getCuttingNumber()));
		this.cuttingNameField.setValue(changeCutting.getName());
		this.cuttingsPriceField.setValue(String.valueOf(changeCutting.getPrice()));
		this.cuttingPlantDate.setValue(changeCutting.getDatePlanted());
		this.cuttingsAmountField.setValue(String.valueOf(changeCutting.getAmountOfCuttings()));
		this.comboBoxResponsibleForCutting.setValue(changeCutting.getResponsiblePerson());
		this.comboBoxLocationCutting.setValue(changeCutting.getGrowLocation());
		this.comboxCuttingStatus.setValue(changeCutting.getStatus());
		this.comboboxCuttingsOrigins.setValue(changeCutting.getMotherPlant() != null ? changeCutting.getMotherPlant() : comboboxCuttingsOrigins.getEmptyValue());
		
		if(changeCutting.getMotherPlant() == null) {
			isFreshCuttingBox.setValue(true);
			this.comboboxCuttingsOrigins.setEnabled(false);
		} else {
			isFreshCuttingBox.setValue(false);
			this.comboboxCuttingsOrigins.setEnabled(true);
		}
		addCuttingsDialog.open();

	}

	private void createBlossomLayout(TabSheet tabSheet) {
		
		VerticalLayout wrapper = new VerticalLayout();
		wrapper.setClassName("grid-wrapper");

		wrapper.setHeightFull();	
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		
		VerticalLayout layoutButton = new VerticalLayout();
		layoutButton.addClassNames(LumoUtility.Padding.Left.NONE);

		Button addStrainButton = new Button();
		addStrainButton.addClassNames("button-layout-common");
		addStrainButton.setText("+ Blüten hinzufügen");
		
		addStrainButton.addClickListener(e -> {
			numberField.setValue(String.valueOf(blossomService.getFreeStrainNumber(associationId)));
			addBlossomDialog.open();
			deleteBlossomButton.setEnabled(false);
		});
		
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
		
		blossomGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
		blossomGrid.addColumn(p -> p.getStrainNumber()).setHeader("Nummer");
		blossomGrid.addColumn(p -> p.getName()).setHeader("Name").setAutoWidth(true).setSortable(true);
		blossomGrid.addColumn(p -> renderDate(p.getDateHarvested())).setHeader("Geerntet am").setAutoWidth(true).setSortable(true);
		blossomGrid.addColumn(p -> p.getAmountGramm() == 0 ? "-" : p.getAmountGramm() + " Gramm").setHeader("Vorhandene Menge").setAutoWidth(true).setSortable(true);
		
		blossomGrid.addComponentColumn(entity -> {
			Span span = new Span(entity.getStatus() == null ? "" : entity.getStatus().getLabel());
			
			if(entity.getStatus() != null) {	
				if (entity.getStatus() == GrowStatus.VERIFYING) {
					span.addClassName("span-verifying");
				} else {
					span.addClassName("span-output-ready");
				}
			}
	        return span;
		});
		
		blossomGrid.addComponentColumn(item -> {
			Icon icon = new Icon(VaadinIcon.PENCIL);
			icon.addClickListener(click -> {
				changeBlossom = item;
				openDialogForEdit(changeBlossom);
				addBlossomDialog.open();
			});
			icon.addClassNames("edit");
			return icon;
		}).setAutoWidth(true);
		
		refreshGrid(ViewStatus.STRAIN);
		
		wrapper.add(blossomGrid);
		
		tabSheet.add("Blüten", wrapper);
	}
	
	private void createChangeStatusDialog() {
		
		VerticalLayout layout = new VerticalLayout();
		H2 title = new H2("Status aktualisieren");
		Hr hr = new Hr();
		
		statBox.setItems(GrowStatus.values());
		statBox.setItemLabelGenerator(e -> e.getLabel());
		statBox.setWidthFull();
		
		Button saveStatusButton = new Button("Aktualisieren", e -> {		
			
			if(statusEntity.isCharge()) {
				Optional<Charge> optionalCharge = chargeService.get(statusEntity.getId());
				optionalCharge.ifPresentOrElse(c -> {
					c.getPlants().forEach(p -> {
						p.setStatus(statBox.getValue());
						plantService.update(p);
					});
				}, () -> {
					Notification.show("Unbekannte Charge.");
				});
			} else {
				Optional<Plant> optionalPlant = plantService.get(statusEntity.getId());
				
				optionalPlant.ifPresentOrElse(p -> {
					p.setStatus(statBox.getValue());
					plantService.update(p);
				}, () -> {
					Notification.show("Unbekannte Pflanze.");
				});
			}
			
			statusEntity = null;
			changeStatusDialog.close();
			refreshGrid(ViewStatus.CHARGE);
		});
		saveStatusButton.addClassName("save-button");
		
		Button cancelSaveStatusButton = new Button("Abbrechen", e -> {
			changeStatusDialog.close();
			statusEntity = null;
		});
		cancelSaveStatusButton.addClassName("cancel-button");
		layout.add(title, hr, statBox);
		
		changeStatusDialog.add(layout);
		changeStatusDialog.getFooter().add(cancelSaveStatusButton);
		changeStatusDialog.getFooter().add(saveStatusButton);
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
	
	private void openDialogForEdit(Blossom blossom) {
		
		
		if(blossom.getDateHarvested() != null) {
			statusBox.setValue(blossom.getStatus());
			
			responsiblePersonOne.setEnabled(true);
			List<Person> weighedByMembers = blossom.getWeighedByMembers();
			responsiblePersonOne.setValue(weighedByMembers.get(0));
			responsiblePersonTwo.setEnabled(true);
			responsiblePersonTwo.setValue(weighedByMembers.get(1));
			
			strainInfoAmount.setEnabled(true);
			strainInfoAmount.setValue(blossom.getAmountGramm());
			
			strainInfoThc.setEnabled(true);
			strainInfoThc.setValue(blossom.getThc());
			
			amountPerGramm.setEnabled(true);
			amountPerGramm.setAmount(String.valueOf(blossom.getPrice()));
			dateBlossomHarvested.setValue(blossom.getDateHarvested());
		} else {
			Notification.show("Old logic kickin");
		}
		
		numberField.setValue(String.valueOf(blossom.getStrainNumber()));
		nameField.setValue(blossom.getName());
		locationBox.setValue(blossom.getGrowLocation());
	}

	private void createAddBlossomDialog() {
		
		addBlossomDialog = new Dialog();
		addBlossomDialog.addClassNames(LumoUtility.MaxWidth.SCREEN_MEDIUM);
		VerticalLayout headerLayout = new VerticalLayout();
		HorizontalLayout headlineLayout = new HorizontalLayout();
		
		H2 header = new H2("Neue Blüten hinzufügen");
		headlineLayout.add(header);
		
		Hr hr = new Hr();		
		headerLayout.add(headlineLayout, hr);
		
		HorizontalLayout layout = new HorizontalLayout();
		layout.addClassNames(LumoUtility.Margin.Left.MEDIUM, LumoUtility.Margin.Right.MEDIUM);
		FormLayout formLayout = createFirstComponent();
		
		layout.add(formLayout);
		addBlossomDialog.add(headerLayout);
		addBlossomDialog.add(layout);
		
		Button saveButton = new Button("Hinzufügen", e -> {

			if (!nameField.isEmpty()) {
				preparePath();
				addNewBlossom(nameField.getValue(), dateBlossomHarvested.getValue(), strainInfoAmount, strainInfoThc, statusBox);
				addBlossomDialog.close();
				clearBlossomDialog();
			} else { 
				Notification.show("Die Sorte muss einen Namen haben !");
			}
			
			deleteBlossomButton.setEnabled(true);
		});
		saveButton.addClassName("save-button");
		
		Button cancelButton = new Button("Abbrechen", e -> {
			addBlossomDialog.close();	
			clearBlossomDialog();
			deleteBlossomButton.setEnabled(true);
		});
		cancelButton.addClassNames("cancel-button");
		
		deleteBlossomButton = new Button("Löschen", e -> {
			blossomService.delete(changeBlossom.getId());
			addBlossomDialog.close();
			Notification.show("Sorte gelöscht.");
			deleteBlossomButton.setEnabled(true);

		});
		
		addBlossomDialog.addDialogCloseActionListener(e -> {
			clearBlossomDialog();
		});
		
		deleteBlossomButton.addClassNames("delete-button");
		
		addBlossomDialog.getFooter().add(deleteBlossomButton);
		addBlossomDialog.getFooter().add(cancelButton);
		addBlossomDialog.getFooter().add(saveButton);
	}
	
	private void createAddCuttingsDialog() {
		addCuttingsDialog = new Dialog();
		addCuttingsDialog.addClassNames(LumoUtility.MaxWidth.SCREEN_MEDIUM);
		
		VerticalLayout headerLayout = new VerticalLayout();
		HorizontalLayout headlineLayout = new HorizontalLayout();
		
		H2 header = new H2("Neue Stecklinge");
		isFreshCuttingBox = new Checkbox("Ohne eigene Mutterpflanze");
		isFreshCuttingBox.addValueChangeListener(e -> {
			comboboxCuttingsOrigins.setEnabled(!e.getValue());
		});
		
		isFreshCuttingBox.addClassNames(LumoUtility.Padding.Top.XSMALL);
		headlineLayout.add(header, isFreshCuttingBox);

		Hr hr = new Hr();		
		headerLayout.add(headlineLayout, hr);
		
		HorizontalLayout layout = new HorizontalLayout();
		layout.addClassNames(LumoUtility.Margin.Left.MEDIUM, LumoUtility.Margin.Right.MEDIUM);
		FormLayout formLayout = createCuttingsDialogComponent();
		
		layout.add(formLayout);
		addCuttingsDialog.add(headerLayout);
		addCuttingsDialog.add(layout);
		
		
		Button saveButton = new Button("Hinzufügen", e -> {

			if (!cuttingNameField.isEmpty()) {
				if (!cuttingPlantDate.isEmpty()) {
					if (!comboboxCuttingsOrigins.isEmpty() || isFreshCuttingBox.getValue()) {
						if(!cuttingsAmountField.isEmpty()) {
							if(!cuttingsPriceField.isEmpty()) {								
								addNewCuttings();
								addCuttingsDialog.close();
								clearCuttingsDialog();
							} else {
								Notification.show("Was soll ein Steckling kosten ?");
							}
						} else {
							Notification.show("Um wieviele Stecklinge handelt es sich ?");
						}
					} else {
						Notification.show("Die Stecklinge brauchen eine Mutterpflanze !");
					}
				} else {
					Notification.show("Es muss feststehen wann die Stecklinge gepflanzt wurden !");
				}
			} else {
				Notification.show("Die Stecklinge müssem einen Namen haben !");
			}

			deleteCuttingButton.setEnabled(true);

		});
		saveButton.addClassName("save-button");
		
		Button cancelButton = new Button("Abbrechen", e -> {
			clearCuttingsDialog();
			addCuttingsDialog.close();	
			deleteCuttingButton.setEnabled(true);
		});
		cancelButton.addClassNames("cancel-button");
		
		deleteCuttingButton = new Button("Löschen", e -> {
			cuttingService.delete(changeCutting.getId());
			addCuttingsDialog.close();
			Notification.show("Steckling gelöscht.");
			deleteCuttingButton.setEnabled(true);
			changeCutting = null;
			refreshGrid(ViewStatus.CUTTING);
		});
		
		addCuttingsDialog.addDialogCloseActionListener(e -> {
			clearBlossomDialog();
		});
		
		deleteCuttingButton.addClassNames("delete-button");
		
		addCuttingsDialog.getFooter().add(deleteCuttingButton);
		addCuttingsDialog.getFooter().add(cancelButton);
		addCuttingsDialog.getFooter().add(saveButton);
	}
 	
	private void clearCuttingsDialog() {
		
		this.cuttingNumberField.setValue(String.valueOf(cuttingService.getFreeCuttingNumber(associationId)));
		this.cuttingNameField.setValue("");
		this.cuttingsPriceField.setValue("");
		this.cuttingPlantDate.setValue(LocalDate.now());
		this.cuttingsAmountField.setValue("");
		this.comboBoxResponsibleForCutting.setValue(comboBoxResponsibleForCutting.isEmpty() ? comboBoxResponsibleForCutting.getEmptyValue() : comboBoxResponsibleForCutting.getListDataView().getItem(0));
		this.comboBoxLocationCutting.setValue(comboBoxLocationCutting.getListDataView().getItemCount() == 0 ? comboBoxLocationCutting.getEmptyValue() : comboBoxLocationCutting.getListDataView().getItem(0));
		this.comboxCuttingStatus.setValue(comboxCuttingStatus.getListDataView().getItem(0));
		this.comboboxCuttingsOrigins.setValue(comboboxCuttingsOrigins.getEmptyValue());
		
		isFreshCuttingBox.setValue(false);
		this.comboboxCuttingsOrigins.setEnabled(true);
	}

	private void addNewCuttings() {
		
		Cutting cutting;
    	
		if(changeCutting == null) {
			cutting = new Cutting();
		} else {
			cutting = changeCutting;
		}
		
		cutting.setAssociationId(associationId);
		cutting.setAmountOfCuttings(Integer.valueOf(cuttingsAmountField.getValue()));
		cutting.setCuttingNumber(Integer.valueOf(cuttingNumberField.getValue()));
		cutting.setDatePlanted(cuttingPlantDate.getValue());
		cutting.setMotherPlant(comboboxCuttingsOrigins.getValue());
		cutting.setName(cuttingNameField.getValue());
		cutting.setStatus(comboxCuttingStatus.getValue());
		cutting.setGrowLocation(comboBoxLocationCutting.getValue());
		cutting.setPrice(Double.valueOf(cuttingsPriceField.getValue()));
		cutting.setResponsiblePerson(comboBoxResponsibleForCutting.getValue());
		
		cuttingService.update(cutting);
		
		refreshGrid(ViewStatus.CUTTING);
	}

	private FormLayout createCuttingsDialogComponent() {
		FormLayout formLayout = new FormLayout();
		
		cuttingNumberField = new TextField("Eindeutige Nummer");
		cuttingNumberField.setEnabled(false);
		cuttingNumberField.setValue(String.valueOf(cuttingService.getFreeCuttingNumber(associationId)));
		comboboxCuttingsOrigins.setItems(plantService.findAllByAssociation(associationId));
		comboboxCuttingsOrigins.setItemLabelGenerator(e -> e.getName() + " (" + e.getNummer() + ")");
		
		cuttingNameField = new TextField("Name");
		cuttingPlantDate = new DatePicker("Datum des Pflanzens");
		comboxCuttingStatus.setItems(Arrays.asList(GrowStatus.values()).stream()
				.filter(e -> (e == GrowStatus.NEW_PLANTED || (e == GrowStatus.GROWING || e == GrowStatus.READY))).toList());
		comboxCuttingStatus.setItemLabelGenerator(e -> e.getLabel());
		comboxCuttingStatus.setValue(GrowStatus.NEW_PLANTED);
		
		cuttingsAmountField = new TextField("Anzahl Stecklinge");
		cuttingsAmountField.setAllowedCharPattern("[0-9/]");
		
		cuttingsPriceField = new TextField("Preis pro Steckling");
		cuttingsPriceField.setAllowedCharPattern("[0-9/]");
		
		comboBoxResponsibleForCutting.setItems(personService.findAllByAssociation(associationId));
		comboBoxResponsibleForCutting.setItemLabelGenerator(e -> e.getFirstName() + " " + e.getLastName());
		comboBoxResponsibleForCutting.setValue(comboBoxResponsibleForCutting.isEmpty() ? comboBoxResponsibleForCutting.getEmptyValue() : comboBoxResponsibleForCutting.getListDataView().getItem(0));
		
		List<Location> allByAssociation = locationService.findAllByAssociation(associationId);
		comboBoxLocationCutting.setItems(allByAssociation);
		comboBoxLocationCutting.setItemLabelGenerator(e -> e.getName());
		comboBoxLocationCutting.setValue(allByAssociation.isEmpty() ? comboBoxLocationCutting.getEmptyValue() : comboBoxLocationCutting.getListDataView().getItem(0));
		
		formLayout.add(cuttingNumberField, cuttingNameField, comboboxCuttingsOrigins, cuttingPlantDate,
				comboxCuttingStatus, cuttingsAmountField, cuttingsPriceField, comboBoxResponsibleForCutting, comboBoxLocationCutting);
		return formLayout;
	}

	private void clearBlossomDialog() {
		dateBlossomHarvested.setValue(LocalDate.now());
		statusBox.setValue(statusBox.getEmptyValue());
		responsiblePersonOne.setValue(responsiblePersonOne.getEmptyValue());
		responsiblePersonTwo.setValue(responsiblePersonTwo.getEmptyValue());
		strainInfoAmount.setValue(0.0);
		strainInfoThc.setValue(0.0);
		amountPerGramm.clear();
		CurrencyUnit eur = Monetary.getCurrency("EUR");
	    MonetaryAmount fstAmtEUR = Monetary.getDefaultAmountFactory()
	      .setCurrency(eur).setNumber(0.0).create();
		amountPerGramm.setValue(fstAmtEUR);
		numberField.setValue("");
		nameField.setValue("");
		locationBox.setValue(locationBox.getEmptyValue());
		
		if(changeBlossom != null) {
			changeBlossom = null;
		}
	}

	private Component createUploadComponent() {
		uploadCertificate = new Upload();
		FileBuffer buffer = new FileBuffer();
		uploadCertificate.setReceiver(buffer);
		uploadCertificate.setAcceptedFileTypes(".pdf");
//		uploadCertificate.setMaxFileSize(16000);
		uploadCertificate.setDropAllowed(true);
		uploadCertificate.setMaxFiles(1);

		UploadI18N i18n = new UploadI18N();
        i18n.setDropFiles(new UploadI18N.DropFiles().setOne("Datei hierhin ziehen...").setMany("Dateien hierhin ziehen..."));
        i18n.setAddFiles(new UploadI18N.AddFiles().setOne("Zertifikat auswählen").setMany("Zertifikate auswählen"));
        i18n.setError(new UploadI18N.Error().setTooManyFiles("Zu viele Dateien.").setFileIsTooBig("Datei ist zu groß."));
        i18n.setUploading(new UploadI18N.Uploading().setStatus(new UploadI18N.Uploading.Status().setConnecting("Verbinden...").setStalled("Stillstand.").setProcessing("Verarbeiten der Datei..."))
                        .setRemainingTime(new UploadI18N.Uploading.RemainingTime().setPrefix("verbleibende Zeit: ").setUnknown("unbekannte verbleibende Zeit"))
                        .setError(new UploadI18N.Uploading.Error().setServerUnavailable("Server nicht verfügbar").setUnexpectedServerError("Unerwarteter Serverfehler").setForbidden("Verboten")));

        uploadCertificate.setI18n(i18n);
        
        uploadCertificate.addSucceededListener(event -> {
        	preparePath();
            streamCertificate = buffer.getInputStream();
            pathToCertificate = new File(directoryPath, event.getFileName());
        });
        
        Button uploadButton = (Button) uploadCertificate.getUploadButton();
        uploadButton.setText("Zertifikat auswählen");
		uploadButton.setEnabled(false);
		uploadCertificate.setVisible(false);
		
		return uploadCertificate;
	}
	
	private void handleFile() {

	    File targetFile = pathToCertificate;
	    try {
	        if (!targetFile.exists()) {
	            targetFile.createNewFile();
	        }
	        try (FileOutputStream out = new FileOutputStream(targetFile)) {
	        	// 16 KB buffer
	            byte[] buffer = new byte[16384];
	            int bytesRead;
	            while ((bytesRead = streamCertificate.read(buffer)) != -1) {
	                out.write(buffer, 0, bytesRead);
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	        Notification.show("Fehler beim Speichern der Datei");
	    }
	    
	    streamCertificate = null;
	}
	
	private void preparePath() {
		final Properties properties = new Properties();
		try (InputStream input = new FileInputStream(new File("/application.properties"))) {

			// Load the properties file
			properties.load(input);
		} catch (IOException ex) {
			try (InputStream input = WarenlagerView.class.getClassLoader().getResourceAsStream("application.properties")) {
				if (input == null) {
					System.out.println("Sorry, unable to find application.properties");
					System.exit(1);
				}

				// Load the properties file
				properties.load(input);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		directoryPath = properties.getProperty("certificate.upload.path") + File.separator +  associationId + File.separator + "strains" + File.separator + numberField.getValue() + File.separator + "certificates" + File.separator;
	    Path path = Paths.get(directoryPath);

	    // Überprüfe, ob das Verzeichnis existiert
	    if (!Files.exists(path)) {
	        try {
	            // Erstelle das Verzeichnis, falls es nicht existiert
	            Files.createDirectories(path);
	        } catch (IOException e) {
	            e.printStackTrace();
	            Notification.show("Fehler beim Erstellen des Verzeichnisses");
	            return; // Beende die Methode, falls das Verzeichnis nicht erstellt werden kann
	        }
	    }
	}

	private FormLayout createFirstComponent() {
		
		FormLayout formLayout = new FormLayout();
		
		numberField = new TextField("Nummer");
		numberField.setValue(String.valueOf(blossomService.getFreeStrainNumber(associationId)));
		numberField.setEnabled(false);
		
		nameField = new TextField("Name");
		statusBox = new ComboBox<GrowStatus>("Status");
		
		statusBox.setItems(Arrays.asList(GrowStatus.HARVESTED, GrowStatus.VERIFYING));
		statusBox.setItemLabelGenerator(e -> e.getLabel());
		statusBox.setValue(GrowStatus.HARVESTED);
		
		statusBox.addValueChangeListener(e -> {
			
//			if(e.getValue() == GrowStatus.VERIFYING || e.getValue() == GrowStatus.OUTPUT_READY) {					
//				uploadCertificate.setDropAllowed(true);
//				Button uploadButton = (Button) uploadCertificate.getUploadButton();
//				uploadButton.setEnabled(true);
//				uploadCertificate.setVisible(true);

//			} else {
//				uploadCertificate.setDropAllowed(false);
//				Button uploadButton = (Button) uploadCertificate.getUploadButton();
//				uploadButton.setEnabled(false);
//				uploadCertificate.setVisible(false);
//			}
		});
		
		locationBox = new ComboBox<>("Standort");
		List<Location> allByAssociation = locationService.findAllByAssociation(associationId);
		locationBox.setItems(allByAssociation);
		locationBox.setValue(allByAssociation.isEmpty() ? locationBox.getEmptyValue() : locationBox.getListDataView().getItem(0));
		locationBox.setItemLabelGenerator(e -> e.getName());

		dateBlossomHarvested = new DatePicker();
		dateBlossomHarvested.setLabel("Geerntet am");
		dateBlossomHarvested.setValue(LocalDate.now());
		dateBlossomHarvested.setWidthFull();

		List<Person> allMembers = personService.findAllByAssociation(associationId);
		responsiblePersonOne = new ComboBox<>("Gewogen durch");
		responsiblePersonOne.setItems(allMembers);
		responsiblePersonOne.setItemLabelGenerator(e -> e.getFirstName() + " " + e.getLastName());
		
		responsiblePersonTwo = new ComboBox<>("Gewogen durch");
		responsiblePersonTwo.setItems(allMembers);
		responsiblePersonTwo.setItemLabelGenerator(e -> e.getFirstName() + " " + e.getLastName());
		
		strainInfoAmount = new NumberField("Menge in Gramm");
		
		strainInfoThc = new NumberField("THC Gehalt in Prozent");		
		strainInfoThc.addClassName("warenlager-view-number-field-1");

		amountPerGramm = new MoneyField();
		amountPerGramm.setLabel("Preis pro Gramm");
		amountPerGramm.setCurrency("EUR");
		
		comboboxBlossomOrigins.setItems(plantService.findAllByAssociation(associationId));
		comboboxBlossomOrigins.setItemLabelGenerator(e -> e.getName());
		
		formLayout.add(numberField, nameField, locationBox, statusBox, dateBlossomHarvested, responsiblePersonOne,
				responsiblePersonTwo, strainInfoAmount, strainInfoThc, amountPerGramm, comboboxBlossomOrigins);
		return formLayout;
	}

	private void addNewBlossom(String name, LocalDate dateHarvested, NumberField strainInfoAmount, NumberField strainInfoThc, ComboBox<GrowStatus> statusBox) {
		
		Blossom newBlossom;
		if (changeBlossom != null) {
			newBlossom = changeBlossom;
		} else {			
			newBlossom = new Blossom();
		}
		
		newBlossom.setStrainNumber(Integer.valueOf(numberField.getValue()));
		newBlossom.setName(name);
		newBlossom.setAssociationId(associationId);
		newBlossom.setStatus(statusBox.getValue());
		newBlossom.setGrowLocation(locationBox.getValue());
		newBlossom.setDateHarvested(dateHarvested);
		newBlossom.setAmountGramm(strainInfoAmount.getValue());
		newBlossom.setThc(strainInfoThc.getValue());
		newBlossom.setWeighedByMembers(Arrays.asList(responsiblePersonOne.getValue(), responsiblePersonTwo.getValue()));
		
		if(convertEntity != null) {
			Optional<Plant> optionalPlant = plantService.get(convertEntity.getId());
			optionalPlant.ifPresentOrElse(e -> {
				newBlossom.setMotherPlant(e);
			}, () -> {});
			
		}
		
		if (amountPerGramm.getValue() != null) {
			newBlossom.setPrice(amountPerGramm.getValue().getNumber().doubleValue());
		}

		if (statusBox.getValue().ordinal() > 3 && pathToCertificate != null) {
			handleFile();
			newBlossom.setPathOfCertificate(pathToCertificate.getAbsolutePath());
			pathToCertificate = null;
		}
		
		blossomService.update(newBlossom);
		
		if (convertEntity != null) {
			if (convertEntity.getStatus() == GrowStatus.READY) {
				Optional<Plant> optionalPlant = plantService.get(convertEntity.getId());
				optionalPlant.ifPresentOrElse(e -> {
					e.setStatus(GrowStatus.HARVESTED);
					plantService.update(e);
					Notification show = Notification.show("Neue Blüten hinzugefügt. Der Status der Pflanze wurde aktualisiert.");
					show.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
					refreshGrid(ViewStatus.CHARGE);
				}, () -> {
				});
			}
			convertEntity = null;
		}
		
		refreshGrid(ViewStatus.STRAIN);
	}
	
	private void refreshGrid(ViewStatus status) {
		
		if(status == ViewStatus.STRAIN) {
		allStrainsByAssociation = blossomService.findAllByAssociation(associationId);
		
		if(outputAssociation.isEmpty()) {
			outputAssociation = outputService.findAllByAssociation(associationId);
		}
		
		this.blossomGrid.setItems(allStrainsByAssociation);
		this.outputGrid.setItems(outputAssociation.stream().filter(e -> !e.isOutdated()).toList());
		cuttingsGrid.setItems(cuttingService.findAllByAssociation(associationId));
		double generalAmount = 0;
		for(Blossom strain : allStrainsByAssociation) {
			generalAmount = generalAmount + strain.getAmountGramm();
		}
		
		this.amount.setText(String.valueOf(generalAmount) + " Gramm");
		
		} else if (status == ViewStatus.CUTTING) {
			double cuttingsAmount = 0;
			List<Cutting> allCuttingsByAssociation = cuttingService.findAllByAssociation(associationId);
			for(Cutting cutting :  allCuttingsByAssociation) {
				cuttingsAmount = cuttingsAmount + cutting.getAmountOfCuttings();
			}
			this.amountCuttings.setText(String.valueOf((int)cuttingsAmount) + " Stück");
			this.cuttingsGrid.setItems(allCuttingsByAssociation);
			
		} else if(status == ViewStatus.SEED) {
			double seedsAmount = 0;
			List<Seed> allSeedsByAssociation = seedService.findAllByAssociation(associationId);
			for(Seed seed :  allSeedsByAssociation) {
				seedsAmount = seedsAmount + seed.getAmountOfSeeds();
			}
			this.amountSeeds.setText(String.valueOf((int)seedsAmount) + " Stück");
			this.seedsGrid.setItems(allSeedsByAssociation);
			
		} else {			
	        this.chargeGrid.setTreeData(builtTreeData());
		}
	}

	private TreeData<EntityWrapper> builtTreeData() {

		TreeData<EntityWrapper> treeData = new TreeData<>();
		List<Charge> allByAssociation = chargeService.findAllByAssociation(associationId);
		
		allByAssociation.forEach(e -> {
			treeData.addItem(null, e);
			e.getPlants().forEach(p -> {				
				treeData.addItem(e, p);
			});
		});
		
		return treeData;
	}
 
}
