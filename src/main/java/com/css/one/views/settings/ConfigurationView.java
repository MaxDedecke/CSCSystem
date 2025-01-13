package com.css.one.views.settings;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.vaadin.lineawesome.LineAwesomeIcon;

import com.css.one.data.AssociationSettings;
import com.css.one.data.Blossom;
import com.css.one.data.Cutting;
import com.css.one.data.Location;
import com.css.one.data.OnboardingAnswer;
import com.css.one.data.OnboardingData;
import com.css.one.data.OnboardingQuestion;
import com.css.one.data.Seed;
import com.css.one.data.SubscriptionModel;
import com.css.one.data.WorkingUnit;
import com.css.one.data.WorkingUnitCategory;
import com.css.one.data.enums.ExpirationTime;
import com.css.one.services.AssociationSettingsService;
import com.css.one.services.BlossomService;
import com.css.one.services.CuttingService;
import com.css.one.services.LocationService;
import com.css.one.services.OnboardingAnswerService;
import com.css.one.services.OnboardingDataService;
import com.css.one.services.OnboardingQuestionService;
import com.css.one.services.SeedService;
import com.css.one.services.SubscriptionModelService;
import com.css.one.services.WorkingUnitCategoryService;
import com.css.one.services.WorkingUnitService;
import com.css.one.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.PermitAll;


@PageTitle("Konfiguration")
@Route(value = "setting", layout = MainLayout.class)
@PermitAll
public class ConfigurationView extends VerticalLayout {

	private static final long serialVersionUID = 4433493049583669274L;
	
	private LocationService locationService;
	private BlossomService strainService;
	private SeedService seedService;
	private CuttingService cuttingService;
	private WorkingUnitService workingUnitService;
	private SubscriptionModelService subscriptionModelService;
	private OnboardingQuestionService onboardingQuestionService;
	private OnboardingAnswerService onboardingAnswerService;
	private OnboardingDataService onboardingDataService;
	private AssociationSettingsService associationSettingsService;
	
	private WorkingUnitCategoryService workingUnitCategoryService;
	private int associationId;
	
	private TabSheet tabSheet = new TabSheet();
	
	private Dialog addLocationDialog;
	private Dialog addCategoryDialog;
	private Dialog addPricingModelDialog;
	private Dialog addOnboardingQuestionDialog;
	
	private TextField nameLocationField = new TextField("Name");
	private TextField streetLocationField = new TextField("Straße");
	private TextField streetNumberLocationField = new TextField("Hausnummer");
	private TextField postalCodeLocationField = new TextField("PLZ");
	private TextField cityLocationField = new TextField("Stadt");
	private TextField noteLocationField = new TextField("Notiz");

	private VerticalLayout membershipTabLayout = new VerticalLayout();
	private HorizontalLayout twoSidesMembershipLayout = new HorizontalLayout();
	private VerticalLayout modelCardWrapper = new VerticalLayout();

	private TextField nameOfPlan = new TextField("Titel");
	private NumberField priceOfPlan = new NumberField("Preis pro Monat");
	private TextArea descriptionOfModel = new TextArea("Beschreibung");
	private Checkbox activeBox = new Checkbox("aktiv");
	private NumberField amountOfMembers = new NumberField("Maximale Anzahl Mitglieder");
	private Grid<SubscriptionModel> subscriptionModelGrid = new Grid<SubscriptionModel>();

	private Span amountPerMonth = new Span();
	private Span title = new Span();
	private Span description = new Span();
	private Span amountPerMonthPreview = new Span();
	private Span titlePreview = new Span();
	private Span descriptionPreview = new Span();	
	private TextField categoryNameField = new TextField("Name");
	
	private Grid<Location> locationsGrid = new Grid<Location>();
	private Grid<WorkingUnitCategory> categoriesGrid = new Grid<WorkingUnitCategory>();
	private Grid<OnboardingQuestion> questionGrid = new Grid<OnboardingQuestion>();

	private Button saveLocationButton = new Button("erfassen");
	private Button saveCategoryButton = new Button("hinzufügen");
	private Button addPricingModelButton = new Button();
	private Button saveOnboardingSettingsButton;
	private Button addQuestionButton;
	
	private AssociationSettings associationSettings;
	private Location selectedLocation;
	private WorkingUnitCategory selectedCategory;
	private SubscriptionModel subscriptionModel;
	
	List<SubscriptionModel> pricingModels = new ArrayList<SubscriptionModel>();
	List<OnboardingQuestion> onboardingQuestions = new ArrayList<OnboardingQuestion>();

	H3 questionsHeader = new H3();
	H3 counterSubscriptionModels = new H3("0/3");
	
	public enum ViewStatus {
		LOCATION, WORKINGCATEGORY;
	}
	
