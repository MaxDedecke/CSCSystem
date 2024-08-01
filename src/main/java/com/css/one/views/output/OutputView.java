package com.css.one.views.output;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.orm.ObjectOptimisticLockingFailureException;

import com.css.one.data.Cutting;
import com.css.one.data.Output;
import com.css.one.data.OutputEntity;
import com.css.one.data.OutputType;
import com.css.one.data.PaymentMethod;
import com.css.one.data.Person;
import com.css.one.data.Seed;
import com.css.one.data.Strain;
import com.css.one.data.Transaction;
import com.css.one.data.TransactionType;
import com.css.one.services.CuttingService;
import com.css.one.services.OutputService;
import com.css.one.services.PersonService;
import com.css.one.services.SeedService;
import com.css.one.services.StrainService;
import com.css.one.services.TransactionService;
import com.css.one.views.MainLayout;
import com.vaadin.flow.component.button.Button;
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
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.PermitAll;

@PageTitle("Abgabe")
@Route(value = "abgabe", layout = MainLayout.class)
@PermitAll
public class OutputView extends Div {

	private static final long serialVersionUID = -433269476320215595L;
	
    private OutputService outputService;
    private PersonService personService;
    private StrainService strainService;
    private TransactionService transactionService;
    private CuttingService cuttingService;
    private SeedService seedService;
    
	ComboBox<PaymentMethod> paymentMethodBox = new ComboBox<PaymentMethod>("Zahlungsmethode");
	ComboBox<Person> memberBox = new ComboBox<Person>("Mitglied");
	Double endPrice;
	ComboBox<OutputType> typeBox = new ComboBox<OutputType>("Typ");
	ComboBox<OutputEntity> outputEntityBox = new ComboBox<OutputEntity>("Sorte");

    private int associationId;

    Dialog addOutputDialog;
	H2 amount = new H2("0 Gramm");

	private Grid<Output> outputGrid = new Grid<Output>();
	
    List<Output> outputAssociation = new ArrayList<>();

	public OutputView(OutputService outputService, PersonService personService, StrainService strainService, TransactionService transactionService, CuttingService cuttingService, SeedService seedService) {
		this.strainService = strainService;
		this.outputService = outputService;
		this.personService = personService;
		this.transactionService = transactionService;
		this.cuttingService = cuttingService;
		this.seedService = seedService;
		
		addClassNames("output-view");
		
        associationId = MainLayout.getAssociationId();

		createMainLayout();
	}

