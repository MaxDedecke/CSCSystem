package com.css.one.views.übersicht;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.orm.ObjectOptimisticLockingFailureException;

import com.css.one.data.Output;
import com.css.one.data.OutputType;
import com.css.one.data.PaymentMethod;
import com.css.one.data.Person;
import com.css.one.data.Strain;
import com.css.one.data.Transaction;
import com.css.one.data.TransactionType;
import com.css.one.data.WorkingUnit;
import com.css.one.data.WorkingUnitCategory;
import com.css.one.services.OutputService;
import com.css.one.services.PersonService;
import com.css.one.services.StrainService;
import com.css.one.services.TransactionService;
import com.css.one.services.WorkingUnitCategoryService;
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
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.PermitAll;

@PageTitle("Übersicht")
@Route(value = "", layout = MainLayout.class)
@RouteAlias(value = "uebersicht/", layout = MainLayout.class)
@PermitAll
public class ÜbersichtView extends Div {

    private static final long serialVersionUID = 7776014341101416897L;
    
    private H1 currentDateText;
    
    private VerticalLayout layoutSearchMembers = new VerticalLayout();
    private VerticalLayout layoutSearchStrains = new VerticalLayout();
    private VerticalLayout layoutNews = new VerticalLayout();

    private HorizontalLayout layoutCurrentDate = new HorizontalLayout();

    private ComboBox<Person> searchMemberBox = new ComboBox<>();
    private ComboBox<Strain> searchStrainBox = new ComboBox<>();
    private ComboBox<Strain> searchStrainOutputBox = new ComboBox<>("Sorte");
    private ComboBox<OutputType> outputTypeBox = new ComboBox<>("Art");
    private ComboBox<PaymentMethod> paymentMethodBox;
    
    private PersonService personService;
    private StrainService strainService;
    private OutputService outputService; 
    private WorkingUnitService workingUnitService;
    private TransactionService transactionService;
    private WorkingUnitCategoryService workingUnitCategoryService;
    private ComboBox<WorkingUnitCategory> box;
    
    private Button buttonOpenPersonInfo = new Button("Info");
    private Button buttonOpenStrainInfo = new Button("Info");
    private Button buttonBookOutput = new Button("Abgabe");
    private Button buttonWorkingUnit = new Button("Zeit stempeln");
    
    Button buttonStopWorkingUnit = new Button("ausstempeln");
    Button buttonStartWorkingUnit = new Button("einstempeln");
    
    private Text durationWorkingUnit = new Text("-");
    
    private Person selectedMember; 
    private Dialog memberInfoDialog = new Dialog();
    private TextField memberNameField = new TextField("Name");
    private TextField dateOutputField = new TextField("Datum");

    Grid<Strain> outputMemberGrid = new Grid<>();
    Grid<String> newsGrid = new Grid<>();
    
    Text workloudMember = new Text("");
    
    private Dialog strainInfoDialog = new Dialog();
    private Dialog bookOutputDialog = new Dialog();
    private Dialog startWorkingUnitDialog = new Dialog();

    private TextField strainNameField = new TextField("Name");
    private TextField strainStatusField = new TextField("Status");
    private TextField strainAmountField = new TextField("Verbleibende Menge");
    private TextField strainIngrediensThcField = new TextField("THC in %");
    private TextField outputMemberField = new TextField("Mitglied");
    private TextField billingTextField;
    private TextField amountField;
    
	private int associationId;

	private WorkingUnit workingUnit;
    
    public ÜbersichtView(PersonService personService, StrainService strainService, OutputService outputService, WorkingUnitService workingUnitService,
    				TransactionService transactionService, WorkingUnitCategoryService workingUnitCategoryService) {    	
    	this.personService = personService;
    	this.strainService = strainService;
    	this.outputService = outputService;
    	this.workingUnitService = workingUnitService;
    	this.transactionService = transactionService;
    	this.workingUnitCategoryService = workingUnitCategoryService;
    	
    	addClassNames("uebersicht-view");

		associationId = MainLayout.getAssociationId();
		createCurrentDateLayout();
		
		createSearchMemberLayout();
		createSearchStrainLayout();
		createNewsLayout();
		
		Hr hr1 = new Hr();
		hr1.addClassName(LumoUtility.Margin.SMALL);
		
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setWidth("100%");
		HorizontalLayout firstLayerLayout = new HorizontalLayout();
		firstLayerLayout.add(layoutSearchMembers, layoutSearchStrains);
		firstLayerLayout.setWidth("100%");
		
		mainLayout.add(firstLayerLayout);
		
		HorizontalLayout secondLayerLayout = new HorizontalLayout(); 
		secondLayerLayout.add(layoutNews);
		secondLayerLayout.setWidth("100%");
		
		mainLayout.add(secondLayerLayout);
        add(layoutCurrentDate, hr1, mainLayout);
    }