	public ConfigurationView(LocationService locationService,
			WorkingUnitCategoryService workingUnitCategoryService, 
			BlossomService strainService, 
			SeedService seedService, 
			CuttingService cuttingService,
			WorkingUnitService workingUnitService,
			SubscriptionModelService subscriptionModelService,
			OnboardingQuestionService onboardingQuestionService,
			AssociationSettingsService associationSettingsService,
			OnboardingAnswerService onboardingAnswerService,
			OnboardingDataService onboardingDataService) {
		
		this.locationService = locationService;
		this.workingUnitCategoryService = workingUnitCategoryService;
		this.strainService = strainService;
		this.seedService = seedService;
		this.cuttingService = cuttingService;
		this.workingUnitService = workingUnitService;
		this.subscriptionModelService = subscriptionModelService;
		this.onboardingQuestionService = onboardingQuestionService;
		this.associationSettingsService = associationSettingsService;
		this.onboardingAnswerService = onboardingAnswerService;
		this.onboardingDataService = onboardingDataService;
		
		addClassNames("configuration-view", LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
		
		associationId = MainLayout.getAssociationId();
		
		loadAssociationSettings();
		
		tabSheet.setSizeFull();
		tabSheet.addClassNames(LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
		tabSheet.add("Onboarding", createOnboardingTab());
		tabSheet.add("Abomodelle", createMembershipTab());
		tabSheet.add("Standorte", createLocationsTab());	
		tabSheet.add("Arbeitsplanung", createWorkingEnvTab());
		tabSheet.add("Finanzen", createFinancesTab());
		tabSheet.add("Autorisierung", createAuthorizationTab());
		tabSheet.add("Import", createImportTab());
		tabSheet.add("Export", createExportTab());
		tabSheet.add("Allgemein", createCommonSettingsTab());
		
		addCategoryDialog();
		addPricingModelDialog();
		addOnboardingQuestionDialog();
		
		add(tabSheet);
    }
	
	private void loadAssociationSettings() {
		Optional<AssociationSettings> optSettings = associationSettingsService.findAllByAssociation(associationId);	
		associationSettings = optSettings.isPresent() ? optSettings.get() : associationSettingsService.createInitialSettings(associationId);		
	}

	private void addOnboardingQuestionDialog() {

		addOnboardingQuestionDialog = new Dialog();
		
		VerticalLayout wrapper = new VerticalLayout();
		
		H3 h3 = new H3("Frage hinzufügen");
		h3.addClassName("customheader");
		
		TextArea questionArea = new TextArea();
		questionArea.setMinWidth(500, Unit.PIXELS);
		questionArea.setMinHeight(120, Unit.PIXELS);
		wrapper.add(h3, questionArea);
		
		Button saveButton = new Button("Speichern");
		saveButton.addClassNames("save-button");
		saveButton.addClickListener(e -> {
			if(!questionArea.getValue().equals("")) {				
				addOnboardingQuestion(questionArea.getValue());
				addOnboardingQuestionDialog.close();
				questionArea.clear();
				Notification notification = Notification.show("Frage erfolgreich hinzugefügt.");
				notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
			} else {
				Notification notification = Notification.show("Zuerst muss eine Frage existieren.");
				notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
			}
		});
		
		Button cancelButton = new Button("Abbrechen");
		cancelButton.addClassNames("cancel-button");
		cancelButton.addClickListener(e -> {
			addOnboardingQuestionDialog.close();
			questionArea.clear();
		});
		
		addOnboardingQuestionDialog.addDialogCloseActionListener(e -> {
			questionArea.clear();
			addOnboardingQuestionDialog.close();
		});
		
		addOnboardingQuestionDialog.getFooter().add(cancelButton, saveButton);
		addOnboardingQuestionDialog.add(wrapper);
	}

	private void addOnboardingQuestion(String questionText) {
		OnboardingQuestion question = new OnboardingQuestion();
		question.setAssociationId(associationId);
		question.setQuestion(questionText);
		
		onboardingQuestionService.update(question);
		refreshOnboardingQuestionGrid();
	}

	private void addPricingModelDialog() {
		addPricingModelDialog = new Dialog();
		VerticalLayout wrapper = new VerticalLayout();
		HorizontalLayout twoSidesLayout = new HorizontalLayout();
		
		twoSidesLayout.add(createLeftSide(), createRightSide());
		wrapper.add(twoSidesLayout);
		
		Button cancelButton = new Button("Abbrechen");
		cancelButton.addClassName("cancel-button");
		cancelButton.addClickListener(e -> {
			clearPricingModelDialog();
			addPricingModelDialog.close();
			
		});
		
		Button saveButton = new Button("Speichern");
		saveButton.addClassName("save-button");
		saveButton.addClickListener(e -> {
			if(validateData()) {
				saveSubscriptionModel();
				addPricingModelDialog.close();
				if(pricingModels.isEmpty()) {					
					switchToModelList();
				}
				refreshModelGrid();
				clearPricingModelDialog();
			}
		});
		
		addPricingModelDialog.add(wrapper);
		addPricingModelDialog.getFooter().add(cancelButton, saveButton);
		
		addPricingModelDialog.addDialogCloseActionListener(e -> {
			addPricingModelDialog.close();
			clearPricingModelDialog();
		});
	}

	private void saveSubscriptionModel() {
		boolean isNew = this.subscriptionModel == null;
		
		if(isNew) {
			this.subscriptionModel = new SubscriptionModel();
		} 
		
		//set data
		this.subscriptionModel.setAmount(this.priceOfPlan.getValue());
		this.subscriptionModel.setAssociationId(associationId);
		this.subscriptionModel.setDescription(descriptionOfModel.getValue());
		this.subscriptionModel.setMaxAllowedMembers(amountOfMembers.getValue().intValue());
		this.subscriptionModel.setName(nameOfPlan.getValue());
		this.subscriptionModel.setOnline(activeBox.getValue());
		
		//save to database
		subscriptionModelService.update(this.subscriptionModel);
		
		if(isNew) {			
			Notification show = Notification.show("Tarif erfolgreich erstellt");
			show.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
		} else {
			Notification show = Notification.show("Tarif erfolgreich geupdated");
			show.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
		}
		
		this.subscriptionModel = null;
	}

	private boolean validateData() {
		
		if(nameOfPlan.getValue().isBlank()) {
			nameOfPlan.setInvalid(true);
			nameOfPlan.setHelperText("Das Angebot muss einen Namen haben");
			nameOfPlan.getStyle().set("--vaadin-input-field-invalid-background", "--lumo-error-color-10pct");
			return false;
		} else {
			nameOfPlan.setInvalid(false);
			nameOfPlan.setHelperText("");
			nameOfPlan.getStyle().set("--vaadin-input-field-invalid-background", "--lumo-contrast-10pct");	
		}
		
		if(priceOfPlan.getValue() == null || priceOfPlan.getValue() == 0.0) {
			priceOfPlan.setInvalid(true);
			priceOfPlan.setHelperText("Das Angebot muss einen Preis haben");
			priceOfPlan.addClassName("invalid-number-field");
			return false;
		} else {
			priceOfPlan.setInvalid(false);
			priceOfPlan.setHelperText("");
			priceOfPlan.removeClassName("invalid-number-field");
			priceOfPlan.addClassName("valid-number-field");
			priceOfPlan.getStyle().set("--vaadin-input-field-invalid-background", "--lumo-contrast-10pct");		
		}
		
		if(descriptionOfModel.getValue().isBlank()) {
			descriptionOfModel.setInvalid(true);
			descriptionOfModel.setHelperText("Das Angebot muss eine Beschreibung haben");
			descriptionOfModel.addClassName("invalid-number-field");	
			return false;
		} else {
			descriptionOfModel.setInvalid(false);
			descriptionOfModel.setHelperText("");
			descriptionOfModel.getStyle().set("--vaadin-input-field-invalid-background", "--lumo-contrast-10pct");				
		}
		
		if(amountOfMembers.getValue() == null) {
			amountOfMembers.setInvalid(true);
			amountOfMembers.setHelperText("Es muss feststehen, für wieviele Mitlglieder das Angebut buchbar ist");
			amountOfMembers.addClassName("invalid-number-field");
			return false;
		} else {
			amountOfMembers.setInvalid(false);
			amountOfMembers.setHelperText("");
			amountOfMembers.removeClassName("invalid-number-field");
			amountOfMembers.addClassName("valid-number-field");
			amountOfMembers.getStyle().set("--vaadin-input-field-invalid-background", "--lumo-contrast-10pct");				
		}		
		
		return true;
	}

	private void clearPricingModelDialog() {
		this.amountOfMembers.setValue(amountOfMembers.getEmptyValue());
		this.priceOfPlan.setValue(priceOfPlan.getEmptyValue());
		this.descriptionOfModel.setValue(descriptionOfModel.getEmptyValue());
		this.activeBox.setValue(false);
		this.nameOfPlan.setValue(nameOfPlan.getEmptyValue());
		
		this.amountPerMonth.setText("100€");
		this.title.setText("Mustertarif");
		this.description.setText("They can’t be focused or display tooltips. They’re invisible to screen readers, and their values cannot be selected and copied.\r\n"
				+ "\r\n"
				+ "Disabled fields can be useful in situations where they can become enabled based on some user action. Consider hiding fields entirely if there’s nothing the user can do to make them editable.");		
	}

	private Component createRightSide() {
		VerticalLayout wrapper = new VerticalLayout();
		
		VerticalLayout card = new VerticalLayout();
		card.setMinHeight(500, Unit.PIXELS);
		card.setMinWidth(500, Unit.PIXELS);
		card.setMaxWidth(550, Unit.PIXELS);
		card.addClassNames("bestand-box");
		
		VerticalLayout priceWrapper = new VerticalLayout();
		priceWrapper.addClassNames(LumoUtility.JustifyContent.CENTER, LumoUtility.AlignItems.CENTER);
		priceWrapper.setWidthFull();
		amountPerMonth.setText("100€");
		amountPerMonth.addClassNames("price-membership");
		
		Span perMonth = new Span("/pro Monat");
		perMonth.addClassNames("desc-membership", LumoUtility.Margin.Top.LARGE);
		priceWrapper.add(amountPerMonth, perMonth);
		
		VerticalLayout titleWrapper = new VerticalLayout();
		titleWrapper.addClassNames(LumoUtility.JustifyContent.CENTER, LumoUtility.AlignItems.CENTER);
		title.setText("Mustertarif");
		title.addClassNames("title-membership");
		titleWrapper.add(title);
		
		VerticalLayout descWrapper = new VerticalLayout();
		descWrapper.addClassNames(LumoUtility.JustifyContent.CENTER, LumoUtility.AlignItems.CENTER);
		description.setText("They can’t be focused or display tooltips. They’re invisible to screen readers, and their values cannot be selected and copied.\r\n"
				+ "\r\n"
				+ "Disabled fields can be useful in situations where they can become enabled based on some user action. Consider hiding fields entirely if there’s nothing the user can do to make them editable.");
		
		description.addClassNames("desc-membership");
		descWrapper.add(description);
		
		card.add(priceWrapper, titleWrapper, descWrapper);
		
		VerticalLayout iconWrapper = new VerticalLayout();
		iconWrapper.addClassNames(LumoUtility.JustifyContent.CENTER, LumoUtility.AlignItems.CENTER);
		iconWrapper.setWidthFull();
		
		SvgIcon svgIcon = LineAwesomeIcon.INFO_CIRCLE_SOLID.create();
		Tooltip.forComponent(svgIcon).withText("Mehrere Angebote werden immer von links nach rechts aufgereit");
		iconWrapper.add(svgIcon);
		
		wrapper.add(card, iconWrapper);
		return wrapper;
	}

	private Component createLeftSide() {
		VerticalLayout wrapper = new VerticalLayout();
		H2 h2 = new H2("Abomodel erstellen");
		h2.addClassName("customheader");
		
		FormLayout formLayout = new FormLayout();
		formLayout.setMinHeight(500, Unit.PIXELS);
		formLayout.setMinWidth(400, Unit.PIXELS);
		formLayout.setMaxWidth(500, Unit.PIXELS);
		
		nameOfPlan.addValueChangeListener(e -> {
			title.setText(e.getValue());
		});
		
		priceOfPlan.addValueChangeListener(e -> {
			if (e.getValue() != null) {
				String value = e.getValue().toString().replace(".", ",");
				if(value.endsWith(",0")) {
					value = value.replace(",0", "");
				}
				amountPerMonth.setText(value + "€");
			}
		});
		Div euroSuffix = new Div();
		euroSuffix.setText("€");
		priceOfPlan.setSuffixComponent(euroSuffix);

		descriptionOfModel.setMinHeight(200, Unit.PIXELS);
		String charLimit = "800";
		descriptionOfModel.setHelperText("0" + "/" + charLimit);
		descriptionOfModel.addValueChangeListener(e -> {
			description.setText(e.getValue().toString());
			e.getSource().setHelperText(e.getValue().length() + "/" + charLimit);
		});
		
		activeBox.addClassNames(LumoUtility.Margin.Top.LARGE);
				
		formLayout.add(nameOfPlan, priceOfPlan, descriptionOfModel, new Hr(), amountOfMembers, activeBox);
		wrapper.add(h2, formLayout);
		return wrapper;
	}

	private Component createMembershipTab() {
		membershipTabLayout.setHeight("100%");
		membershipTabLayout.setWidthFull();
		
		membershipTabLayout.addClassNames(LumoUtility.JustifyContent.CENTER, LumoUtility.Margin.NONE, LumoUtility.Padding.NONE, LumoUtility.AlignItems.CENTER);
		addPricingModelButton.addClassNames("save-button", "animation-button-fade");
		
		addPricingModelButton.addClickListener(e -> {
			this.subscriptionModel = null;
			addPricingModelDialog.open();
		});
		
		pricingModels = subscriptionModelService.findAllByAssociation(associationId);
		
		if(pricingModels.isEmpty()) {
			addPricingModelButton.setText("Erstelle dein erstes Abomodell");			

			membershipTabLayout.add(addPricingModelButton);
		} else {
			addPricingModelButton.setText("Abomodell hinzufügen");
			createMembershipTabContent(membershipTabLayout, pricingModels);
		}
		
		return membershipTabLayout;
	}

	private void createMembershipTabContent(VerticalLayout mainLayout, List<SubscriptionModel> models) {
		
		twoSidesMembershipLayout.addClassNames(LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
		twoSidesMembershipLayout.setWidthFull();
		twoSidesMembershipLayout.setHeightFull();
		
		twoSidesMembershipLayout.add(createModelListComponent(models), createModelCardComponent());
		
		refreshModelGrid();
		
		subscriptionModelGrid.select(models.iterator().next());
		
		VerticalLayout membershipLayoutWrapper = new VerticalLayout();
		membershipLayoutWrapper.addClassNames(LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
		membershipLayoutWrapper.setWidthFull();
		membershipLayoutWrapper.setHeightFull();
		
		membershipLayoutWrapper.add(twoSidesMembershipLayout, createLimitComponent());
		mainLayout.add(membershipLayoutWrapper);	
	}
	
	private Component createLimitComponent() {
		VerticalLayout wrapper = new VerticalLayout();
		counterSubscriptionModels.addClassNames("counter");
		
		wrapper.setWidthFull();
		wrapper.addClassName(LumoUtility.JustifyContent.CENTER);
		
		counterSubscriptionModels.addClassName("customheader");
		counterSubscriptionModels.setText(subscriptionModelService.findAllByAssociation(associationId).size() + "/3");
		
		wrapper.add(counterSubscriptionModels);
		
		return wrapper;
	}

	private void refreshModelGrid() {
		pricingModels = subscriptionModelService.findAllByAssociation(associationId);
		
		counterSubscriptionModels.setText(pricingModels.size() + "/3");
		
		//disable button if already 3 existing
		addPricingModelButton.setVisible(!(pricingModels.size() >= 3));
		

		if(!pricingModels.isEmpty()) {			
			subscriptionModelGrid.setItems(pricingModels);
		}
	}
	
	private void switchToNoModelView() {	
		modelCardWrapper.removeAll();
		twoSidesMembershipLayout.removeAll();
		membershipTabLayout.removeAll();
		membershipTabLayout.add(addPricingModelButton);
		
		addPricingModelButton.addClassName("fade-in");

		// Layout mit Button hinzufügen
		membershipTabLayout.add(addPricingModelButton);

		// Transition aktivieren
		UI.getCurrent().access(() -> {
			addPricingModelButton.addClassName("fade-in-active");
		});
	}
	
	private void switchToModelList() {	
		pricingModels = subscriptionModelService.findAllByAssociation(associationId);
		createMembershipTabContent(membershipTabLayout, pricingModels);
	}

	private Component createModelCardComponent() {
		modelCardWrapper.setWidthFull();
		modelCardWrapper.setHeightFull();
		modelCardWrapper.addClassNames(LumoUtility.JustifyContent.CENTER, LumoUtility.AlignItems.CENTER);
		
		VerticalLayout card = new VerticalLayout();
		card.setMinHeight(500, Unit.PIXELS);
		card.setMinWidth(500, Unit.PIXELS);
		card.setMaxWidth(550, Unit.PIXELS);
		card.addClassNames("bestand-box");
		
		VerticalLayout priceWrapper = new VerticalLayout();
		priceWrapper.addClassNames(LumoUtility.JustifyContent.CENTER, LumoUtility.AlignItems.CENTER);
		priceWrapper.setWidthFull();
		amountPerMonthPreview.setText("100€");
		amountPerMonthPreview.addClassNames("price-membership");
		
		Span perMonth = new Span("/pro Monat");
		perMonth.addClassNames("desc-membership", LumoUtility.Margin.Top.XLARGE);
		priceWrapper.add(amountPerMonthPreview, perMonth);
		
		VerticalLayout titleWrapper = new VerticalLayout();
		titleWrapper.addClassNames(LumoUtility.JustifyContent.CENTER, LumoUtility.AlignItems.CENTER);
		titlePreview.setText("Mustertarif");
		titlePreview.addClassNames("title-membership");
		titleWrapper.add(titlePreview);
		
		VerticalLayout descWrapper = new VerticalLayout();
		descWrapper.addClassNames(LumoUtility.JustifyContent.CENTER, LumoUtility.AlignItems.CENTER);
		descriptionPreview.setText("They can’t be focused or display tooltips. They’re invisible to screen readers, and their values cannot be selected and copied.\r\n"
				+ "\r\n"
				+ "Disabled fields can be useful in situations where they can become enabled based on some user action. Consider hiding fields entirely if there’s nothing the user can do to make them editable.");
		
		descriptionPreview.addClassNames("desc-membership");
		descWrapper.add(descriptionPreview);
		
		card.add(priceWrapper, titleWrapper, descWrapper);
		
		modelCardWrapper.add(card);
		
		return modelCardWrapper;
	}

	private Component createModelListComponent(List<SubscriptionModel> models) {
		VerticalLayout wrapper = new VerticalLayout();
		wrapper.setMaxWidth(400, Unit.PIXELS);
		
		subscriptionModelGrid.removeAllColumns();
		
		subscriptionModelGrid.addClassNames(LumoUtility.Padding.NONE, LumoUtility.Margin.Top.NONE, "custom-scrollbar");
		subscriptionModelGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

		subscriptionModelGrid.addColumn(e -> e.getName()).setAutoWidth(true);
		subscriptionModelGrid.addColumn(e -> e.isOnline() ? "aktiv" : "nicht aktiv").setAutoWidth(true)
		.setTooltipGenerator(e -> e.isOnline() ? "Das Abo wird im Onboarding angeboten" : "Das Abo wird nicht angeboten");
		
		subscriptionModelGrid.addComponentColumn(item -> {
			SvgIcon icon = LineAwesomeIcon.EDIT.create();
			icon.addClassName("icon");
			icon.addClickListener(click -> {
				this.subscriptionModel = item;
				initSubscriptionModelDialog();
				addPricingModelDialog.open();
			});
			
			Tooltip.forComponent(icon).withText("bearbeiten");
			return icon;
		}).setWidth("10%");
		
		subscriptionModelGrid.addComponentColumn(item -> {
			SvgIcon icon = LineAwesomeIcon.TRASH_ALT.create();
			icon.addClassName("icon");
			icon.addClickListener(click -> {
				this.subscriptionModel = item;
				removeSubscriptionModel();
				Notification notification = Notification.show("Abomodell erfolgreich gelöscht");
				notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
			});

			Tooltip.forComponent(icon).withText("löschen");
			return icon;
		}).setWidth("10%");
		
		subscriptionModelGrid.addSelectionListener(e -> {
			e.getFirstSelectedItem().ifPresent(model -> {
				switchModelCard(model);
			});
		});
		
		wrapper.add(addPricingModelButton, subscriptionModelGrid);
		return wrapper;
	}

	private void switchModelCard(SubscriptionModel model) {
		
		String value = String.valueOf(model.getAmount()).replace(".", ",");
		if(value.endsWith(",0")) {
			value = value.replace(",0", "");
		}
		this.amountPerMonthPreview.setText(value + "€");
		
		this.descriptionPreview.setText(model.getDescription());
		this.titlePreview.setText(model.getName());
	}

	private void removeSubscriptionModel() {
		Optional<SubscriptionModel> optModel = subscriptionModelService.get(subscriptionModel.getId());
		
		optModel.ifPresent(model -> {
			if (subscriptionModel.getMemberOfModel().isEmpty()) {
				if (subscriptionModel.getWaitingPersonOfModel().isEmpty()) {
					subscriptionModelService.delete(this.subscriptionModel.getId());
				} else {
					Notification notification = Notification.show("Es stehen noch Personen auf der Warteliste");
					notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
				}
			} else {
				Notification notification = Notification
						.show("Es gibt noch Mitglieder, die diesen Tarif abonniert haben");
				notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
			}
			this.subscriptionModel = null;
			refreshModelGrid();
			
			if(subscriptionModelService.findAllByAssociation(associationId).isEmpty()) {				
				switchToNoModelView();
			}
		});
	}

	private void initSubscriptionModelDialog() {
		this.activeBox.setValue(this.subscriptionModel.isOnline());
		this.nameOfPlan.setValue(this.subscriptionModel.getName());
		this.descriptionOfModel.setValue(this.subscriptionModel.getDescription());
		this.amountOfMembers.setValue((double)this.subscriptionModel.getMaxAllowedMembers());
		this.priceOfPlan.setValue(this.subscriptionModel.getAmount());
	}

	private Component createCommonSettingsTab() {
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setHeight("100%");
		
		mainLayout.addClassNames(LumoUtility.JustifyContent.CENTER, LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
		H1 help = new H1("Work in progress.");

		StreamResource imageResource = new StreamResource("potteriepng.png",
				() -> getClass().getResourceAsStream("/potteriepng.png"));

		Avatar avatar = new Avatar("logo_club");
		avatar.setImageResource(imageResource);
		avatar.setHeight(250, Unit.PIXELS);
	    avatar.setWidth(250, Unit.PIXELS);
		
		mainLayout.add(avatar, help);
		return mainLayout;
	}

	private Component createOnboardingTab() {
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setHeight("100%");
		mainLayout.setWidthFull();
		mainLayout.addClassNames(LumoUtility.AlignItems.STRETCH);
						
		mainLayout.add(createTokenDueDateWrapper(), createQuestionsWrapper());
		refreshOnboardingQuestionGrid();
		return mainLayout;
	}

	private Component createQuestionsWrapper() {
		VerticalLayout questionsMainWrapper = new VerticalLayout();
		questionsMainWrapper.addClassNames(LumoUtility.Margin.Top.XLARGE);
		questionsMainWrapper.setMaxWidth(1200, Unit.PIXELS);
		
		HorizontalLayout headerWrapper = new HorizontalLayout();
		headerWrapper.addClassNames(LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);		
		questionsHeader.addClassNames("customheader", "lower");
		
		addQuestionButton = new Button(LineAwesomeIcon.PLUS_SOLID.create());
		addQuestionButton.addClassNames("add-cross");
		addQuestionButton.addClickListener(e -> {
			addOnboardingQuestionDialog.open();
		});
		
		headerWrapper.add(questionsHeader, addQuestionButton);
		questionGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
		questionGrid.setMinHeight(300, Unit.PIXELS);
		questionGrid.addColumn(e -> e.getQuestion()).setTooltipGenerator(e -> e.getQuestion()).setFlexGrow(9);
		questionGrid.addComponentColumn(item -> {
			SvgIcon icon = LineAwesomeIcon.TRASH_ALT.create();
			icon.addClassName("icon");
			icon.addClickListener(click -> {
				removeOnboardingQuestion(item);
				refreshOnboardingQuestionGrid();
				Notification notification = Notification.show("Frage erfolgreich gelöscht");
				notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
			});

			Tooltip.forComponent(icon).withText("löschen");
			return icon;
		
		}).setFlexGrow(1);
		
		questionGrid.addClassName("custom-scrollbar");
		questionsMainWrapper.add(headerWrapper, questionGrid);
		return questionsMainWrapper;
	}

	private void removeOnboardingQuestion(OnboardingQuestion item) {
		
		List<OnboardingAnswer> answersOfQuestion = onboardingAnswerService.findByQuestion(item.getId());
		List<OnboardingData> existingOnboardingData = onboardingDataService.findAllByAssociation(associationId);
			
		//first, remove all answers of question from existing onboarding data objects
		existingOnboardingData.forEach(existingData -> {
			List<OnboardingAnswer> answersOfExistingData = existingData.getAnswers();
			
			for(OnboardingAnswer answerOfExistingData : answersOfExistingData) {
				
				//if answer of onboarding data is under the ones about to be deleted
				if(answersOfQuestion.stream().filter(answerOfQuestion -> answerOfQuestion.getId().equals(answerOfExistingData.getId())).findAny().isPresent()) {
					
					//create temporary list of answers of data
					List<OnboardingAnswer> tmpAnswers = new ArrayList<>(answersOfExistingData);
					
					//remove the identified answer
					tmpAnswers.remove(answerOfExistingData);
					
					//assign to onboarding data and update the object
					existingData.setAnswers(tmpAnswers);
					onboardingDataService.update(existingData);
				}
			}
		});
		
		//second, delete all existing answers of the question
		answersOfQuestion.forEach(e -> onboardingAnswerService.delete(e.getId()));
		
		//then delete question itself
		onboardingQuestionService.delete(item.getId());
	}
	
	private void refreshOnboardingQuestionGrid() {
		onboardingQuestions = onboardingQuestionService.findAllByAssociation(associationId);
		
		questionsHeader.setText("Onboarding Fragen   " + onboardingQuestions.size() + "/3");
		
		addQuestionButton.setVisible(!(onboardingQuestions.size() >= 3));

		this.questionGrid.setItems(onboardingQuestions);
	}

	private Component createTokenDueDateWrapper() {
		VerticalLayout tokenLengthWrapper = new VerticalLayout();
		tokenLengthWrapper.addClassNames(LumoUtility.Margin.Top.XLARGE);
		
		H3 tokenLengthHeader = new H3();
		tokenLengthHeader.setText("Onboarding Link - Ablaufdatum");
		tokenLengthHeader.addClassName("customHeader");
		
		HorizontalLayout innerWrapper = new HorizontalLayout();
		
		ComboBox<ExpirationTime> periodBox = new ComboBox<ExpirationTime>("Zeitraum");
		periodBox.setItems(ExpirationTime.values());
		periodBox.setItemLabelGenerator(time -> time.getLabel());
		periodBox.setMinWidth(400, Unit.PIXELS);
		
		if(associationSettings.getOnboardingTokenExpirationTime() != null) {
			periodBox.setValue(associationSettings.getOnboardingTokenExpirationTime());		
		} else {
			periodBox.setValue(periodBox.getListDataView().getItem(0));
		}
		
		Checkbox exactBox = new Checkbox();
		exactBox.addClassNames(LumoUtility.Margin.Top.XLARGE, LumoUtility.Margin.Left.XLARGE, LumoUtility.Margin.Right.XLARGE);
		exactBox.setLabel("spezifisch");
		
		DatePicker expirePicker = new DatePicker("Datum");
		expirePicker.setMinWidth(400, Unit.PIXELS);
		expirePicker.setEnabled(false);
		
		if (associationSettings.getOnboardingTokenExpirationDate() != null) {
			expirePicker.setValue(associationSettings.getOnboardingTokenExpirationDate());
			exactBox.setValue(true);
			periodBox.setEnabled(false);
			periodBox.setValue(periodBox.getEmptyValue());
			expirePicker.setEnabled(true);
		}
		
		periodBox.addValueChangeListener(e -> {
			if(e.getValue() != periodBox.getEmptyValue()) {
				saveOnboardingSettingsButton.setVisible(true);
				saveOnboardingSettingsButton.setEnabled(true);
			}
		});
		
		expirePicker.addValueChangeListener(e -> {
			if (e.getValue() != null) {
				expirePicker.setInvalid(!e.getValue().isAfter(LocalDate.now()));
				saveOnboardingSettingsButton.setVisible(true);
				saveOnboardingSettingsButton.setEnabled(true);
			}
		});
		
		exactBox.addValueChangeListener(e -> {
			expirePicker.setEnabled(e.getValue());
			periodBox.setEnabled(!e.getValue());
			
			if(!e.getValue()) {
				expirePicker.clear();

				if(associationSettings.getOnboardingTokenExpirationTime() != null) {
					periodBox.setValue(associationSettings.getOnboardingTokenExpirationTime());
				} else {					
					periodBox.setValue(periodBox.getListDataView().getItem(0));
				}
			} else {
				periodBox.clear();
				
				if(associationSettings.getOnboardingTokenExpirationDate() != null) {
					expirePicker.setValue(associationSettings.getOnboardingTokenExpirationDate());
					
				}
			}
			saveOnboardingSettingsButton.setVisible(false);
			saveOnboardingSettingsButton.setEnabled(false);
		});
		
		SvgIcon svgIcon = LineAwesomeIcon.SAVE_SOLID.create();
		svgIcon.addClassNames("save-icon");
		
		saveOnboardingSettingsButton = new Button("speichern", svgIcon);
		saveOnboardingSettingsButton.setMinWidth(200, Unit.PIXELS);
		saveOnboardingSettingsButton.setMinHeight(50, Unit.PIXELS);
		saveOnboardingSettingsButton.addClickListener(e -> {
			
			if(exactBox.getValue()) {
				associationSettings.setOnboardingTokenExpirationDate(expirePicker.getValue());
				associationSettings.setOnboardingTokenExpirationTime(null);
			} else {
				associationSettings.setOnboardingTokenExpirationDate(null);
				associationSettings.setOnboardingTokenExpirationTime(periodBox.getValue());
			}
			
			associationSettings = associationSettingsService.update(associationSettings);
			
			Notification notification = Notification.show("Erfolgreich gespeichert.");
			notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
		});
		saveOnboardingSettingsButton.addClassNames("save-button", "extra-margin-right");
		saveOnboardingSettingsButton.setEnabled(false);
		saveOnboardingSettingsButton.setVisible(false);
		
		innerWrapper.add(periodBox, exactBox, expirePicker, saveOnboardingSettingsButton);
		tokenLengthWrapper.add(tokenLengthHeader, innerWrapper);
		return tokenLengthWrapper;
	}

	private Component createExportTab() {
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setHeight("100%");
		
		mainLayout.addClassNames(LumoUtility.JustifyContent.CENTER, LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
		H1 help = new H1("Work in progress.");

		StreamResource imageResource = new StreamResource("potteriepng.png",
				() -> getClass().getResourceAsStream("/potteriepng.png"));

		Avatar avatar = new Avatar("logo_club");
		avatar.setImageResource(imageResource);
		avatar.setHeight(250, Unit.PIXELS);
	    avatar.setWidth(250, Unit.PIXELS);
		
		mainLayout.add(avatar, help);
		return mainLayout;
	}

	private Component createImportTab() {
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setHeight("100%");
		mainLayout.setWidthFull();
		mainLayout.addClassName(LumoUtility.JustifyContent.CENTER);
		H1 help = new H1("Work in progress.");

		StreamResource imageResource = new StreamResource("potteriepng.png",
				() -> getClass().getResourceAsStream("/potteriepng.png"));

		Avatar avatar = new Avatar("logo_club");
		avatar.setImageResource(imageResource);
		avatar.setHeight(250, Unit.PIXELS);
	    avatar.setWidth(250, Unit.PIXELS);
		
		mainLayout.add(avatar, help);
		return mainLayout;
	}

	private Component createAuthorizationTab() {
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setHeight("100%");
		mainLayout.setWidthFull();
		mainLayout.addClassName(LumoUtility.JustifyContent.CENTER);
		H1 help = new H1("Work in progress.");

		StreamResource imageResource = new StreamResource("potteriepng.png",
				() -> getClass().getResourceAsStream("/potteriepng.png"));

		Avatar avatar = new Avatar("logo_club");
		avatar.setImageResource(imageResource);
		avatar.setHeight(250, Unit.PIXELS);
	    avatar.setWidth(250, Unit.PIXELS);
		
		mainLayout.add(avatar, help);
		return mainLayout;
	}

	private Component createLocationsTab() {
		
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setHeight("100%");
		mainLayout.addClassNames("grid-wrapper",LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
		addLocationDialog();
		
		HorizontalLayout buttonLayout = new HorizontalLayout();
		Button addLocationButton = new Button("Standort hinzufügen");
		addLocationButton.addClassName("button-category");
		addLocationButton.setIcon(LineAwesomeIcon.BUILDING_SOLID.create());
		
		addLocationButton.addClickListener(e -> {
			saveLocationButton.setText("erfassen");
			addLocationDialog.open();
		});
		
		buttonLayout.add(addLocationButton);
		
		locationsGrid.addColumn(e -> e.getName()).setHeader("Name").setAutoWidth(true);
		locationsGrid.addColumn(e -> e.getCity()).setHeader("Stadt").setAutoWidth(true);
		locationsGrid.addColumn(e -> e.getPostalCode()).setHeader("PLZ").setAutoWidth(true);
		locationsGrid.addColumn(e -> e.getStreet()).setHeader("Straße").setAutoWidth(true);
		locationsGrid.addColumn(e -> e.getStreetNumber()).setHeader("Hausnummer").setAutoWidth(true);
		locationsGrid.addColumn(e -> e.getNote()).setHeader("Bemerkung").setAutoWidth(true);
		
		locationsGrid.addComponentColumn(item -> {
			Button button = new Button("bearbeiten");
			button.addClassName("button-grid-green");
			button.addClickListener(click -> {
				saveLocationButton.setText("aktualisieren");
				clearLocationDialog();
				selectedLocation = item;				
				openDialogWithLocation();
				addLocationDialog.open();
			});

			return button;
		}).setAutoWidth(true);

		locationsGrid.setHeightFull();
		locationsGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
		refreshGrid(ViewStatus.LOCATION);
		
		mainLayout.add(buttonLayout, locationsGrid);
		
		return mainLayout;
	}

	private void openDialogWithLocation() {
		nameLocationField.setValue(selectedLocation.getName());
		streetLocationField.setValue(selectedLocation.getStreet());
		streetNumberLocationField.setValue(selectedLocation.getStreetNumber());
		postalCodeLocationField.setValue(String.valueOf(selectedLocation.getPostalCode()));
		cityLocationField.setValue(selectedLocation.getCity());
		noteLocationField.setValue(selectedLocation.getNote());
	}
	
	private void clearLocationDialog() {
		nameLocationField.setValue("");
		streetLocationField.setValue("");
		streetNumberLocationField.setValue("");
		postalCodeLocationField.setValue("");
		cityLocationField.setValue("");
		noteLocationField.setValue("");
	}

	private void refreshGrid(ViewStatus status) {
		if(status == ViewStatus.LOCATION) {			
			locationsGrid.setItems(locationService.findAllByAssociation(associationId));
		} else if(status == ViewStatus.WORKINGCATEGORY) {
			categoriesGrid.setItems(workingUnitCategoryService.findAllByAssociation(associationId));
		}
	}

	private void addLocationDialog() {
		addLocationDialog = new Dialog();
		H2 h2 = new H2("Standort");
		FormLayout formLayout = new FormLayout();
		
		postalCodeLocationField.setAllowedCharPattern("[0-9/]");
		
		formLayout.add(nameLocationField, streetLocationField, streetNumberLocationField, postalCodeLocationField, cityLocationField, noteLocationField);
		
		addLocationDialog.add(h2, new Hr(), formLayout);
		
		saveLocationButton.addClassName("save-button");
		saveLocationButton.addClickListener(e -> {
			addNewLocation();
			clearLocationDialog();
			addLocationDialog.close();
			refreshGrid(ViewStatus.LOCATION);
			selectedLocation = null;
		});
		
		Button cancelButton = new Button("abbrechen");
		cancelButton.addClassName("cancel-button");
		cancelButton.addClickListener(e -> {
			clearLocationDialog();
			addLocationDialog.close();
			selectedLocation = null;
		});
		
		Button deleteButton = new Button("löschen");
		deleteButton.addClassName("delete-button");
		deleteButton.addClickListener(e -> {
			deleteSelectedLocation();
			clearLocationDialog();
			addLocationDialog.close();
			selectedLocation = null;
			refreshGrid(ViewStatus.LOCATION);
		});
		
		addLocationDialog.getFooter().add(deleteButton, cancelButton, saveLocationButton);
	}

	private void deleteSelectedLocation() {
		List<Blossom> strainList = strainService.findAllByAssociation(associationId).stream().filter(e -> {
			if (e.getGrowLocation() != null) {
				return e.getGrowLocation().getId().equals(this.selectedLocation.getId());
			} else {
				return false;
			}
		}).toList();
		
		List<Seed> seedList = seedService.findAllByAssociation(associationId).stream().filter(e -> {
			if (e.getGrowLocation() != null) {
				return e.getGrowLocation().getId().equals(this.selectedLocation.getId());
			} else {
				return false;
			}
		}).toList();
		
		List<Cutting> cuttingList = cuttingService.findAllByAssociation(associationId).stream().filter(e -> {
			if (e.getGrowLocation() != null) {
				return e.getGrowLocation().getId().equals(this.selectedLocation.getId());
			} else {
				return false;
			}
		}).toList();
		
		strainList.forEach(e -> {
			e.setGrowLocation(null);
			strainService.update(e);
		});
		
		seedList.forEach(e -> {
			e.setGrowLocation(null);
			seedService.update(e);
		});
		
		cuttingList.forEach(e -> {
			e.setGrowLocation(null);
			cuttingService.update(e);
		});
		
		locationService.delete(selectedLocation.getId());
	}

	private void addNewLocation() {
		Location location;
		String notification;
		if(selectedLocation != null) {
			location = selectedLocation;
			notification = "Standort aktualisiert";
		} else {
			notification = "Neuer Standort hinzugefügt.";
			location = new Location();
		}
		
		location.setAssociationId(associationId);
		location.setCity(cityLocationField.getValue());
		location.setName(nameLocationField.getValue());
		location.setNote(noteLocationField.getValue());
		location.setPostalCode(Integer.valueOf(postalCodeLocationField.getValue()));
		location.setStreet(streetLocationField.getValue());
		location.setStreetNumber(streetNumberLocationField.getValue());
		
		locationService.update(location);
		Notification.show(notification);		
	}

	private Component createFinancesTab() {
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setHeight("100%");
		mainLayout.setWidth("100%");
		mainLayout.addClassNames(LumoUtility.JustifyContent.CENTER);
		H1 help = new H1("Work in progress.");

		StreamResource imageResource = new StreamResource("potteriepng.png",
				() -> getClass().getResourceAsStream("/potteriepng.png"));

		Avatar avatar = new Avatar("logo_club");
		avatar.setImageResource(imageResource);
		avatar.setHeight(250, Unit.PIXELS);
	    avatar.setWidth(250, Unit.PIXELS);
		
		mainLayout.add(avatar, help);
		return mainLayout;
	}

	private Component createWorkingEnvTab() {
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setHeight("100%");
		
		HorizontalLayout buttonLayout = new HorizontalLayout();
		Button addCategoryButton = new Button("Kategorie hinzufügen");
		addCategoryButton.addClassName("button-category");
		addCategoryButton.setIcon(LineAwesomeIcon.TAG_SOLID.create());
		
		addCategoryButton.addClickListener(e -> {
			saveCategoryButton.setText("hinzufügen");
			addCategoryDialog.open();
		});
		
		buttonLayout.add(addCategoryButton);
		
		categoriesGrid.addColumn(e -> e.getName()).setHeader("Name").setAutoWidth(true);
		
		categoriesGrid.addComponentColumn(item -> {
			Button button = new Button("bearbeiten");
			button.addClassName("button-grid-green");
			button.setMaxWidth("20%");
			button.addClickListener(click -> {
				saveCategoryButton.setText("aktualisieren");
				clearCategoryDialog();
				selectedCategory = item;
				openDialogWithCategory();
				addCategoryDialog.open();
			});
			return button;
		});
		
		categoriesGrid.setHeightFull();
		categoriesGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
		refreshGrid(ViewStatus.WORKINGCATEGORY);
		
		mainLayout.add(buttonLayout, new Hr(), categoriesGrid);
		
		return mainLayout;
	}

	private void openDialogWithCategory() {
		this.categoryNameField.setValue(selectedCategory.getName());
	}

	private void deleteWorkingUnitCategory() {
		
		List<WorkingUnit> workingUnitList = workingUnitService.findAllByAssociation(associationId).stream().filter(e -> {
			if(e.getCategory() != null) {
				return e.getCategory().getId().equals(selectedCategory.getId());
			} else {
				return false;
			}
		}).toList();
		
		Optional<WorkingUnitCategory> optional = workingUnitCategoryService.get(1L);
		
		optional.ifPresent(e -> {
			workingUnitList.forEach(w -> {
				w.setCategory(e);
				workingUnitService.update(w);
			});
		});
		
		workingUnitCategoryService.delete(selectedCategory.getId());
	}

	
	private void addCategoryDialog() {
		
		addCategoryDialog = new Dialog();

		H2 h2 = new H2("Kategorie");
		FormLayout formLayout = new FormLayout();
		
		formLayout.add(categoryNameField);
		
		addCategoryDialog.add(h2, new Hr(), formLayout);
		
		saveCategoryButton.addClassName("save-button");
		saveCategoryButton.addClickListener(e -> {
			if (categoryNameField.isEmpty()) {
				Notification.show("Die Kategorie braucht einen Namen!");
			} else {
				addNewCategory();
				clearCategoryDialog();
				addCategoryDialog.close();
				refreshGrid(ViewStatus.WORKINGCATEGORY);
				selectedCategory = null;
			}
		});
		
		Button cancelButton = new Button("abbrechen");
		cancelButton.addClassName("cancel-button");
		cancelButton.addClickListener(e -> {
			clearCategoryDialog();
			addCategoryDialog.close();
			selectedCategory = null;
		});
		
		Button deleteButton = new Button("löschen");
		deleteButton.addClassName("delete-button");
		deleteButton.addClickListener(e -> {
			deleteWorkingUnitCategory();
			clearCategoryDialog();
			addCategoryDialog.close();
			selectedCategory = null;
			refreshGrid(ViewStatus.WORKINGCATEGORY);
		});
		
		addCategoryDialog.getFooter().add(deleteButton, cancelButton, saveCategoryButton);	
	}

	private void addNewCategory() {
		WorkingUnitCategory newCategory;
		
		if(selectedCategory == null) {
			newCategory = new WorkingUnitCategory();
		} else {
			newCategory = selectedCategory;
		}
		
		newCategory.setAssociationId(associationId);
		newCategory.setName(categoryNameField.getValue());
		workingUnitCategoryService.update(newCategory);
	}

	private void clearCategoryDialog() {
		this.categoryNameField.setValue("");
	}

}