	private void createMainLayout() {
		
		VerticalLayout wrapper = new VerticalLayout();
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		
		VerticalLayout layoutButton = new VerticalLayout();
		layoutButton.addClassNames(LumoUtility.Padding.Left.NONE);
		Button addOutputButton = new Button();
		addOutputButton.addClassName("button-layout-common");
		addOutputButton.setText("+ Abgabe");
		
		addOutputButton.addClickListener(e -> openAddOutput());
		
		layoutButton.setAlignItems(Alignment.CENTER);
	
		layoutButton.add(addOutputButton);
		
		H2 balance = new H2("Kontingent:");

		VerticalLayout layout = new VerticalLayout();
		layout.add(balance);
		layout.add(amount);
		layout.setAlignItems(Alignment.CENTER);

		horizontalLayout.add(layoutButton);
		horizontalLayout.add(layout);
		horizontalLayout.addClassNames(LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
		wrapper.add(horizontalLayout);
		
		outputGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
		outputGrid.addColumn(p -> renderDate(p.getDate())).setHeader("Datum").setAutoWidth(true).setSortable(true);
		outputGrid.addColumn(p -> resolveStrain(p.getEntityId())).setHeader("Sorte").setAutoWidth(true).setSortable(true);
		outputGrid.addColumn(p -> renderPersonName(personService.get(Integer.toUnsignedLong(p.getPersonId())))).setHeader("Mitglied").setAutoWidth(true).setSortable(true);
		outputGrid.addColumn(p -> p.getAmount() + " Gramm").setHeader("Menge").setAutoWidth(true).setSortable(true);
		outputGrid.addColumn(p -> p.getNote()).setHeader("Notiz").setAutoWidth(true).setSortable(true);
				
		outputGrid.addComponentColumn(item ->{
			Button button = new Button("Löschen");
			button.addClickListener(click -> {
				item.setOutdated(true);
				outputService.update(item);
				Optional<Strain> optionalStrain = strainService.get(Integer.toUnsignedLong(item.getEntityId()));
				if(optionalStrain.isPresent()) {				
					optionalStrain.get().setAmountGramm(optionalStrain.get().getAmountGramm() + item.getAmount());
					strainService.update(optionalStrain.get());	
				}
				refreshGrid();
	        });
			button.addClassName("button-grid-red");
			
			return button;
		});
		
		refreshGrid();
		
		wrapper.add(outputGrid);
		add(wrapper);
	}
	
	private void openAddOutput() {
		addOutputDialog = new Dialog();
		VerticalLayout mainLayout = new VerticalLayout();
		VerticalLayout headerLayout = new VerticalLayout();
		
		H2 header = new H2("Abgabe");
		Hr hr = new Hr();		
		headerLayout.add(header, hr);
		
		FormLayout formLayout = new FormLayout();
		formLayout.addClassNames(LumoUtility.Margin.Left.MEDIUM);
		
		DateTimePicker date = new DateTimePicker();
		date.setLabel("Datum");
		date.setStep(Duration.ofSeconds(1));
		date.setValue(LocalDateTime.now());
		
		NumberField strainInfoAmount = new NumberField("Menge in Gramm");
		strainInfoAmount.addValueChangeListener(e -> {
    		if (!outputEntityBox.isEmpty()) {
				endPrice = outputEntityBox.getValue().getPrice() * Double.valueOf(e.getValue());
			}
    	});
		
		memberBox.setItems(personService.findAllByAssociation(associationId));
		memberBox.setItemLabelGenerator(e -> e.getFirstName() + " " + e.getLastName());
		
		changeItemsDependingOnOutputType(OutputType.BLOSSOM);
//		outputEntityBox.setItemLabelGenerator(e -> e.getName() + " (" + e.getThc() + "% THC)");
		outputEntityBox.addValueChangeListener(e -> {
			endPrice = e.getValue().getPrice() * Double.valueOf(strainInfoAmount.getValue());
		});
		
		typeBox.setItems(OutputType.values());
		typeBox.setItemLabelGenerator(e -> e.getLabel());
		typeBox.addValueChangeListener(e -> {
			
			changeItemsDependingOnOutputType(e.getValue());
			
		});
		typeBox.setValue(typeBox.getListDataView().getItem(0));
		
		TextField noteField = new TextField("Notiz");
		
		paymentMethodBox.setItems(PaymentMethod.values());
		paymentMethodBox.setValue(paymentMethodBox.getListDataView().getItem(0));
		paymentMethodBox.setItemLabelGenerator(e -> e.getLabel());
		
		formLayout.add(date, outputEntityBox, strainInfoAmount, memberBox, typeBox, noteField, paymentMethodBox);
		
		mainLayout.add(headerLayout, formLayout);
		
		addOutputDialog.add(mainLayout);
		
		Button saveButton = new Button("Hinzufügen", e -> {
			
			if(checkInput(strainInfoAmount, memberBox, outputEntityBox)) {
				addNewOutput(date.getValue(), outputEntityBox.getValue().getId(), strainInfoAmount.getValue(), memberBox.getValue(), noteField.getValue());
				bookTransaction(strainInfoAmount, paymentMethodBox.getValue());
				Notification.show("Neue Abgabe mit zugehöriger Transaktion erstellt!");
				addOutputDialog.close();
				refreshGrid();
			}
		});
		saveButton.addClassName("save-button");

		Button cancelButton = new Button("Abbrechen", e -> addOutputDialog.close());
		cancelButton.addClassName("cancel-button");
		
		addOutputDialog.getFooter().add(cancelButton);
		addOutputDialog.getFooter().add(saveButton);
		addOutputDialog.open();
	}
	
	private void changeItemsDependingOnOutputType(OutputType value) {
		List<OutputEntity> list = new ArrayList<>();
		
		if(value == OutputType.BLOSSOM) {			
			strainService.findAllByAssociation(associationId).forEach(e -> list.add(e));
		} else if(value == OutputType.CUTTING) {
			cuttingService.findAllByAssociation(associationId).forEach(e -> list.add(e));
		} else {
			seedService.findAllByAssociation(associationId).forEach(e -> list.add(e));
		}
		outputEntityBox.setItems(list);
		
	}

	private void bookTransaction(NumberField strainInfoAmount, PaymentMethod value) {
		try {
			Transaction outputTransaction = new Transaction();
			outputTransaction.setNote("Schnellausgabe");
			outputTransaction.setType(TransactionType.INCOME);
			outputTransaction.setDateOfTransaction(LocalDate.now());
			outputTransaction.setAssociationId(associationId);
			outputTransaction.setPaymentMethod(paymentMethodBox.getValue());
		    outputTransaction.setMemberId(memberBox.getValue().getId().intValue());
		    outputTransaction.setAmount(Double.valueOf(endPrice));

			transactionService.update(outputTransaction);				
		} catch (ObjectOptimisticLockingFailureException exception) {
			Notification n = Notification.show(
					"Error updating the data. Somebody else has updated the record while you were making changes.");
			n.setPosition(Position.MIDDLE);
			n.addThemeVariants(NotificationVariant.LUMO_ERROR);
		}
		
	}

	private boolean checkInput(NumberField strainInfoAmount, ComboBox<Person> memberBox, ComboBox<OutputEntity> strainBox) {
		
		if(strainInfoAmount.getValue() == null) {
			Notification.show("Keine Menge angegeben !");
			return false;
		}
		
		if(memberBox.getValue() == null) {
			Notification.show("Kein Mitglied ausgewählt !");
			return false;
		}
		
		if(strainBox.getValue() == null) {
			Notification.show("Keine Sorte ausgewählt !");
			return false;
		}
		
		//TODO
		//Hier überprüfen, ob mitglied ott haben darf
		
		
		return true;
	}
	
	private String resolveStrain(int strainId) {
		Optional<Strain> optionalStrain = strainService.get(Integer.toUnsignedLong(strainId));
		return optionalStrain.isPresent() ? optionalStrain.get().getName() : "-";
	}
	
	private String renderPersonName(Optional<Person> person) {
		
		if(person.isPresent()) {
			Person p = person.get();
			return p.getFirstName() + " " + p.getLastName();
		} else {
			return "-";			
		}
	}
	
	private void addNewOutput(LocalDateTime date, Long outputEntityId, Double amount, Person person, String note) {
		Output output = new Output();
		
		output.setDate(date.toLocalDate());
		output.setEntityId(outputEntityId.intValue());
		output.setAmount(amount);
		output.setAssociationId(associationId);
		output.setPersonId(person.getId().intValue());
		output.setType(typeBox.getValue());
		
		if(note != null) {			
			output.setNote(note);
		}
		
		outputService.update(output);
		
		if (typeBox.getValue() == OutputType.BLOSSOM) {
			Optional<Strain> optional = strainService.get(outputEntityId);
			if (optional.isPresent()) {
				optional.get().setAmountGramm(optional.get().getAmountGramm() - amount);
				strainService.update(optional.get());
			}
		} else if (typeBox.getValue() == OutputType.CUTTING) {
			Optional<Cutting> optional = cuttingService.get(outputEntityId);	
			if(optional.isPresent()) {			
				optional.get().setAmountOfCuttings(optional.get().getAmountOfCuttings() - (amount.intValue()));
				cuttingService.update(optional.get());		
			}
		} else {
			Optional<Seed> optional = seedService.get(outputEntityId);	
			if(optional.isPresent()) {			
				optional.get().setAmountOfSeeds(optional.get().getAmountOfSeeds() - (amount.intValue()));
				seedService.update(optional.get());		
			}
		}
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
	
	private void refreshGrid() {

		outputAssociation = outputService.findAllByAssociation(associationId);
		
		this.outputGrid.setItems(outputAssociation.stream().filter(e -> !e.isOutdated()).toList());

		double generalAmount = 0;

		this.amount.setText(String.valueOf(generalAmount) + " Gramm");
	}

}