	private void createNewsLayout() {		
		newsGrid.addColumn(e -> e).setAutoWidth(true);
		newsGrid.setItems(Arrays.asList("Keine Neuigkeiten."));
		newsGrid.setHeight(200, Unit.PIXELS);
		newsGrid.addClassNames(LumoUtility.Border.ALL ,LumoUtility.BorderRadius.LARGE, LumoUtility.BorderColor.PRIMARY_10, LumoUtility.Padding.NONE);
		layoutNews.addClassNames(LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
		
		layoutNews.add(newsGrid);
	}

	private void createCurrentDateLayout() {
    	
    	layoutCurrentDate.addClassNames(LumoUtility.AlignContent.CENTER,
    			LumoUtility.JustifyContent.CENTER, LumoUtility.Margin.Top.LARGE, LumoUtility.Margin.Bottom.SMALL, LumoUtility.BorderRadius.LARGE); 
    	
		LocalDate now = LocalDate.now();
		currentDateText = new H1(convertDayOfWeek(now.getDayOfWeek()) + ", der " + 
				now.getDayOfMonth() + "." + now.getMonth().getValue() + "." + now.getYear());
		
		layoutCurrentDate.add(currentDateText);
    }
    
    private void createSearchMemberLayout() {
    	
    	layoutSearchMembers.addClassNames("vaadin-horizontal-layout", LumoUtility.AlignContent.CENTER,
    			LumoUtility.Border.ALL, LumoUtility.BorderColor.PRIMARY_10, 
    			LumoUtility.JustifyContent.CENTER, 
    			LumoUtility.Margin.Top.SMALL, LumoUtility.BorderRadius.LARGE); 
    	 
    	HorizontalLayout headerLayout = new HorizontalLayout();
    	H1 headerSearch = new H1("Mitgliedersuche");
    	headerSearch.addClassNames(LumoUtility.Margin.MEDIUM);
    	headerLayout.add(headerSearch);
    	headerLayout.addClassNames(LumoUtility.JustifyContent.CENTER);
    	headerLayout.setWidth("100%");
		
		buttonOpenPersonInfo.setMinWidth("20%");
    	buttonWorkingUnit.setMinWidth("20%");
		buttonBookOutput.setMinWidth("20%");
		
    	VerticalLayout mainLayout = new VerticalLayout();
    	HorizontalLayout boxLayout = new HorizontalLayout();
    	
    	boxLayout.setWidth("100%");
    	boxLayout.addClassNames(LumoUtility.JustifyContent.CENTER);
    	
    	this.searchMemberBox.setItems(personService.findAllByAssociation(associationId));
    	this.searchMemberBox.setItemLabelGenerator(e -> e.getFirstName() + " " + e.getLastName());
    	this.searchMemberBox.setHeight(75, Unit.PIXELS);
    	this.searchMemberBox.setWidth("100%");
    	
    	createMemberInfoDialogContent();
    	createStartWorkingDialogContent();
    	createBookOutputDialogContent();
    	
    	this.searchMemberBox.addValueChangeListener(e -> {
    		if(e.getValue() == null) {
    			buttonOpenPersonInfo.setEnabled(false);
    			buttonWorkingUnit.setEnabled(false);
    			buttonBookOutput.setEnabled(false);
    		} else {    			
    			buttonOpenPersonInfo.setEnabled(true);
    			buttonWorkingUnit.setEnabled(true);
    			buttonBookOutput.setEnabled(true);
    		}
    	});
    	
    	buttonOpenPersonInfo.addClassName("button-category");
    	buttonOpenPersonInfo.addClickListener(e -> {
    		initMemberInfoDialog(searchMemberBox.getValue());
    		memberInfoDialog.open();
    	});
    	
    	buttonWorkingUnit.addClassName("button-category");
    	buttonWorkingUnit.addClickListener(e -> {
    		initStartWorkDialog(searchMemberBox.getValue());
    		startWorkingUnitDialog.open();
    	});
    	
    	buttonBookOutput.addClassName("button-category");
    	buttonBookOutput.addClickListener(e -> {
    		initOutputDialog(searchMemberBox.getValue());
    		bookOutputDialog.open();
    	});
    	
    	buttonOpenPersonInfo.setEnabled(false);
    	buttonWorkingUnit.setEnabled(false);
		buttonBookOutput.setEnabled(false);
		
    	this.searchMemberBox.setClearButtonVisible(true);	
    	boxLayout.add(buttonOpenPersonInfo, buttonBookOutput, buttonWorkingUnit);
    	mainLayout.add(this.searchMemberBox, boxLayout);
    	
    	//Add margin top
    	headerSearch.addClassNames(LumoUtility.Margin.Top.LARGE);
    	searchMemberBox.addClassNames(LumoUtility.Padding.SMALL, LumoUtility.Margin.Top.LARGE);
    	buttonOpenPersonInfo.addClassNames(LumoUtility.Padding.LARGE,LumoUtility.Margin.Top.LARGE);
    	buttonWorkingUnit.addClassNames(LumoUtility.Padding.LARGE,LumoUtility.Margin.Top.LARGE);
		buttonBookOutput.addClassNames(LumoUtility.Padding.LARGE,LumoUtility.Margin.Top.LARGE);
		
		layoutSearchMembers.add(headerLayout, new Hr(), mainLayout);

    }

    private void createBookOutputDialogContent() {
    	
    	bookOutputDialog.add(addBookOutputLayoutForDialog());
    	Button bookButton = new Button("Buchen", e -> {
    		Output output = new Output();
    		output.setAssociationId(associationId);
    		output.setDate(LocalDate.now());
    		output.setNote("Schnellausgabe");
    		output.setOutdated(false);
    		output.setPersonId(this.selectedMember.getId().intValue());
    		if(this.searchStrainOutputBox.getValue() != null) {    			
    			output.setStrainId(this.searchStrainOutputBox.getValue().getId().intValue());
    			if(!amountField.getValue().isEmpty()) {
    				output.setAmount(Double.valueOf(amountField.getValue()));
    				
    				if(!billingTextField.isEmpty()) {    					
    					outputService.update(output);
    					bookTransaction(this.amountField, this.paymentMethodBox.getValue());
    					Notification.show("Neue Abgabe mit zugehöriger Transaktion erstellt!");
    					bookOutputDialog.close();
    				} else {
    					Notification.show("Ohne Preis pro Gramm kann keine Transaktion gebucht werden !");
    				}
    			} else {
    				Notification.show("Es muss die Menge angeben werden!");
    			}			
    		} else {
    			Notification.show("Es muss eine Sorte ausgewählt werden");
    		}
    	});
    	
    	
    	Button cancelButton = new Button("Zurück", e -> bookOutputDialog.close());
    	cancelButton.addClassName("cancel-button");
    	bookButton.addClassName("save-button");
    	bookOutputDialog.getFooter().add(cancelButton, bookButton);	
	}

	private void bookTransaction(TextField amountField, PaymentMethod value) {
		
		try {
			Transaction outputTransaction = new Transaction();
			outputTransaction.setNote("Schnellausgabe");
			outputTransaction.setType(TransactionType.INCOME);
			outputTransaction.setDateOfTransaction(LocalDate.now());
			outputTransaction.setAssociationId(associationId);
			outputTransaction.setPaymentMethod(paymentMethodBox.getValue());
		    outputTransaction.setMemberId(this.selectedMember.getId().intValue());
		    outputTransaction.setAmount(Double.valueOf(billingTextField.getValue()));

			transactionService.update(outputTransaction);				
		} catch (ObjectOptimisticLockingFailureException exception) {
			Notification n = Notification.show(
					"Error updating the data. Somebody else has updated the record while you were making changes.");
			n.setPosition(Position.MIDDLE);
			n.addThemeVariants(NotificationVariant.LUMO_ERROR);
		}
	}

	private Component addBookOutputLayoutForDialog() {
		
		VerticalLayout mainDialogLayout = new VerticalLayout();
    	mainDialogLayout.setWidth("100%");
    	mainDialogLayout.setMinWidth(400, Unit.PIXELS);
    	mainDialogLayout.addClassNames(LumoUtility.Margin.Bottom.LARGE);
    	H1 h1 = new H1("Abgabe");
    	
    	FormLayout outputInfoLayout = new FormLayout();
    	
    	dateOutputField.setEnabled(false);
    	outputMemberField.setEnabled(false);
    	
    	outputInfoLayout.add(dateOutputField);
    	
    	outputTypeBox.setItems(OutputType.values());
    	outputTypeBox.setItemLabelGenerator(e -> e.getLabel());
    	outputTypeBox.setWidthFull();
    	outputTypeBox.setValue(outputTypeBox.getListDataView().getItem(0));
    	outputTypeBox.addValueChangeListener(e -> {
    		//TODO Set items in searchStrainOutputBox depending on selected value
    	});
    	
    	searchStrainOutputBox.setItems(strainService.findAllReadyForOutput(associationId));
    	searchStrainOutputBox.setItemLabelGenerator(e -> e.getName() + " (" + e.getThc() + "%)");
    	searchStrainOutputBox.setWidth("100%"); 
		searchStrainOutputBox.addValueChangeListener(e -> {
			if (!amountField.isEmpty()) {
				Double endPrice = e.getValue().getPricePerGram() * Double.valueOf(amountField.getValue());
				billingTextField.setValue(String.valueOf(endPrice));
			}
		});
    	
    	amountField = new TextField("Menge in Gramm");
    	amountField.setAllowedCharPattern("[0-9/]");
    	amountField.setWidthFull();
    	amountField.addValueChangeListener(e -> {
    		if (!searchStrainOutputBox.isEmpty()) {
				Double endPrice = searchStrainOutputBox.getValue().getPricePerGram() * Double.valueOf(e.getValue());
				billingTextField.setValue(String.valueOf(endPrice));
			}
    	});
    	
    	paymentMethodBox = new ComboBox<PaymentMethod>("Zahlungsart");
    	paymentMethodBox.setItems(PaymentMethod.values());
    	paymentMethodBox.setItemLabelGenerator(e -> e.getLabel());
    	paymentMethodBox.setValue(paymentMethodBox.getListDataView().getItem(0));
    	paymentMethodBox.setWidthFull();
    	
    	billingTextField = new TextField("Zu zahlender Betrag in €");
    	billingTextField.setAllowedCharPattern("[0-9/]");
    	billingTextField.setWidthFull();
    	billingTextField.setEnabled(false);
    	
    	mainDialogLayout.add(h1, outputMemberField, outputInfoLayout, outputTypeBox, searchStrainOutputBox, amountField, paymentMethodBox, new Hr(), billingTextField);
    	
    	outputInfoLayout.setColspan(outputMemberField, 2);

    	return mainDialogLayout;
	}

	private void createSearchStrainLayout() {
    	layoutSearchStrains.addClassNames("vaadin-horizontal-layout", LumoUtility.AlignContent.CENTER,
    			LumoUtility.Border.ALL, LumoUtility.BorderColor.PRIMARY_10, LumoUtility.Margin.Top.SMALL,  
    			LumoUtility.BorderRadius.LARGE);
    	
    	HorizontalLayout headerLayout = new HorizontalLayout();
    	headerLayout.setWidth("100%");
    	H1 headerSearch = new H1("Bestandssuche");
    	headerLayout.addClassNames(LumoUtility.JustifyContent.CENTER, LumoUtility.Margin.Top.LARGE);
    	headerLayout.add(headerSearch);

    	buttonOpenStrainInfo.setEnabled(false);
    	buttonOpenStrainInfo.addClassName("button-category");
    	buttonOpenStrainInfo.setMinWidth("40%");

    	VerticalLayout mainLayout = new VerticalLayout();
    	HorizontalLayout boxLayout = new HorizontalLayout();
    	
    	boxLayout.setWidth("100%");
    	boxLayout.addClassNames(LumoUtility.Margin.Top.SMALL,LumoUtility.JustifyContent.CENTER);
    	
    	this.searchStrainBox.setItems(strainService.findAllByAssociation(associationId));
    	this.searchStrainBox.setItemLabelGenerator(e -> e.getName() + " (" + e.getThc() + "% THC)");
    	this.searchStrainBox.setHeight(75, Unit.PIXELS);
    	this.searchStrainBox.setWidth("100%");
    	
    	createStrainInfoDialogContent();
    	
    	this.searchStrainBox.addValueChangeListener(e -> {
    		if(e.getValue() == null) {
    			buttonOpenStrainInfo.setEnabled(false);
    		} else {    			
    			buttonOpenStrainInfo.setEnabled(true);
    		}
    	});
    	
    	buttonOpenStrainInfo.addClickListener(e -> {
    		initStrainInfoDialog(searchStrainBox.getValue());
    		strainInfoDialog.open();
    	});
    	
    	this.searchStrainBox.setClearButtonVisible(true);	
    	boxLayout.add(searchStrainBox, buttonOpenStrainInfo);
    	mainLayout.add(this.searchStrainBox, boxLayout);
    	
    	//Add margin top
    	headerSearch.addClassNames(LumoUtility.Margin.Top.LARGE);
    	searchStrainBox.addClassNames(LumoUtility.Padding.SMALL, LumoUtility.Margin.Top.LARGE);
    	buttonOpenStrainInfo.addClassNames(LumoUtility.Padding.LARGE,LumoUtility.Margin.Top.LARGE);
    	
		layoutSearchStrains.add(headerLayout, new Hr(), mainLayout);

    }
    
    private void createMemberInfoDialogContent() {    	
    	memberInfoDialog.add(addGeneralLayoutForDialog());
    	Button cancelButton = new Button("Zurück", e -> memberInfoDialog.close());
    	cancelButton.addClassNames("cancel-button");
    	memberInfoDialog.getFooter().add(cancelButton);
    }
    
    private void createStartWorkingDialogContent() {
    	
    	startWorkingUnitDialog.add(addWorkingUnitLayoutForDialog());
    	Button cancelButton = new Button("Zurück", e -> startWorkingUnitDialog.close());
    	cancelButton.addClassNames("cancel-button");
    	startWorkingUnitDialog.getFooter().add(cancelButton);
    }

    private Component addGeneralLayoutForDialog() {
    	VerticalLayout mainDialogLayout = new VerticalLayout();
    	mainDialogLayout.setWidth("100%");
    	mainDialogLayout.setMinWidth(400, Unit.PIXELS);
    	
    	H1 h1 = new H1("Infos");
    	
    	FormLayout memberInfoLayout = new FormLayout();
    	memberInfoLayout.add(memberNameField);
    	
    	H2 h2 = new H2("Abgaben");
    	    	
    	outputMemberGrid.setMinWidth(600, Unit.PIXELS);
    	outputMemberGrid.setMinHeight(200, Unit.PIXELS);
    	outputMemberGrid.addColumn(o -> o.getName()).setAutoWidth(true).setHeader("Sorte");
    	outputMemberGrid.addColumn(o -> o.getDateFinished()).setAutoWidth(true).setHeader("Datum der Ernte");
    	outputMemberGrid.addColumn(o -> resolveOutputOfStrainPerMember(o)).setAutoWidth(true).setHeader("Ausgegebene Menge in Gramm");
	
    	H2 h2Work = new H2("Arbeitszeiten");
    	
    	mainDialogLayout.add(h1, memberInfoLayout, h2, new Hr(), outputMemberGrid, h2Work, workloudMember);
    	return mainDialogLayout;
    }
    
    private Component addWorkingUnitLayoutForDialog() {
    	
    	VerticalLayout mainDialogLayout = new VerticalLayout();
    	mainDialogLayout.setWidth("100%");
    	mainDialogLayout.setMinWidth(400, Unit.PIXELS);
    	
    	H1 h1 = new H1("Zeiterfassung");
    	
    	H2 h2 = new H2("Aktuelle Schicht");
    	h2.addClassNames(LumoUtility.Margin.Top.LARGE);
    	
    	HorizontalLayout currentWorkingUnitLayout = new HorizontalLayout();
    	currentWorkingUnitLayout.setMinWidth(400, Unit.PIXELS);
    	currentWorkingUnitLayout.addClassNames(LumoUtility.Margin.Top.LARGE, LumoUtility.JustifyContent.CENTER);
    	
    	H3 h3 = new H3("Läuft seit: ");
    	HorizontalLayout textWrapper = new HorizontalLayout(durationWorkingUnit);
    	
	    currentWorkingUnitLayout.add(h3, textWrapper);
	    
	    buttonStopWorkingUnit.addClassName("save-button");
	    buttonStopWorkingUnit.setWidthFull();
	    buttonStopWorkingUnit.setEnabled(false);
	    buttonStopWorkingUnit.addClickListener(e -> {
	    	buttonStopWorkingUnit.setEnabled(false);
	    	LocalDateTime now = LocalDateTime.now();
	    	this.workingUnit.setEnd(now.toLocalDate());
	    	this.workingUnit.setHourEnd(now.getHour());
	    	this.workingUnit.setMinuteEnd(now.getMinute());
	    	
	    	LocalDateTime begin = LocalDateTime.of(workingUnit.getBegin(), LocalTime.of(workingUnit.getHourBegin(), workingUnit.getMinuteBegin()));
    		this.workingUnit.setWorkingHours((int)ChronoUnit.MINUTES.between(begin, now));

	    	workingUnitService.update(workingUnit);
	    	Notification.show("Schicht beendet.");
	    	
	    	this.workingUnit = null;
	    	startWorkingUnitDialog.close();
	    });
	    
    	H2 h2StartUnit = new H2("Neue Schicht");
    	FormLayout workingUnitInfoLayout = new FormLayout();
    	box = new ComboBox<WorkingUnitCategory>("Bereich");
    	box.setItems(workingUnitCategoryService.findAllByAssociation(associationId));
    	box.setItemLabelGenerator(e -> e.getName());
    	workingUnitInfoLayout.add(box);

    	buttonStartWorkingUnit.setWidthFull();
    	buttonStartWorkingUnit.setEnabled(false);
    	buttonStartWorkingUnit.addClassName("save-button");
    	buttonStartWorkingUnit.addClickListener(e -> {
    		
			if (box.getValue() != null) {
				LocalDateTime now = LocalDateTime.now();
				this.workingUnit = new WorkingUnit();
				this.workingUnit.setAssociationId(associationId);
				this.workingUnit.setBegin(now.toLocalDate());
				this.workingUnit.setHourBegin(now.getHour());
				this.workingUnit.setMinuteBegin(now.getMinute());
				this.workingUnit.setNote(
						"Schicht vom " + now.getDayOfMonth() + "." + now.getMonthValue() + "." + now.getYear());
				this.workingUnit.setCategory(box.getValue());
				this.workingUnit.setPersonId(this.selectedMember.getId());
				this.workingUnit
						.setPersonName(this.selectedMember.getFirstName() + " " + this.selectedMember.getLastName());
				this.workingUnit.setWorkingHours(0);

				this.workingUnit = workingUnitService.update(workingUnit);
				this.workingUnit = null;
				buttonStartWorkingUnit.setEnabled(false);
				startWorkingUnitDialog.close();
				Notification.show("Schicht begonnen.");

			} else {
				Notification.show("Es muss noch ein Bereich ausgewählt werden");
			}
    	});
	    
    	mainDialogLayout.add(h1, h2, currentWorkingUnitLayout, buttonStopWorkingUnit, new Hr(), h2StartUnit, workingUnitInfoLayout, buttonStartWorkingUnit);
    	return mainDialogLayout;
    }
      
    
    private String resolveOutputOfStrainPerMember(Strain s) {
    	
    	List<Output> outputOfMember = outputService.findOutputByMember(selectedMember.getId().intValue());
    	List<Output> outputOfSpecificStrain = outputOfMember.stream().filter(e -> e.getStrainId() == s.getId().intValue()).toList();
    	
    	Double amountOfStrain = 0.0;
    	
    	for(Output o : outputOfSpecificStrain) {
    		amountOfStrain = amountOfStrain + o.getAmount();
    	}
    	
    	return String.valueOf(amountOfStrain);
    }
    
    private void initMemberInfoDialog(Person person) {
    	this.selectedMember = person;
    	
    	memberNameField.setValue(person.getFirstName() + " " + person.getLastName());
    	memberNameField.setEnabled(false);
    	
    	outputMemberGrid.setItems(strainService.findAllByAssociation(associationId));
    	
    	List<WorkingUnit> workOfMember = workingUnitService.findByMember(person.getId().intValue());
    	
    	int workingTime = 0;
    	
    	for(WorkingUnit unit : workOfMember) {
    		workingTime = workingTime + unit.getWorkingHours();
    	}
    	
    	workloudMember.setText("Es wurden bereits " + resolveWorkingHours(workingTime) + " Minuten gearbeitet.");
    }
    
    private void initStartWorkDialog(Person person) {
    	this.selectedMember = person;
    	
    	if(workingUnitService.hasOpenWorkingUnit(person.getId().intValue())) {
    		Optional<WorkingUnit> openUnitByMember = workingUnitService.findOpenUnitByMember(person.getId().intValue());
    		
    		if(openUnitByMember.isPresent()) {
    			this.workingUnit = openUnitByMember.get();
    			LocalDateTime now = LocalDateTime.now();
				LocalDateTime begin = LocalDateTime.of(openUnitByMember.get().getBegin(),
						LocalTime.of(openUnitByMember.get().getHourBegin(), openUnitByMember.get().getMinuteBegin()));
    			
    			int i = ((int)ChronoUnit.MINUTES.between(begin, now));
    			
    			int hours = i/60;
    			int minutes = i%60;
    			
    			String time =  hours + " h " + minutes + " min";
    			
    			durationWorkingUnit.setText(time);
    		}
    		
    		buttonStopWorkingUnit.setEnabled(true);
    		buttonStartWorkingUnit.setEnabled(false);
    		
    	} else {
			durationWorkingUnit.setText("-");
    		buttonStopWorkingUnit.setEnabled(false);
    		buttonStartWorkingUnit.setEnabled(true);
    	}
		box.setValue(box.getEmptyValue());

    }
    
    private void initOutputDialog(Person person) {
    	LocalDateTime now = LocalDateTime.now();
    	this.selectedMember = person;

    	outputMemberField.setValue(person.getFirstName() + " " + person.getLastName());
    	outputMemberField.setWidthFull();
    	dateOutputField.setValue(now.getDayOfMonth() + "." + now.getMonthValue() + "." + now.getYear() + ", um " + now.getHour() + ":" + now.getMinute() + " Uhr");
    	
    }
    
    private String resolveWorkingHours(int workingHours) {
		
		int hours = workingHours/60;
		int minutes = workingHours%60;
		
		return hours + " h " + minutes + " min";
	}
    
    private void createStrainInfoDialogContent() {
    	
    	VerticalLayout mainDialogLayout = new VerticalLayout();
    	mainDialogLayout.setWidth("100%");
    	mainDialogLayout.setMinWidth(400, Unit.PIXELS);
    	
    	H1 h1 = new H1("Infos");
    	FormLayout strainInfoLayout = new FormLayout();
    	
    	strainInfoLayout.add(strainNameField);
    	strainInfoLayout.add(strainStatusField);
    	strainInfoLayout.add(strainAmountField);
    	strainInfoLayout.add(strainIngrediensThcField);
    	
    	mainDialogLayout.add(h1, strainInfoLayout);
    	strainInfoDialog.add(mainDialogLayout);
    	
    	Button cancelButton = new Button("Zurück", e -> strainInfoDialog.close());
    	cancelButton.addClassNames("cancel-button");
    	strainInfoDialog.getFooter().add(cancelButton);
    }
    
    private void initStrainInfoDialog(Strain strain) {    	
    	strainNameField.setValue(strain.getName());
    	strainNameField.setEnabled(false);
    	
    	strainStatusField.setValue(strain.getStatus().getLabel());
    	strainStatusField.setEnabled(false);
    	
    	strainAmountField.setValue(String.valueOf(strain.getAmountGramm()));
    	strainAmountField.setEnabled(false);
    	
    	strainIngrediensThcField.setValue(String.valueOf(strain.getThc()));
    	strainIngrediensThcField.setEnabled(false);
    }
    
    private String convertDayOfWeek(DayOfWeek dayOfWeek) {
		
		switch(dayOfWeek) {
		case MONDAY: return "Montag";
		case TUESDAY: return "Dienstag";
		case WEDNESDAY: return "Mittwoch";
		case THURSDAY: return "Donnerstag";
		case FRIDAY: return "Freitag";
		case SATURDAY: return "Samstag";
		case SUNDAY: return "Sonntag";
		}
		
		return "MONTAG";
	}

}
