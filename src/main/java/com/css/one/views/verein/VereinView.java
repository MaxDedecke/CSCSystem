package com.css.one.views.verein;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.docx4j.Docx4J;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.vaadin.lineawesome.LineAwesomeIcon;
import org.vaadin.olli.FileDownloadWrapper;

import com.css.one.data.Association;
import com.css.one.data.Location;
import com.css.one.data.Person;
import com.css.one.data.Transaction;
import com.css.one.data.WorkingUnit;
import com.css.one.data.enums.AssociationRole;
import com.css.one.data.enums.TransactionType;
import com.css.one.services.AssociationService;
import com.css.one.services.LocationService;
import com.css.one.services.PersonService;
import com.css.one.services.TransactionService;
import com.css.one.services.WorkingUnitService;
import com.css.one.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.PermitAll;

@PageTitle("Verein")
@Route(value = "verein", layout = MainLayout.class)
@PermitAll
public class VereinView extends Div {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1953468178819136512L;

	private int associationId;
	
	private TabSheet tabSheet = new TabSheet();

	private AssociationService associationService;
	private PersonService samplePersonService;
	private WorkingUnitService workingUnitService;
	private TransactionService transactionService;
	private LocationService locationService;
	
	final String memberList = "Mitgliederliste";
	final String workload = "Arbeitsaufwand";
	final String general = "Montaliche Übersicht";
	final String importantMembers = "Verantwortliche";
	final String waitingList = "Warteliste";
	final String wareInfo = "Produkte";
	final String outputInfo = "Ausgabeliste";
	final String income = "Monatliche Einnahmen";
	final String costs = "Monatliche Kosten";
	
	private TextField fieldAssociationName = new TextField("Name des Vereins");
	private TextField fieldAssociationNumber = new TextField("Vereinsnummer");
	private TextField fieldStreetName = new TextField("Straße");
	private TextField fieldHouseNumber = new TextField("Hausnummer");
	private TextField fieldPostalCode = new TextField("PLZ");
	private TextField fieldCity = new TextField("Ort");
	private TextField fieldEmail = new TextField("Email");

	private List<Person> importantPeople;
	private List<TextField> textFieldsNameOfDocument = new ArrayList<>();
	private List<ComboBox<String>> formatComboBoxes = new ArrayList<>();
	private List<String> formatTypes = Arrays.asList(".pdf", ".docx", ".txt");

	private Dialog downloadDialog;

	private File exportFile;
	private VirtualList<Location> virtualList = new VirtualList<>();
	private SvgIcon iconEdit = LineAwesomeIcon.PEN_SOLID.create();
	private boolean isOnEdit = false;

