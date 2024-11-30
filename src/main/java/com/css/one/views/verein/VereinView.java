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
import org.vaadin.olli.FileDownloadWrapper;

import com.css.one.data.Association;
import com.css.one.data.Person;
import com.css.one.data.Transaction;
import com.css.one.data.TransactionType;
import com.css.one.data.WorkingUnit;
import com.css.one.data.enums.AssociationRole;
import com.css.one.services.AssociationService;
import com.css.one.services.LocationService;
import com.css.one.services.PersonService;
import com.css.one.services.TransactionService;
import com.css.one.services.WorkingUnitService;
import com.css.one.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.TextField;
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

	List<Person> importantPeople;
	List<TextField> textFieldsNameOfDocument = new ArrayList<>();
	List<ComboBox<String>> formatComboBoxes = new ArrayList<>();
	List<String> formatTypes = Arrays.asList(".pdf", ".docx", ".txt");

	private Dialog downloadDialog;

	private File exportFile;

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
		tabSheet.add("Verantwortliche", createResponsiblesTab());
		tabSheet.add("Downloads", createDownloadsTab());
		tabSheet.add("Allgemeine Infos", createGeneralInfoTab());
		
		addClassNames(LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
		add(tabSheet);
	}

	private Component createGeneralInfoTab() {
		
		Div wrapper = new Div();
		wrapper.addClassName("grid-wrapper");
		wrapper.setHeight("100%");
		
		H2 h2General = new H2("Hauptsitz");
		h2General.addClassNames(LumoUtility.Margin.Top.MEDIUM);
		
		FormLayout formLayout = new FormLayout();

		TextField fieldAssociationName = new TextField("Name des Vereins");
		TextField fieldAssociationNumber = new TextField("Vereinsnummer");
		TextField fieldStreetName = new TextField("Straße");
		TextField fieldHouseNumber = new TextField("Hausnummer");
		TextField fieldPostalCode = new TextField("PLZ");
		TextField fieldCity = new TextField("Ort");

		fieldAssociationName.setEnabled(false);
		fieldAssociationNumber.setEnabled(false);
		fieldStreetName.setEnabled(false);
		fieldHouseNumber.setEnabled(false);
		fieldPostalCode.setEnabled(false);
		fieldCity.setEnabled(false);
		
		Optional<Association> optionalAssociation = associationService.get(Integer.toUnsignedLong(associationId));
		
		if (optionalAssociation.isPresent()) {
			fieldAssociationName.setValue(optionalAssociation.get().getName());
			fieldAssociationNumber.setValue(String.valueOf(optionalAssociation.get().getNumber()));
			fieldStreetName.setValue(optionalAssociation.get().getStreet());
			fieldHouseNumber.setValue(optionalAssociation.get().getStreetNumber());
			fieldPostalCode.setValue(String.valueOf(optionalAssociation.get().getPostalCode()));
			fieldCity.setValue(optionalAssociation.get().getCity());
		} else {
			fieldAssociationName.setValue("");
			fieldAssociationNumber.setValue("");
			fieldStreetName.setValue("");
			fieldHouseNumber.setValue("");
			fieldPostalCode.setValue("");
		}

		formLayout.add(fieldAssociationName, fieldAssociationNumber, fieldStreetName, fieldHouseNumber,
				fieldPostalCode, fieldCity);
		
		formLayout.setColspan(fieldAssociationName, 2);
		formLayout.setColspan(fieldAssociationNumber, 2);
		formLayout.setColspan(fieldStreetName, 2);
		formLayout.setColspan(fieldHouseNumber, 2);
		formLayout.setColspan(fieldPostalCode, 2);
		formLayout.setColspan(fieldCity, 2);
		
		
		wrapper.add(h2General, formLayout, new Hr(), createInnerTabsForLocations());
		
		return wrapper;
	}

	private TabSheet createInnerTabsForLocations() {
		TabSheet innerTabSheet = new TabSheet();
		innerTabSheet.addClassNames("vaadin-tabsheet");
		
		locationService.findAllByAssociation(associationId).forEach(e -> {
			FormLayout tmpLayout = new FormLayout();
			
			TextField nameField = new TextField("Name");
			nameField.setValue(e.getName());
			nameField.setEnabled(false);
			TextField addressField = new TextField("Adresse");
			addressField.setEnabled(false);
			addressField.setValue(e.getStreet() + " " + e.getStreetNumber() + ", " + e.getPostalCode() + " " + e.getCity());
			TextField noteField = new TextField("Notiz");
			noteField.setEnabled(false);
			noteField.setValue(e.getNote());
			
			tmpLayout.add(nameField, addressField, noteField);
			tmpLayout.setSizeFull();
			innerTabSheet.add(e.getName(), tmpLayout);
		});
		
		return innerTabSheet;
	}

	private Component createDownloadsTab() {
		Div wrapper = new Div();
		wrapper.addClassName("grid-wrapper");

		VerticalLayout mainLayout = new VerticalLayout();

		mainLayout.add(new H3("Mitglieder"));
		Hr hr1 = new Hr();
		hr1.setWidth(420, Unit.PIXELS);
		mainLayout.add(hr1);

		HorizontalLayout layerOne = new HorizontalLayout();
		setFirstLayer(layerOne);
		setSecondLayer(layerOne);
		mainLayout.add(layerOne);

		mainLayout.add(new H3("Waren"));
		Hr hr2 = new Hr();
		hr2.setWidth(420, Unit.PIXELS);
		mainLayout.add(hr2);

		HorizontalLayout layerThree = new HorizontalLayout();
		setThirdLayer(layerThree);
		mainLayout.add(layerThree);

		mainLayout.add(new H3("Finanzen"));
		Hr hr3 = new Hr();
		hr3.setWidth(420, Unit.PIXELS);
		mainLayout.add(hr3);

		HorizontalLayout layerFour = new HorizontalLayout();
		createFourthLayer(layerFour);
		mainLayout.add(layerFour);

		mainLayout.add(new H3("Allgemein"));
		Hr hr4 = new Hr();
		hr4.setWidth(420, Unit.PIXELS);
		mainLayout.add(hr4);

		HorizontalLayout layerGeneral = new HorizontalLayout();
		setGeneralLayer(layerGeneral);
		mainLayout.add(layerGeneral);

		wrapper.add(mainLayout);
		return wrapper;
	}

	private Component createResponsiblesTab() {
		VerticalLayout wrapper = new VerticalLayout();
		wrapper.addClassNames(LumoUtility.Padding.NONE, LumoUtility.BorderColor.CONTRAST_90, LumoUtility.Margin.NONE);
		
		importantPeople = samplePersonService.findAllByAssociation(associationId).stream()
				.filter(e -> e.getAssociationRole() != AssociationRole.MEMBER).toList();
		
		Grid<Person> responsiblesGrid = new Grid<>(Person.class, false);
		responsiblesGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
		responsiblesGrid.addColumn(p -> p.getFirstName() + " " + p.getLastName()).setAutoWidth(true).setHeader("Name");
		responsiblesGrid.addColumn(p -> p.getAssociationRole().getLabel()).setAutoWidth(true).setHeader("Rolle");
		responsiblesGrid.addColumn(p -> renderDate(p.getDateOfHigherRole())).setAutoWidth(true).setHeader("In der Funktion seit");
		responsiblesGrid.setItems(importantPeople);
		
		wrapper.add(responsiblesGrid);
		return wrapper;
	}

	private void setGeneralLayer(HorizontalLayout layerGeneral) {

		VerticalLayout mainButtonLayoutOne = new VerticalLayout();
		mainButtonLayoutOne.add(new Text(general)); 
		mainButtonLayoutOne.setAlignItems(Alignment.CENTER);
		Button buttonPrintGeneral = new Button(mainButtonLayoutOne);
		buttonPrintGeneral.addClassNames("button-category");
		buttonPrintGeneral.addClassNames(LumoUtility.Border.ALL, LumoUtility.BorderColor.PRIMARY_50);
		
		Dialog printDialogGeneral = new Dialog();
		initDialog(printDialogGeneral, general);
		buttonPrintGeneral.addClickListener(e -> {
			refreshDialogs();
			printDialogGeneral.open();
		});
		layerGeneral.add(buttonPrintGeneral);
	}

	private void setFirstLayer(HorizontalLayout layerOne) {

		VerticalLayout mainButtonLayoutOne = new VerticalLayout();
		mainButtonLayoutOne.add(new Text(memberList));
		mainButtonLayoutOne.setAlignItems(Alignment.CENTER);
		Button buttonPrintMemberList = new Button(mainButtonLayoutOne);
		buttonPrintMemberList.addClassNames("button-category");
		buttonPrintMemberList.addClassNames(LumoUtility.Border.ALL, LumoUtility.BorderColor.PRIMARY_50);
		
		Dialog printDialogMemberList = new Dialog();
		initDialog(printDialogMemberList, memberList);
		buttonPrintMemberList.addClickListener(e -> {
			refreshDialogs();
			printDialogMemberList.open();
		});

		VerticalLayout mainButtonLayoutTwo = new VerticalLayout();
		mainButtonLayoutTwo.add(new Text(workload));
		mainButtonLayoutTwo.setAlignItems(Alignment.CENTER);
		Button buttonPrintWorkload = new Button(mainButtonLayoutTwo);
		buttonPrintWorkload.addClassNames("button-category");
		buttonPrintWorkload.addClassNames(LumoUtility.Border.ALL, LumoUtility.BorderColor.PRIMARY_50);
		
		Dialog printDialogWorkload = new Dialog();
		initDialog(printDialogWorkload, workload);
		buttonPrintWorkload.addClickListener(e -> {
			refreshDialogs();
			printDialogWorkload.open();
		});

		layerOne.add(buttonPrintMemberList);
		layerOne.add(buttonPrintWorkload);
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

		dialogLayout.add(new H2(title + " downloaden"));
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

		Button cancelButton = new Button("Abbrechen", e -> printDialog.close());

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

		dialogLayout.add(new H2("Jetzt downloaden"));
		dialogLayout.add(new Hr());

		dialogLayout.add(new H1(name + format));
		downloadDialog.add(dialogLayout);

		Button saveButton = new Button("Download");
		Button cancelButton = new Button("Fertig", e -> {
			downloadDialog.close();

			if (exportFile != null) {
				if (exportFile.exists()) {
					exportFile.delete();
				}
			}
		});

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

	private void setSecondLayer(HorizontalLayout layerTwo) {

		VerticalLayout mainButtonLayoutOne = new VerticalLayout();
		mainButtonLayoutOne.add(new Text(importantMembers));
		mainButtonLayoutOne.setAlignItems(Alignment.CENTER);
		Button buttonImportantMembers = new Button(mainButtonLayoutOne);
		buttonImportantMembers.addClassNames("button-category");
		buttonImportantMembers.addClassNames(LumoUtility.Border.ALL, LumoUtility.BorderColor.PRIMARY_50);
		
		Dialog printDialogImportantMembers = new Dialog();
		initDialog(printDialogImportantMembers, "Liste von " + importantMembers + "n");
		buttonImportantMembers.addClickListener(e -> {
			refreshDialogs();
			printDialogImportantMembers.open();
		});

		VerticalLayout mainButtonLayoutTwo = new VerticalLayout();
		mainButtonLayoutTwo.add(new Text(waitingList));
		mainButtonLayoutTwo.setAlignItems(Alignment.CENTER);
		Button buttonPrintWaitingList = new Button(mainButtonLayoutTwo);
		buttonPrintWaitingList.addClassNames("button-category");
		buttonPrintWaitingList.addClassNames(LumoUtility.Border.ALL, LumoUtility.BorderColor.PRIMARY_50);
		
		Dialog printDialogWaitingList = new Dialog();
		initDialog(printDialogWaitingList, waitingList);
		buttonPrintWaitingList.addClickListener(e -> {
			refreshDialogs();
			printDialogWaitingList.open();
		});

		layerTwo.add(buttonImportantMembers);
		layerTwo.add(buttonPrintWaitingList);
	}

	private void setThirdLayer(HorizontalLayout layerThree) {

		VerticalLayout mainButtonLayoutOne = new VerticalLayout();
		mainButtonLayoutOne.add(new Text(wareInfo));
		mainButtonLayoutOne.setAlignItems(Alignment.CENTER);
		Button buttonPrintWareInfo = new Button(mainButtonLayoutOne);
		buttonPrintWareInfo.addClassNames("button-category");
		buttonPrintWareInfo.addClassNames(LumoUtility.Border.ALL, LumoUtility.BorderColor.PRIMARY_50);
		
		Dialog printDialogWareInfo = new Dialog();
		initDialog(printDialogWareInfo, wareInfo);
		buttonPrintWareInfo.addClickListener(e -> {
			refreshDialogs();
			printDialogWareInfo.open();
		});

		VerticalLayout mainButtonLayoutTwo = new VerticalLayout();
		mainButtonLayoutTwo.add(new Text(outputInfo));
		mainButtonLayoutTwo.setAlignItems(Alignment.CENTER);
		Button buttonPrintOutputInfo = new Button(mainButtonLayoutTwo);
		buttonPrintOutputInfo.addClassNames("button-category");
		buttonPrintOutputInfo.addClassNames(LumoUtility.Border.ALL, LumoUtility.BorderColor.PRIMARY_50);
		
		Dialog printDialogOutputInfo = new Dialog();
		initDialog(printDialogOutputInfo, outputInfo);
		buttonPrintOutputInfo.addClickListener(e -> {
			refreshDialogs();
			printDialogOutputInfo.open();
		});

		layerThree.add(buttonPrintWareInfo);
		layerThree.add(buttonPrintOutputInfo);
	}

	private void createFourthLayer(HorizontalLayout layerFour) {

		VerticalLayout mainButtonLayoutOne = new VerticalLayout();
		mainButtonLayoutOne.add(new Text(income));
		mainButtonLayoutOne.setAlignItems(Alignment.CENTER);
		Button buttonPrintIncome = new Button(mainButtonLayoutOne);
		buttonPrintIncome.addClassNames("button-category");
		buttonPrintIncome.addClassNames(LumoUtility.Border.ALL, LumoUtility.BorderColor.PRIMARY_50);
		
		Dialog printDialogIncome = new Dialog();
		initDialog(printDialogIncome, income);
		buttonPrintIncome.addClickListener(e -> {
			refreshDialogs();
			printDialogIncome.open();
		});

		VerticalLayout mainButtonLayoutTwo = new VerticalLayout();
		mainButtonLayoutTwo.add(new Text(costs));
		mainButtonLayoutTwo.setAlignItems(Alignment.CENTER);
		Button buttonPrintCosts = new Button(mainButtonLayoutTwo);
		buttonPrintCosts.addClassNames("button-category");
		buttonPrintCosts.addClassNames(LumoUtility.Border.ALL, LumoUtility.BorderColor.PRIMARY_50);
		
		Dialog printDialogCosts = new Dialog();
		initDialog(printDialogCosts, costs);
		buttonPrintCosts.addClickListener(e -> {
			refreshDialogs();
			printDialogCosts.open();
		});

		layerFour.add(buttonPrintIncome);
		layerFour.add(buttonPrintCosts);
	}

	private void refreshDialogs() {
		textFieldsNameOfDocument.forEach(e -> e.setValue(""));
		formatComboBoxes.forEach(e -> e.setValue(formatTypes.iterator().next()));
	}

}
