package com.css.one.views.warenlager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;

import org.vaadin.addons.MoneyField;

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
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
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
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.PermitAll;

@PageTitle("Bestand")
@Route(value = "waren", layout = MainLayout.class)
@PermitAll
public class WarenlagerView extends Div {
	
    private static final long serialVersionUID = 5652277988730640569L;
    private OutputService outputService;
    private PersonService personService;
    private BlossomService strainService;
    private LocationService locationService;
    private CuttingService cuttingService;	
    private SeedService seedService;
    private ChargeService chargeService;
    private PlantService plantService;
    
    private int associationId;
    
    Dialog addStrainDialog;
    Dialog addOutputDialog;
    
	Grid<Blossom> strainGrid = new Grid<Blossom>();
	Grid<Output> outputGrid = new Grid<Output>();
	Grid<Cutting> cuttingsGrid = new Grid<Cutting>();
	Grid<Seed> seedsGrid = new Grid<Seed>();
	Grid<Plant> plantGrid = new Grid<Plant>();

	TreeGrid<EntityWrapper> chargeGrid = new TreeGrid<>();

	ComboBox<GrowStatus> statusBox = new ComboBox<GrowStatus>("Status");
	Checkbox box;
	Checkbox isFreshCuttingBox;
	Checkbox isFreshSeedBox;
	Checkbox singlePlantBox;
	Checkbox setCustomSettingsBox = new Checkbox("Global");

	DateTimePicker dateHarvested;
	ComboBox<Location> locationBox;
	
	ComboBox<Person> responsiblePersonOne;
	ComboBox<Person> responsiblePersonTwo;
	ComboBox<Plant> comboboxCuttingsOrigins = new ComboBox<Plant>("Mutterpflanze");
	ComboBox<Plant> comboboxSeedsOrigins = new ComboBox<Plant>("Mutterpflanze");
	ComboBox<Person> comboBoxResponsibleForSeed = new ComboBox<Person>("Verantwortlicher");

	ComboBox<GrowStatus> comboxCuttingStatus = new ComboBox<GrowStatus>("Status");
	ComboBox<Person> comboBoxResponsibleForCutting = new ComboBox<Person>("Verantwortlicher");
	ComboBox<Location> comboBoxLocationCutting = new ComboBox<Location>("Ort");
	ComboBox<Location> comboBoxLocationSeed = new ComboBox<Location>("Lagerort");

	H2 amount = new H2("0 Gramm");
	H2 amountCuttings = new H2("0 Stück");
	H2 amountSeeds = new H2("0 Stück");
	
	List<Blossom> allStrainsByAssociation = new ArrayList<>();
    List<Output> outputAssociation = new ArrayList<>();
	List<Plant> tmpPlants = new ArrayList<>();

    Dialog changeStrainStatusDialog = new Dialog();
    Dialog addCuttingsDialog = new Dialog();
    Dialog addSeedsDialog = new Dialog();
    Dialog addChargeDialog = new Dialog();
    
    Blossom changeStrain;
    Cutting changeCutting;
    Seed changeSeed;
    Charge changeCharge;
    Plant editPlant;
    
	Button updatePlantButton = new Button("update");
	Button saveChargeButton;
	
    File pathToCertificate;
    InputStream streamCertificate;
    String directoryPath;
    
    private Upload uploadCertificate;
    
    private TextField nameField;
    private DateTimePicker date;
    private DateTimePicker dateAvailable;
    private NumberField strainInfoThc;
    private NumberField strainInfoAmount;
    private TextField numberField;
    private TextField amountOfPlantsField;
    private MoneyField amountPerGramm;
    
	private TextField chargeNameField = new TextField("Name");
	private DatePicker chargeRegDate = new DatePicker("Erfassen am");
	private NumberField chargeAmountPlantsField = new NumberField("Anzahl Pflanzen");		
	private TextField chargeNumberField = new TextField("Nummer");
	private TextField plantNameField = new TextField("Name");
    private ComboBox<Location> plantLocationBox = new ComboBox<Location>("Standort");
    private ComboBox<GrowStatus> plantStatusBox = new ComboBox<GrowStatus>("Status");
    