	public VereinView(AssociationService associationService, PersonService samplePersonService,
			WorkingUnitService workingUnitService, TransactionService transactionService, LocationService locationService) {
		this.associationService = associationService;
		this.samplePersonService = samplePersonService;
		this.workingUnitService = workingUnitService;
		this.transactionService = transactionService;
		this.locationService = locationService;
		
		addClassNames("verein-view");
		associationId = MainLayout.getAssociationId();
		
		tabSheet.setSizeFull();
		tabSheet.addClassNames(LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
		tabSheet.add("Allgemeine Infos", createGeneralInfoTab());
		tabSheet.add("Downloads", createDownloadsTab());
		
		addClassNames(LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
		add(tabSheet);
	}

	private Component createGeneralInfoTab() {
		
		//init layout
		VerticalLayout mainWrapper = new VerticalLayout();
		HorizontalLayout wrapper = new HorizontalLayout();
		VerticalLayout dataWrapper = new VerticalLayout();

		dataWrapper.addClassName("bestand-box");
		dataWrapper.setWidthFull();
		
		wrapper.addClassNames(LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
		
		wrapper.setWidthFull();
		wrapper.setHeightFull();
		
		mainWrapper.addClassNames(LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
		mainWrapper.setWidthFull();
		mainWrapper.setHeightFull();
		
		HorizontalLayout vereinHeaderWrapper = new HorizontalLayout();
		vereinHeaderWrapper.setWidthFull();
		H3 h2General = new H3("Daten des Vereins");
		h2General.addClassNames(LumoUtility.Margin.Top.MEDIUM);
		h2General.setMinWidth(200, Unit.PIXELS);
		
		VerticalLayout innerIconEditWrapper = new VerticalLayout();
		innerIconEditWrapper.setWidthFull();
		iconEdit.addClassNames("icon-edit");
		innerIconEditWrapper.add(iconEdit);
		
		iconEdit.addClickListener(e -> {
			
			if(isOnEdit) {
				//click when edit is active -> save data
				iconEdit.setSrc(LineAwesomeIcon.PEN_SOLID.create().getSrc());
				
				if(validateAssociationData()) {					
					saveAssociationData();
					isOnEdit = false;
					
					Notification notification = Notification.show("Daten des Vereins aktualisiert!");
					notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
				
				} else {
					Notification notification = Notification.show("Validierung der Daten des Vereins fehlgeschlagen!");
					notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
				}
				
			} else {
				//click when edit is not active -> edit data
				iconEdit.setSrc(LineAwesomeIcon.SAVE_SOLID.create().getSrc());
				isOnEdit = true;
			}
			
			refreshDataFieldsReadOnly(!isOnEdit);
			
		});
		
		vereinHeaderWrapper.add(h2General, innerIconEditWrapper);
		
		FormLayout formLayout = new FormLayout();

		//set read only
		refreshDataFieldsReadOnly(true);
		
		Optional<Association> optionalAssociation = associationService.get(Integer.toUnsignedLong(associationId));

		//assign values
		fieldAssociationName.setValue(optionalAssociation.isPresent() ? optionalAssociation.get().getName() : "");
		fieldAssociationNumber
				.setValue(optionalAssociation.isPresent() ? String.valueOf(optionalAssociation.get().getNumber()) : "");
		fieldStreetName.setValue(optionalAssociation.isPresent() ? optionalAssociation.get().getStreet() : "");
		fieldHouseNumber.setValue(optionalAssociation.isPresent() ? optionalAssociation.get().getStreetNumber() : "");
		fieldPostalCode.setValue(
				optionalAssociation.isPresent() ? String.valueOf(optionalAssociation.get().getPostalCode()) : "");
		fieldCity.setValue(optionalAssociation.isPresent() ? optionalAssociation.get().getCity() : "");
		fieldEmail.setValue(optionalAssociation.get().getEmail() != null ? optionalAssociation.get().getEmail() : "");

		formLayout.add(fieldAssociationName, fieldAssociationNumber, fieldStreetName, fieldHouseNumber,
				fieldPostalCode, fieldCity, fieldEmail);
		
		formLayout.setColspan(fieldStreetName, 2);
		formLayout.setColspan(fieldHouseNumber, 2);
		formLayout.setColspan(fieldPostalCode, 2);
		formLayout.setColspan(fieldCity, 2);
		formLayout.setColspan(fieldEmail, 2);
		
		formLayout.setResponsiveSteps(
		        // Use one column by default
		        new ResponsiveStep("0", 1),
		        // Use two columns, if layout's width exceeds 500px
		        new ResponsiveStep("500px", 2));

		dataWrapper.add(vereinHeaderWrapper, formLayout);
		wrapper.add(dataWrapper, createInnerTabsForLocations());
		mainWrapper.add(wrapper, createResponsiblesTab());
		
		return mainWrapper;
	}

	private boolean validateAssociationData() {
		
		if(fieldAssociationName.getValue().equals("")) {
			return false;
		}
		
		if(fieldStreetName.getValue().equals("")) {
			return false;
		}
		
		if(fieldCity.getValue().equals("")) {
			return false;
		}
		
		if(fieldHouseNumber.getValue().equals("")) {
			return false;
		}
		
		if(fieldPostalCode.getValue().equals("")) {
			return false;
		}
		
		return true;
	}

	private void saveAssociationData() {
				
		//get association
		Optional<Association> optAssociation = associationService.get(Integer.toUnsignedLong(this.associationId));
		
		//save if present
		optAssociation.ifPresentOrElse(association -> {
			
			association.setName(fieldAssociationName.getValue());
			association.setStreet(fieldStreetName.getValue());
			association.setCity(fieldCity.getValue());
			association.setEmail(fieldEmail.getValue());
			association.setPostalCode(Integer.valueOf(fieldPostalCode.getValue()));
			association.setStreetNumber(fieldHouseNumber.getValue());
			
			associationService.update(association);
		}, () -> {			
			Notification notification = Notification.show("Keinen Verein gefunden. Kontaktieren sie den Support!");
			notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
		});
		
	}

	private void refreshDataFieldsReadOnly(boolean readOnly) {
		fieldAssociationName.setReadOnly(readOnly);
		fieldStreetName.setReadOnly(readOnly);
		fieldHouseNumber.setReadOnly(readOnly);
		fieldPostalCode.setReadOnly(readOnly);
		fieldCity.setReadOnly(readOnly);
		fieldEmail.setReadOnly(readOnly);
		
		//alsways read only
		fieldAssociationNumber.setReadOnly(true);
	}

	private Component createInnerTabsForLocations() {
		VerticalLayout listWrapper = new VerticalLayout();
		
		H3 h2 = new H3("Standorte");
		h2.addClassNames(LumoUtility.Margin.Left.MEDIUM, LumoUtility.Margin.Top.MEDIUM);
		
		listWrapper.setWidthFull();

		listWrapper.addClassNames(LumoUtility.Padding.SMALL, "bestand-box");
		
		virtualList.setRenderer(new ComponentRenderer<>(entry -> {
        	EntryLayout entryLayout = new EntryLayout();
        	entryLayout.addClassName("diary-view-horizontal-layout-1");
        	entryLayout.setEntry(entry);
            return entryLayout;
        }));
		
		virtualList.addClassNames(LumoUtility.Padding.MEDIUM, LumoUtility.Margin.Top.NONE, "custom-scrollbar");
		virtualList.setItems(locationService.findAllByAssociation(associationId));

		listWrapper.add(h2, virtualList);
		
		return listWrapper;
	}

	private Component createDownloadsTab() {
		VerticalLayout wrapper = new VerticalLayout();
		VerticalLayout mainLayout = new VerticalLayout();
		
		//Member area downloads
		HorizontalLayout layerOne = new HorizontalLayout();
		layerOne.setHeightFull();
		layerOne.setWidthFull();
		
		layerOne.add(createMemberDownloads());
		layerOne.add(createBestandDownloads());
		
		mainLayout.add(layerOne);

		HorizontalLayout layerThree = new HorizontalLayout();
		layerThree.setHeightFull();
		layerThree.setWidthFull();
		
		layerThree.add(createFinanceDownloads());
		layerThree.add(createGeneralDownloads());
		mainLayout.add(layerThree);

		wrapper.add(mainLayout);
		return wrapper;
	}

	private Component createGeneralDownloads() {
		
		VerticalLayout generalDownloadsWrapper = new VerticalLayout();
		generalDownloadsWrapper.addClassNames("bestand-box", LumoUtility.AlignItems.CENTER);
		
		H3 h3 = new H3("Allgemein");
		
		Button buttonPrintGeneral = new Button("Monatliche Übersicht");
		buttonPrintGeneral.addClassNames("save-button");
		buttonPrintGeneral.setWidth(200, Unit.PIXELS);
		
		Dialog printDialogGeneral = new Dialog();
		initDialog(printDialogGeneral, general);
		buttonPrintGeneral.addClickListener(e -> {
			refreshDialogs();
			printDialogGeneral.open();
		});
		
		
		generalDownloadsWrapper.add(h3, buttonPrintGeneral);
		return generalDownloadsWrapper;
	}

	private Component createFinanceDownloads() {
		
		VerticalLayout financeDownloadsWrapper = new VerticalLayout();
		financeDownloadsWrapper.addClassNames("bestand-box", LumoUtility.AlignItems.CENTER);
		
		H3 h3 = new H3("Finanzen");

		Button buttonPrintIncome = new Button("Einnahmen");
		buttonPrintIncome.addClassNames("save-button");
		buttonPrintIncome.setWidth(200, Unit.PIXELS);
		
		Dialog printDialogIncome = new Dialog();
		initDialog(printDialogIncome, income);
		buttonPrintIncome.addClickListener(e -> {
			refreshDialogs();
			printDialogIncome.open();
		});
		
		Button buttonPrintCosts = new Button("Ausgaben");
		buttonPrintCosts.addClassNames("save-button");
		buttonPrintCosts.setWidth(200, Unit.PIXELS);
		
		Dialog printDialogCosts = new Dialog();
		initDialog(printDialogCosts, costs);
		buttonPrintCosts.addClickListener(e -> {
			refreshDialogs();
			printDialogCosts.open();
		});
		
		financeDownloadsWrapper.add(h3, buttonPrintIncome, buttonPrintCosts);
		
		return financeDownloadsWrapper;
	}

	private Component createBestandDownloads() {
		
		VerticalLayout wrapperBestandDownloads = new VerticalLayout();
		wrapperBestandDownloads.addClassNames("bestand-box", LumoUtility.AlignItems.CENTER);
		
		H3 h3 = new H3("Bestand und Abgabe");
		h3.addClassName("customheader");

		Button buttonPrintWareInfo = new Button("Produkte");
		buttonPrintWareInfo.addClassNames("save-button");
		buttonPrintWareInfo.setWidth(200, Unit.PIXELS);
		
		Dialog printDialogWareInfo = new Dialog();
		initDialog(printDialogWareInfo, wareInfo);
		buttonPrintWareInfo.addClickListener(e -> {
			refreshDialogs();
			printDialogWareInfo.open();
		});

		Button buttonPrintOutputInfo = new Button("Abgaben");
		buttonPrintOutputInfo.addClassNames("save-button");
		buttonPrintOutputInfo.setWidth(200, Unit.PIXELS);
		
		Dialog printDialogOutputInfo = new Dialog();
		initDialog(printDialogOutputInfo, outputInfo);
		buttonPrintOutputInfo.addClickListener(e -> {
			refreshDialogs();
			printDialogOutputInfo.open();
		});

		wrapperBestandDownloads.add(h3, buttonPrintWareInfo, buttonPrintOutputInfo);
		return wrapperBestandDownloads;
	}

	private Component createMemberDownloads() {
		
		VerticalLayout memberLayout = new VerticalLayout();
		memberLayout.addClassNames("bestand-box", LumoUtility.AlignItems.CENTER);
		
		H3 h3 = new H3("Mitglieder und Warteliste");
		h3.addClassName("customheader");
			
		Button buttonPrintMemberList = new Button("Mitgliederliste");
		buttonPrintMemberList.addClassNames("save-button");
		buttonPrintMemberList.setWidth(200, Unit.PIXELS);
		
		Dialog printDialogMemberList = new Dialog();
		initDialog(printDialogMemberList, memberList);
		buttonPrintMemberList.addClickListener(e -> {
			refreshDialogs();
			printDialogMemberList.open();
		});
		
		Button buttonPrintWorkload = new Button("Arbeitszeiten");
		buttonPrintWorkload.addClassNames("save-button");
		buttonPrintWorkload.setWidth(200, Unit.PIXELS);
		
		Dialog printDialogWorkload = new Dialog();
		initDialog(printDialogWorkload, workload);
		buttonPrintWorkload.addClickListener(e -> {
			refreshDialogs();
			printDialogWorkload.open();
		});
		
		Button buttonImportantMembers = new Button("Verantwortliche");
		buttonImportantMembers.addClassNames("save-button");
		buttonImportantMembers.setWidth(200, Unit.PIXELS);
		
		Dialog printDialogImportantMembers = new Dialog();
		initDialog(printDialogImportantMembers, "Liste von " + importantMembers + "n");
		buttonImportantMembers.addClickListener(e -> {
			refreshDialogs();
			printDialogImportantMembers.open();
		});

		Button buttonPrintWaitingList = new Button("Warteliste");
		buttonPrintWaitingList.addClassNames("save-button");
		buttonPrintWaitingList.setWidth(200, Unit.PIXELS);
		
		Dialog printDialogWaitingList = new Dialog();
		initDialog(printDialogWaitingList, waitingList);
		buttonPrintWaitingList.addClickListener(e -> {
			refreshDialogs();
			printDialogWaitingList.open();
		});
		
		memberLayout.add(h3, buttonPrintMemberList, buttonPrintWorkload, buttonImportantMembers, buttonPrintWaitingList);
		
		return memberLayout;
	}

	private Component createResponsiblesTab() {
		HorizontalLayout mainWrapper = new HorizontalLayout();
		mainWrapper.setWidthFull();
		mainWrapper.setMinHeight(250, Unit.PIXELS);
		mainWrapper.addClassNames(LumoUtility.Padding.Bottom.MEDIUM);
		
		VerticalLayout wrapper = new VerticalLayout();
		wrapper.addClassNames(LumoUtility.Margin.Top.MEDIUM, "bestand-box");
		
		wrapper.setWidthFull();
		
		H3 responsibles = new H3("Verantwortliche");
		responsibles.addClassNames(LumoUtility.Margin.Top.MEDIUM, LumoUtility.Margin.Left.SMALL);
		
		importantPeople = samplePersonService.findAllByAssociation(associationId).stream()
				.filter(e -> e.getAssociationRole() != AssociationRole.MEMBER).toList();
		
		Grid<Person> responsiblesGrid = new Grid<>(Person.class, false);
		responsiblesGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
		responsiblesGrid.addColumn(p -> p.getFirstName() + " " + p.getLastName()).setAutoWidth(true);
		responsiblesGrid.addColumn(p -> p.getAssociationRole().getLabel()).setAutoWidth(true);
		responsiblesGrid.addColumn(p -> "In der Funktion seit " + renderDate(p.getDateOfHigherRole()));
		responsiblesGrid.setItems(importantPeople);

		responsiblesGrid.addClassName("custom-scrollbar");
		
		wrapper.add(responsibles, responsiblesGrid);
		mainWrapper.add(wrapper);
		return mainWrapper;
	}
	
	private String renderDate(LocalDate date) {

		if (date != null) {
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
		return "";
	}

	private void initDialog(Dialog printDialog, String title) {

		VerticalLayout dialogLayout = new VerticalLayout();

		H2 h2 = new H2(title + " downloaden");
		h2.addClassName("customheader");
		
		dialogLayout.add(h2);
		dialogLayout.add(new Hr());

		FormLayout formLayout = new FormLayout();

		TextField nameOfDocument = new TextField("Dateiname");
		textFieldsNameOfDocument.add(nameOfDocument);

		ComboBox<String> comboBox = new ComboBox<String>("Dateiformat");
		formatComboBoxes.add(comboBox);

		comboBox.setItems(formatTypes);
		comboBox.setValue(formatTypes.iterator().next());

		ComboBox<String> comboBoxMonths = new ComboBox<String>("Für Monat");
		if (title.equals(income) || title.equals(costs) || title.equals(general)) {
			List<String> months = Arrays.asList("Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August",
					"September", "November", "Dezember");
			comboBoxMonths.setItems(months);
			formLayout.add(nameOfDocument, comboBox, comboBoxMonths);

		} else {
			formLayout.add(nameOfDocument, comboBox);
		}
		dialogLayout.add(formLayout);

		printDialog.add(dialogLayout);

		Button saveButton = new Button("Weiter", e -> {

			if (!nameOfDocument.isEmpty()) {
				if (title.equals(income) || title.equals(costs) || title.equals(general)) {
					if (comboBoxMonths.getValue() != null) {
						downloadPopup(nameOfDocument.getValue(), comboBox.getValue(), createContentForDownload(title));
						printDialog.close();
					} else {
						Notification.show("Es muss noch der Monat ausgewählt werden!");
					}
				} else {
					downloadPopup(nameOfDocument.getValue(), comboBox.getValue(), createContentForDownload(title));
					printDialog.close();
				}
			} else {
				Notification.show("Die Datei braucht noch einen Namen!");
			}
		});
		saveButton.addClassName("save-button");

		Button cancelButton = new Button("Abbrechen", e -> printDialog.close());
		cancelButton.addClassName("cancel-button");
		
		printDialog.getFooter().add(cancelButton);
		printDialog.getFooter().add(saveButton);
	}

	private String createContentForDownload(String typeOfContent) {

		switch (typeOfContent) {
		case memberList: {

			List<Person> allByAssociation = samplePersonService.findAllByAssociation(associationId);
			StringBuilder builder = new StringBuilder();
			int counter = 1;
			builder.append(typeOfContent);
			builder.append("\n");

			for (Person person : allByAssociation) {

				builder.append(String.valueOf(counter) + ".: ");
				builder.append(person.getFirstName() + " ");
				builder.append(person.getLastName() + ", ");
				builder.append(person.getEmail() + ", ");
				builder.append(person.getPhone() + ", ");
				builder.append(person.getDateOfBirth() + ", ");
				builder.append(person.getAssociationRole().getLabel() + ", ");
//TODO				builder.append("dabei seit: " + person.getDateOfRegistration());
				builder.append("\n");
				counter = counter + 1;
			}
			return builder.toString();
		}
		case workload: {

			List<WorkingUnit> allByAssociation = workingUnitService.findAllByAssociation(associationId);
			StringBuilder builder = new StringBuilder();
			builder.append(typeOfContent);
			builder.append("\n");

			for (WorkingUnit unit : allByAssociation) {

				builder.append(unit.getPersonName() + ", ");
				builder.append(unit.getCategory() + ", ");
				builder.append("Arbeitszeit: " + unit.getWorkingHours() + " Minuten, ");
				builder.append("Notiz: " + unit.getNote());
				builder.append("\n");
			}

			return builder.toString();
		}
		case "Liste von " + importantMembers + "n": {

			StringBuilder builder = new StringBuilder();
			int counter = 1;
			builder.append(typeOfContent);
			builder.append("\n");

			for (Person person : importantPeople) {

				builder.append(String.valueOf(counter) + ".: ");
				builder.append(person.getFirstName() + " ");
				builder.append(person.getLastName() + ", ");
				builder.append(person.getEmail() + ", ");
				builder.append(person.getPhone() + ", ");
				builder.append(person.getDateOfBirth() + ", ");
				builder.append(person.getAssociationRole().getLabel() + ", ");
				builder.append("In der Position seit: " + person.getDateOfHigherRole());
				builder.append("\n");
				counter = counter + 1;
			}
			return builder.toString();
		}
		case waitingList: {

			// TODO Still needs to be implemented
			StringBuilder builder = new StringBuilder();
			return builder.toString();
		}
		case wareInfo: {

			// TODO Still needs to be implemented
			StringBuilder builder = new StringBuilder();
			return builder.toString();
		}
		case outputInfo: {

			// TODO Still needs to be implemented
			StringBuilder builder = new StringBuilder();
			return builder.toString();
		}

		case income: {

			// TODO Still needs to be implemented
			StringBuilder builder = new StringBuilder();
			List<Transaction> incomeTransactions = transactionService.findAllByAssociation(associationId).stream()
					.filter(e -> e.getType() == TransactionType.INCOME).collect(Collectors.toList());

			builder.append(typeOfContent);
			builder.append("\n");

			for (Transaction transaction : incomeTransactions) {

				builder.append("Datum: " + transaction.getDateOfTransaction() + ", ");
				builder.append("Betrag: " + transaction.getAmount() + "€, ");
				builder.append(transaction.getNote());

			}

			return builder.toString();
		}

		case costs: {

			// TODO Still needs to be implemented
			StringBuilder builder = new StringBuilder();
			List<Transaction> costTransactions = transactionService.findAllByAssociation(associationId).stream()
					.filter(e -> e.getType() == TransactionType.COST).collect(Collectors.toList());

			builder.append(typeOfContent);
			builder.append("\n");

			for (Transaction transaction : costTransactions) {

				builder.append("Datum: " + transaction.getDateOfTransaction() + ", ");
				builder.append("Betrag: " + transaction.getAmount() + "€, ");
				builder.append(transaction.getNote());

			}

			return builder.toString();
		}
		case general: {

			// TODO Still needs to be implemented

			StringBuilder builder = new StringBuilder();

			builder.append(typeOfContent);
			builder.append("\n");

			builder.append("Monatliche Ernte: ");
			builder.append("\n");

			builder.append("Monatliche Warenausgabe: ");
			builder.append("\n");

			builder.append("Monatliche Finanzen");
			builder.append("\n");

			builder.append("Einnahmen: ");
			builder.append("\n");

			builder.append("Ausgaben: ");
			builder.append("\n");

			builder.append("Monatliche Arbeiten: ");
			builder.append("\n");

			return builder.toString();
		}
		default:
			return "";
		}
	}

	private void downloadPopup(String name, String format, String content) {

		downloadDialog = new Dialog();

		VerticalLayout dialogLayout = new VerticalLayout();
		
		H2 h2 = new H2("Jetzt downloaden");
		h2.addClassName("customheader");
		
		dialogLayout.add(h2);
		dialogLayout.add(new Hr());

		dialogLayout.add(new H1(name + format));
		downloadDialog.add(dialogLayout);

		Button saveButton = new Button("Download");
		saveButton.addClassName("save-button");
		Button cancelButton = new Button("Fertig", e -> {
			downloadDialog.close();

			if (exportFile != null) {
				if (exportFile.exists()) {
					exportFile.delete();
				}
			}
		});
		
		cancelButton.addClassName("cancel-button");

		if (format.equals(".txt")) {

			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content.getBytes());
			FileDownloadWrapper buttonWrapper = new FileDownloadWrapper(
					new StreamResource(name + format, () -> byteArrayInputStream));

			buttonWrapper.wrapComponent(saveButton);

			downloadDialog.getFooter().add(cancelButton);
			downloadDialog.getFooter().add(buttonWrapper);

			downloadDialog.open();
			downloadDialog.addDialogCloseActionListener(e -> {
				try {
					byteArrayInputStream.close();
					exportFile.delete();
				} catch (IOException e1) {
					exportFile.delete();
					e1.printStackTrace();
				}
			});

			cancelButton.addClickListener(e -> {
				downloadDialog.close();
				try {
					byteArrayInputStream.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (exportFile != null) {
					if (exportFile.exists()) {
						exportFile.delete();
					}
				}
			});
		}

		if (format.equals(".pdf")) {
			try {
				WordprocessingMLPackage wordPackage = WordprocessingMLPackage.createPackage();
				MainDocumentPart mainDocumentPart = wordPackage.getMainDocumentPart();
				mainDocumentPart.addParagraphOfText(content);
				File exportFile = new File(name + format);
				wordPackage.save(exportFile);

				FileOutputStream stream = new FileOutputStream(exportFile);
				Docx4J.toPDF(wordPackage, stream);
				stream.close();

				FileInputStream downloadStream = new FileInputStream(exportFile);

				FileDownloadWrapper buttonWrapper = new FileDownloadWrapper(new StreamResource(name + format, () -> {

					return downloadStream;

				}));
				buttonWrapper.wrapComponent(saveButton);

				downloadDialog.getFooter().add(cancelButton);
				downloadDialog.getFooter().add(buttonWrapper);

				downloadDialog.open();

				downloadDialog.addDialogCloseActionListener(e -> {
					try {
						downloadStream.close();
						exportFile.delete();
					} catch (IOException e1) {
						exportFile.delete();
						e1.printStackTrace();
					}
				});

				cancelButton.addClickListener(e -> {
					downloadDialog.close();
					try {
						downloadStream.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if (exportFile != null) {
						if (exportFile.exists()) {
							exportFile.delete();
						}
					}
				});
			} catch (Docx4JException | IOException e) {
				Notification.show("Download momentan nicht möglich!");
			}
		}

		if (format.equals(".docx")) {
			try {

				WordprocessingMLPackage wordPackage = WordprocessingMLPackage.createPackage();
				MainDocumentPart mainDocumentPart = wordPackage.getMainDocumentPart();
				mainDocumentPart.addParagraphOfText(content);
				File exportFile = new File(name + format);
				wordPackage.save(exportFile);

				FileInputStream stream = new FileInputStream(exportFile);
				FileDownloadWrapper buttonWrapper = new FileDownloadWrapper(new StreamResource(name + format, () -> {

					return stream;

				}));
				buttonWrapper.wrapComponent(saveButton);

				downloadDialog.getFooter().add(cancelButton);
				downloadDialog.getFooter().add(buttonWrapper);

				downloadDialog.open();
				downloadDialog.addDialogCloseActionListener(e -> {

					try {
						stream.close();
						exportFile.delete();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						exportFile.delete();
					}
				});

				cancelButton.addClickListener(e -> {
					downloadDialog.close();
					try {
						stream.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if (exportFile != null) {
						if (exportFile.exists()) {
							exportFile.delete();
						}
					}
				});

			} catch (Docx4JException | FileNotFoundException e) {
				Notification.show("Download momentan nicht möglich!");
			}
		}
	}

	private void refreshDialogs() {
		textFieldsNameOfDocument.forEach(e -> e.setValue(""));
		formatComboBoxes.forEach(e -> e.setValue(formatTypes.iterator().next()));
	}
	
	public class EntryLayout extends HorizontalLayout {

		private static final long serialVersionUID = 8080325977391846535L;
		private TextArea textField;
	    private H3 nameField;
	    private H3 streetNameAndNumber;
	    private H3 postalCodeAndCity;

	    private SvgIcon avatar;
	    private VerticalLayout innerLayout;
	    
	    public EntryLayout() {
			nameField = new H3("");

			nameField.addClassNames("diary-view-h3-1");
			textField = new TextArea();
			textField.setWidthFull();
			textField.setReadOnly(true);
			textField.addClassNames("textarea-border-invisible");
			streetNameAndNumber = new H3("");
			postalCodeAndCity = new H3("");
			
			avatar = LineAwesomeIcon.HOME_SOLID.create();
            avatar.getElement().setAttribute("tabindex", "-1");
            avatar.addClassNames( LumoUtility.Margin.Right.LARGE);
            
            
	        setWidthFull();
	        addClassNames("diary-view-horizontal-layout-1", LumoUtility.Padding.MEDIUM);
	        
	        innerLayout = new VerticalLayout();
	        innerLayout.setMinHeight(100, Unit.PIXELS);
	        
	        innerLayout.add(nameField, streetNameAndNumber, postalCodeAndCity, textField);
	        
	        VerticalLayout avatarLayout = new VerticalLayout();
	        avatarLayout.addClassName("diary-view-vertical-layout-2");
	        avatarLayout.add(avatar);
	        
	        
	        add(avatarLayout, innerLayout);
	    }

	    public void setEntry(Location entry) {
	    	//set values 
	        textField.setValue(entry.getNote());
	        nameField.setText(entry.getName());	        
	        streetNameAndNumber.setText(entry.getStreet() + " " + entry.getStreetNumber());
	        postalCodeAndCity.setText(String.valueOf(entry.getPostalCode()) + " " + entry.getCity());	    }
	}

}