    private TextField cuttingNumberField;
    private TextField cuttingNameField;
    private DateTimePicker cuttingPlantDate;
    private TextField cuttingsAmountField;
    private TextField cuttingsPriceField;
    
    private TextField seedNameField;
    private TextField seedNumberField;
    private TextField seedPriceField;
    private TextField seedsAmountField;
    
    private Button deleteStrainButton;
    private Button deleteCuttingButton;
    private Button deleteSeedButton;
    
    private Button deleteChargeButton;
    
    public enum ViewStatus {
		CHARGE, STRAIN, CUTTING, SEED
	}
    
	public WarenlagerView(BlossomService strainService, OutputService outputService, PersonService personService, LocationService locationService,
			CuttingService cuttingService, SeedService seedService, ChargeService chargeService, PlantService plantService) {
		this.strainService = strainService;
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
        
        TabSheet tabSheet = new TabSheet();
        tabSheet.addClassNames(LumoUtility.Margin.NONE);
        tabSheet.setSizeFull();
        
        createChargeLayout(tabSheet);
        createStrainsLayout(tabSheet);
        createCuttingsLayout(tabSheet);
        createSeedsLayout(tabSheet);
        
        createAddStrainDialog();
        createAddCuttingsDialog();
        createAddSeedsDialog();
        createAddChargeDialog();
        
        add(tabSheet);
    }

	private void createAddChargeDialog() {
		VerticalLayout wrapper = new VerticalLayout();
		addChargeDialog.addClassNames(LumoUtility.MaxWidth.SCREEN_XXLARGE);
		
		VerticalLayout headerLayout = new VerticalLayout();
		HorizontalLayout headlineLayout = new HorizontalLayout();
		
		H2 header = new H2("Neue Pflanze(n) hinzufügen");
		
		headlineLayout.add(header);

		Hr hr = new Hr();		
		headerLayout.add(headlineLayout, hr);
		
		HorizontalLayout layout = new HorizontalLayout();
		layout.addClassNames(LumoUtility.Margin.Left.MEDIUM, LumoUtility.Margin.Right.MEDIUM);
		
		layout.add(createChargeDialogComponent());
		wrapper.add(headerLayout, layout);
		
		saveChargeButton = new Button("Hinzufügen", e -> {

			if (!chargeNameField.isEmpty()) {
				if (!chargeNumberField.isEmpty()) {
					if (!chargeAmountPlantsField.isEmpty()) {
						if (!tmpPlants.isEmpty()) {
							addNewCharge();
							addChargeDialog.close();
							refreshGrid(ViewStatus.CHARGE);
							clearChargeDialog();
						} else {
							Notification.show("Eine Charge muss mindestens eine Pflanze beinhalten!");
						}
					} else {
						Notification.show("Wieviele Pflanzen hat die Charge ?");
					}
				} else {
					Notification.show("Jede Charge braucht eine Nummer !");
				}
			} else {
				Notification.show("Jede Charge braucht einen Namen !");
			}

		});
		saveChargeButton.addClassNames("save-button");
		saveChargeButton.setEnabled(false);
		
		Button cancelButton = new Button("Abbrechen", e -> {
			clearChargeDialog();
			addChargeDialog.close();	
			deleteChargeButton.setEnabled(true);
		});
		cancelButton.addClassNames("cancel-button");
		
		deleteChargeButton = new Button("Löschen", e -> {
			//chargeService.delete(e);
			addChargeDialog.close();
			Notification.show("Pflanze(n) gelöscht.");
//			deleteSeedButton.setEnabled(true);
//			changeSeed = null;
//			refreshGrid(ViewStatus.SEED);
		});
		
		addChargeDialog.addDialogCloseActionListener(e -> {
			clearChargeDialog();
		});
		
		deleteChargeButton.addClassNames("delete-button");
		
		addChargeDialog.getFooter().add(deleteCuttingButton);
		addChargeDialog.getFooter().add(cancelButton);
		addChargeDialog.getFooter().add(saveChargeButton);
	
		addChargeDialog.add(wrapper);
	}

	private void addNewCharge() {

		List<Plant> includedPlants = new ArrayList<>();
		
		Charge charge;
		
		if(changeCharge != null) {
			charge = changeCharge;
			tmpPlants.forEach(e -> {
				includedPlants.add(plantService.update(e));
			});
		} else {
			charge = new Charge();	
			tmpPlants.forEach(e -> {
				e.setId(0L);
				includedPlants.add(plantService.update(e));
			});
		}
		
		charge.setAssociationId(associationId);
		charge.setDateOfExistense(chargeRegDate.getValue() != chargeRegDate.getEmptyValue() ? chargeRegDate.getValue() : LocalDate.now());
		charge.setName(chargeNameField.getValue());
		charge.setPlants(includedPlants);
		
		chargeService.update(charge);
	}

	private void clearChargeDialog() {
		this.chargeNameField.setValue(chargeNameField.getEmptyValue());
		this.chargeRegDate.setValue(LocalDate.now());
		this.chargeAmountPlantsField.setValue(chargeAmountPlantsField.getEmptyValue());
		this.plantNameField.setValue(plantNameField.getEmptyValue());
		this.plantLocationBox.setValue(plantLocationBox.getEmptyValue());
		this.plantStatusBox.setValue(plantStatusBox.getEmptyValue());
		setCustomSettingsBox.setEnabled(true);
		setCustomSettingsBox.setValue(false);
		
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
					p.setStatus(GrowStatus.NEW);
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
				p.setStatus(GrowStatus.NEW);
				p.setGrowLocation(locationsByAssociation.get(0));
				tmpPlants.add(p);
			}
		}
		plantGrid.setItems(tmpPlants);
	}

	private Component createChargeDialogComponent() {
		
		HorizontalLayout mainLayout = new HorizontalLayout();
		mainLayout.addClassNames(LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
		
		H3 headerCharge = new H3("Charge - Details");
		headerCharge.addClassNames(LumoUtility.Margin.MEDIUM);
		VerticalLayout firstWrapper = new VerticalLayout();
		VerticalLayout chargeWrapperLayout = new VerticalLayout();
		firstWrapper.addClassNames("bottom-layout", "bestand-box");
		FormLayout layout = new FormLayout();
		layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
		
		chargeNumberField.setEnabled(false);
		chargeNumberField.setValue(String.valueOf((chargeService.count() + 1)));
		chargeRegDate.setValue(LocalDate.now());
			
		chargeNumberField.setWidthFull();
		chargeNameField.setWidthFull();
		chargeRegDate.setWidthFull();
		chargeAmountPlantsField.setWidthFull();
		chargeAmountPlantsField.setAllowedCharPattern("[0-9/]");
		
		layout.add(chargeNumberField, chargeNameField, chargeRegDate, chargeAmountPlantsField);
		
		Button firstStepButton = new Button("übernehmen");
		firstStepButton.addClassName("save-button");
		firstStepButton.setWidthFull();
		firstStepButton.addClickListener(e -> {
			
			if(chargeNameField.getValue() != null) {	
				if(chargeAmountPlantsField.getValue() != null) {					
					refreshPlantsGrid(chargeAmountPlantsField.getValue().intValue());	
					saveChargeButton.setEnabled(true);
				} else {
					Notification.show("Die Anzahl der Pflanzen muss angegeben sein.");
				}
			} else {
				Notification.show("Die Charge muss zuerst einen Namen haben.");
			}
		});
		
		chargeWrapperLayout.add(layout);
		firstWrapper.add(headerCharge, chargeWrapperLayout, firstStepButton);
		firstWrapper.setMaxWidth(400, Unit.PIXELS);
		VerticalLayout secondLayout = new VerticalLayout();
		secondLayout.addClassNames(LumoUtility.Margin.NONE, 
				LumoUtility.Padding.NONE, LumoUtility.FlexDirection.COLUMN, LumoUtility.Display.FLEX, LumoUtility.AlignItems.START);
		
		HorizontalLayout globalSettingsLayout = new HorizontalLayout();
		globalSettingsLayout.addClassNames("rechtliches-box-horizontal-start");
		globalSettingsLayout.setWidthFull();
		
		VerticalLayout checkBoxWrapper = new VerticalLayout();
		checkBoxWrapper.setAlignItems(Alignment.CENTER);
		checkBoxWrapper.setJustifyContentMode(JustifyContentMode.CENTER);
		checkBoxWrapper.add(setCustomSettingsBox);
		checkBoxWrapper.setSizeUndefined();
		
		setCustomSettingsBox.addClassNames(LumoUtility.Margin.Left.MEDIUM, LumoUtility.Margin.Top.SMALL);
		ComboBox<Location> globalLocationBox = new ComboBox<Location>("Standort");
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
	
		ComboBox<GrowStatus> globalStatusBox = new ComboBox<GrowStatus>("Status");
		globalStatusBox.setItems(GrowStatus.values());
		globalStatusBox.setItemLabelGenerator(e -> e.getLabel());
		globalStatusBox.setValue(globalStatusBox.getListDataView().getItem(0));
		globalStatusBox.setEnabled(false);
		globalStatusBox.addValueChangeListener(e -> {
			tmpPlants.forEach(p -> {
				p.setStatus(e.getValue());
			});
			plantGrid.setItems(tmpPlants);
		});
		
		globalSettingsLayout.add(checkBoxWrapper, globalLocationBox, globalStatusBox);
		
		setCustomSettingsBox.addValueChangeListener(e -> {
			globalLocationBox.setEnabled(e.getValue());
			globalStatusBox.setEnabled(e.getValue());
		});
		
		HorizontalLayout plantWrapper = new HorizontalLayout();
		plantWrapper.addClassNames("rechtliches-box-horizontal");
		plantWrapper.setWidthFull();
		
		plantGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
		plantGrid.addColumn(e -> e.getName()).setHeader("Name").setAutoWidth(true);
		plantGrid.setMinWidth(300, Unit.PIXELS);
		plantGrid.setMinHeight(400, Unit.PIXELS);
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
		
		H3 headerPlant = new H3("Pflanze - Details");
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
			tmpPlants.removeIf(p -> p.getNummer().equals(editPlant.getNummer()));
			
			tmpPlants.add(tmpPlant);
			tmpPlants.sort((o1, o2) -> Integer.compare(o1.getId().intValue(), o2.getId().intValue()));
			
			plantGrid.setItems(tmpPlants);
			editPlant = null;
		});
		
		plantLayout.add(plantNameField, plantStatusBox, plantLocationBox);
		plantLayout.setMinWidth(300, Unit.PIXELS);
		plantLayout.setHeightFull();
		formLayoutWrapper.add(headerPlant, plantLayout, updatePlantButton);
		
		plantWrapper.add(plantGrid, formLayoutWrapper);
		secondLayout.add(globalSettingsLayout, plantWrapper);
		mainLayout.add(firstWrapper, secondLayout);
		mainLayout.setFlexGrow(1, chargeWrapperLayout, plantWrapper);
		return mainLayout;
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
		addChargeButton.setText("+ Pflanze(n) hinzufügen");

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
			Avatar avatar;
			
			if(entity.isCharge()) {
				avatar = new Avatar("");				
				StreamResource imageResource = new StreamResource("seed.png",
						() -> getClass().getResourceAsStream("/seed.png"));
				
				avatar.setImageResource(imageResource);
			} else {
				avatar = new Avatar("");				
				StreamResource imageResource = new StreamResource("empty-plant.png",
						() -> getClass().getResourceAsStream("/empty-plant.png"));
				
				avatar.setImageResource(imageResource);
			}
			
			avatar.setHeight("64px");
			avatar.setWidth("64px");
			avatar.getElement().setAttribute("tabindex", "-1");
						
			Span fullName = new Span("Nummer: " + entity.getNummer());

		    Span profession = new Span("Name: " + entity.getName());
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
		}).setHeader("Nummer");

		chargeGrid.addColumn(e -> renderDate(e.getErfasst())).setHeader("Erfasst am");
		chargeGrid.addColumn(e -> e.getStatus() == null ? "-" : e.getStatus().getLabel()).setHeader("Status");
		chargeGrid.addColumn(e -> e.getLocation() == null ? "-" : e.getLocation().getName()).setHeader("Standort");
		
		chargeGrid.addComponentColumn(entity -> {
			MenuBar menuBar = new MenuBar();
			menuBar.setOverlayClassName("warenlager-view-menu-bar-1");
			menuBar.addClassName("warenlager-view-menu-bar-1");
			
			menuBar.addItem("Charge bearbeiten", event -> {
				setValuesInChargePopup(entity);
				addChargeDialog.open();
			});
			if (!entity.isCharge()) {
				menuBar.addItem("Pflanzen bearbeiten", event -> {
					Notification.show("Popup kommt noch.");
				});
				menuBar.addItem("Status ändern", event -> {
					Notification.show("Popup kommt noch.");
				});
			} else {
				menuBar.addItem("Status für alle ändern", event -> {
					Notification.show("Popup kommt noch.");
				});
			}
			return menuBar;
		}).setWidth("70px").setFlexGrow(0);

		refreshGrid(ViewStatus.CHARGE);
		wrapper.add(chargeGrid);
		tabSheet.add("Pflanze(n)", wrapper);
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
		saveChargeButton.setEnabled(false);
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
		comboBoxResponsibleForSeed.setValue(comboBoxResponsibleForSeed.getListDataView().getItem(0));
		
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
		this.cuttingPlantDate.setValue(LocalDateTime.of(changeCutting.getDatePlanted(), LocalTime.now()));
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

	private void createStrainsLayout(TabSheet tabSheet) {
		
		VerticalLayout wrapper = new VerticalLayout();
		wrapper.setClassName("grid-wrapper");

		wrapper.setHeightFull();	
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		
		VerticalLayout layoutButton = new VerticalLayout();
		layoutButton.addClassNames(LumoUtility.Padding.Left.NONE);

		Button addStrainButton = new Button();
		addStrainButton.addClassNames("button-layout-common");
		addStrainButton.setText("+ Sorte hinzufügen");
		
		addStrainButton.addClickListener(e -> {
			numberField.setValue(String.valueOf(strainService.getFreeStrainNumber(associationId)));
			addStrainDialog.open();
			deleteStrainButton.setEnabled(false);
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
		
		strainGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
		strainGrid.addColumn(p -> p.getStrainNumber()).setHeader("Nummer");
		strainGrid.addColumn(p -> p.getName()).setHeader("Name").setAutoWidth(true).setSortable(true);
		strainGrid.addColumn(p -> renderDate(p.getDatePlanted())).setHeader("Gepflanzt am").setAutoWidth(true).setSortable(true);
		strainGrid.addColumn(p -> renderDate(p.getDateFinished())).setHeader("Geerntet am").setAutoWidth(true).setSortable(true);
		strainGrid.addColumn(p -> p.getAmountGramm() == 0 ? "-" : p.getAmountGramm() + " Gramm").setHeader("Vorhandene Menge").setAutoWidth(true).setSortable(true);
		strainGrid.addColumn(p -> p.getStatus().getLabel()).setHeader("Status").setAutoWidth(true).setSortable(true);
		
		strainGrid.addComponentColumn(item -> {
			Button button = new Button("Details");
			button.addClickListener(click -> {
				changeStrain = item;
				openDialogForEdit(changeStrain);
				addStrainDialog.open();
			});
			button.addClassNames("button-grid-green");
			return button;
		}).setAutoWidth(true);
		
		refreshGrid(ViewStatus.STRAIN);
		
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
			refreshGrid(ViewStatus.STRAIN);
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
	
	private void openDialogForEdit(Blossom strain) {
		
		
		if(strain.getDateFinished() != null) {
			box.setValue(true);
			statusBox.setValue(strain.getStatus());
			
			responsiblePersonOne.setEnabled(true);
			List<Person> weighedByMembers = strain.getWeighedByMembers();
			responsiblePersonOne.setValue(weighedByMembers.get(0));
			responsiblePersonTwo.setEnabled(true);
			responsiblePersonTwo.setValue(weighedByMembers.get(1));
			
			strainInfoAmount.setEnabled(true);
			strainInfoAmount.setValue(strain.getAmountGramm());
			
			strainInfoThc.setEnabled(true);
			strainInfoThc.setValue(strain.getThc());
			
			amountPerGramm.setEnabled(true);
			amountPerGramm.setAmount(String.valueOf(strain.getPrice()));
			dateAvailable.setValue(LocalDateTime.of(strain.getDateFinished(), LocalTime.now()));
		} else {
			box.setValue(false);
			statusBox.setValue(strain.getStatus());
			
			responsiblePersonOne.setEnabled(false);
			responsiblePersonOne.setValue(responsiblePersonOne.getEmptyValue());
			responsiblePersonTwo.setEnabled(false);
			responsiblePersonTwo.setValue(responsiblePersonTwo.getEmptyValue());
			
			strainInfoAmount.setEnabled(false);
			strainInfoAmount.setValue(0.0);
			
			strainInfoThc.setEnabled(false);
			strainInfoThc.setValue(0.0);
			
			amountPerGramm.setEnabled(false);
			amountPerGramm.setAmount(String.valueOf(strain.getPrice()));
			dateAvailable.setValue(LocalDateTime.now());
		}
		
		numberField.setValue(String.valueOf(strain.getStrainNumber()));
		nameField.setValue(strain.getName());
		date.setValue(LocalDateTime.of(strain.getDatePlanted(), LocalTime.now()));
		locationBox.setValue(strain.getGrowLocation());
		amountOfPlantsField.setValue(String.valueOf(strain.getAmountOfPlants()));
	}

	private void createAddStrainDialog() {
		
		addStrainDialog = new Dialog();
		addStrainDialog.addClassNames(LumoUtility.Width.FULL);
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
				preparePath();
				addNewStrain(nameField.getValue(), date.getValue(), dateAvailable.getValue(), strainInfoAmount, strainInfoThc, statusBox);
				addStrainDialog.close();
				clearStrainDialog();
			} else { 
				Notification.show("Die Sorte muss einen Namen haben !");
			}
			
			deleteStrainButton.setEnabled(true);

		});
		saveButton.addClassName("save-button");
		
		Button cancelButton = new Button("Abbrechen", e -> {
			addStrainDialog.close();	
			clearStrainDialog();
			deleteStrainButton.setEnabled(true);
		});
		cancelButton.addClassNames("cancel-button");
		
		deleteStrainButton = new Button("Löschen", e -> {
			strainService.delete(changeStrain.getId());
			addStrainDialog.close();
			Notification.show("Sorte gelöscht.");
			deleteStrainButton.setEnabled(true);

		});
		
		addStrainDialog.addDialogCloseActionListener(e -> {
			clearStrainDialog();
		});
		
		deleteStrainButton.addClassNames("delete-button");
		
		addStrainDialog.getFooter().add(deleteStrainButton);
		addStrainDialog.getFooter().add(cancelButton);
		addStrainDialog.getFooter().add(saveButton);
	}
	
	private void createAddCuttingsDialog() {
		addCuttingsDialog = new Dialog();
		addCuttingsDialog.addClassNames(LumoUtility.MaxWidth.SCREEN_MEDIUM);
		
		VerticalLayout headerLayout = new VerticalLayout();
		HorizontalLayout headlineLayout = new HorizontalLayout();
		
		H2 header = new H2("Neue Stecklinge hinzufügen");
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
			clearStrainDialog();
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
		this.cuttingPlantDate.setValue(LocalDateTime.now());
		this.cuttingsAmountField.setValue("");
		this.comboBoxResponsibleForCutting.setValue(comboBoxResponsibleForCutting.getListDataView().getItem(0));
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
		cutting.setDatePlanted(cuttingPlantDate.getValue().toLocalDate());
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
		cuttingPlantDate = new DateTimePicker("Datum des Pflanzens");
		comboxCuttingStatus.setItems(Arrays.asList(GrowStatus.values()).stream()
				.filter(e -> (e == GrowStatus.NEW || (e == GrowStatus.GROWING || e == GrowStatus.READY))).toList());
		comboxCuttingStatus.setItemLabelGenerator(e -> e.getLabel());
		comboxCuttingStatus.setValue(GrowStatus.NEW);
		
		cuttingsAmountField = new TextField("Anzahl Stecklinge");
		cuttingsAmountField.setAllowedCharPattern("[0-9/]");
		
		cuttingsPriceField = new TextField("Preis pro Steckling");
		cuttingsPriceField.setAllowedCharPattern("[0-9/]");
		
		comboBoxResponsibleForCutting.setItems(personService.findAllByAssociation(associationId));
		comboBoxResponsibleForCutting.setItemLabelGenerator(e -> e.getFirstName() + " " + e.getLastName());
		comboBoxResponsibleForCutting.setValue(comboBoxResponsibleForCutting.getListDataView().getItem(0));
		
		List<Location> allByAssociation = locationService.findAllByAssociation(associationId);
		comboBoxLocationCutting.setItems(allByAssociation);
		comboBoxLocationCutting.setItemLabelGenerator(e -> e.getName());
		comboBoxLocationCutting.setValue(allByAssociation.isEmpty() ? comboBoxLocationCutting.getEmptyValue() : comboBoxLocationCutting.getListDataView().getItem(0));
		
		formLayout.add(cuttingNumberField, cuttingNameField, comboboxCuttingsOrigins, cuttingPlantDate,
				comboxCuttingStatus, cuttingsAmountField, cuttingsPriceField, comboBoxResponsibleForCutting, comboBoxLocationCutting);
		return formLayout;
	}

	private void clearStrainDialog() {
		box.setValue(false);
		dateAvailable.setValue(LocalDateTime.now());
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
		date.setValue(LocalDateTime.now());
		locationBox.setValue(locationBox.getEmptyValue());
		amountOfPlantsField.setValue("");
		
		if(changeStrain != null) {
			changeStrain = null;
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
		numberField.setValue(String.valueOf(strainService.getFreeStrainNumber(associationId)));
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
		List<Location> allByAssociation = locationService.findAllByAssociation(associationId);
		locationBox.setItems(allByAssociation);
		locationBox.setValue(allByAssociation.isEmpty() ? locationBox.getEmptyValue() : locationBox.getListDataView().getItem(0));
		locationBox.setItemLabelGenerator(e -> e.getName());
		
		amountOfPlantsField = new TextField("Anzahl Pflanzen");
		amountOfPlantsField.setAllowedCharPattern("[0-9/]");
		
		formLayout.add(numberField, nameField, amountOfPlantsField, date, locationBox, statusBox);
		return formLayout;
	}

	private void addNewStrain(String name, LocalDateTime dateBegin, LocalDateTime dateEnd, NumberField strainInfoAmount, NumberField strainInfoThc, ComboBox<GrowStatus> statusBox) {
		
		Blossom newStrain;
		if (changeStrain != null) {
			newStrain = changeStrain;
		} else {			
			newStrain = new Blossom();
		}
		
		newStrain.setStrainNumber(Integer.valueOf(numberField.getValue()));
		newStrain.setName(name);
		newStrain.setDatePlanted(dateBegin.toLocalDate());
		newStrain.setAssociationId(associationId);
		newStrain.setStatus(statusBox.getValue());
		newStrain.setGrowLocation(locationBox.getValue());
		newStrain.setAmountOfPlants(Integer.valueOf(amountOfPlantsField.getValue()));
		
		if (box.getValue()) {
			newStrain.setDateFinished(dateEnd.toLocalDate());
			newStrain.setAmountGramm(strainInfoAmount.getValue());
			newStrain.setThc(strainInfoThc.getValue());
			newStrain.setWeighedByMembers(
					Arrays.asList(responsiblePersonOne.getValue(), responsiblePersonTwo.getValue()));
			if(amountPerGramm.getValue() != null) {				
				newStrain.setPrice(amountPerGramm.getValue().getNumber().doubleValue());
			}

			if (statusBox.getValue().ordinal() > 3 && pathToCertificate != null) {
				handleFile();
				newStrain.setPathOfCertificate(pathToCertificate.getAbsolutePath());
				pathToCertificate = null;

			}
		}

		strainService.update(newStrain);

		refreshGrid(ViewStatus.STRAIN);
	}
	
	private void refreshGrid(ViewStatus status) {
		
		if(status == ViewStatus.STRAIN) {
		allStrainsByAssociation = strainService.findAllByAssociation(associationId);
		
		if(outputAssociation.isEmpty()) {
			outputAssociation = outputService.findAllByAssociation(associationId);
		}
		
		this.strainGrid.setItems(allStrainsByAssociation);
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
	        this.chargeGrid.setTreeData(builtTreeDate());
		}
	}

	private TreeData<EntityWrapper> builtTreeDate() {

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
