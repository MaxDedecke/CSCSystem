package com.css.one.views.output;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.css.one.data.Output;
import com.css.one.data.Person;
import com.css.one.data.Strain;
import com.css.one.services.OutputService;
import com.css.one.services.PersonService;
import com.css.one.services.StrainService;
import com.css.one.views.MainLayout;
import com.vaadin.flow.component.Unit;
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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Abgabe")
@Route(value = "abgabe", layout = MainLayout.class)
@AnonymousAllowed
public class OutputView extends Div {

	private static final long serialVersionUID = -433269476320215595L;
	
    private OutputService outputService;
    private PersonService personService;
    private StrainService strainService;

    private int associationId;

    Dialog addOutputDialog;
	H2 amount = new H2("0 Gramm");

	private Grid<Output> outputGrid = new Grid<Output>();
	
    List<Output> outputAssociation = new ArrayList<>();

	public OutputView(OutputService outputService, PersonService personService, StrainService strainService) {
		this.strainService = strainService;
		this.outputService = outputService;
		this.personService = personService;
		
        associationId = MainLayout.getAssociationId();

		createMainLayout();
	}

	private void createMainLayout() {
		
		Div wrapper = new Div();
		wrapper.setClassName("grid-wrapper");
			
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		
		VerticalLayout layoutButton = new VerticalLayout();
		Button addOutputButton = new Button();
		addOutputButton.setHeight(75, Unit.PIXELS);
		addOutputButton.setWidth(175, Unit.PIXELS);
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
		wrapper.add(horizontalLayout);
		
		outputGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
		outputGrid.addColumn(p -> renderDate(p.getDate())).setHeader("Datum").setAutoWidth(true).setSortable(true);
		outputGrid.addColumn(p -> resolveStrain(p.getStrainId())).setHeader("Sorte").setAutoWidth(true).setSortable(true);
		outputGrid.addColumn(p -> renderPersonName(personService.get(Integer.toUnsignedLong(p.getPersonId())))).setHeader("Mitglied").setAutoWidth(true).setSortable(true);
		outputGrid.addColumn(p -> p.getAmount() + " Gramm").setHeader("Menge").setAutoWidth(true).setSortable(true);
		outputGrid.addColumn(p -> p.getNote()).setHeader("Notiz").setAutoWidth(true).setSortable(true);
		
		outputGrid.addComponentColumn(item -> new Button("Löschen", click -> {
			item.setOutdated(true);
			outputService.update(item);
			Optional<Strain> optionalStrain = strainService.get(Integer.toUnsignedLong(item.getStrainId()));
			if(optionalStrain.isPresent()) {				
				optionalStrain.get().setAmount(optionalStrain.get().getAmount() + item.getAmount());
				strainService.update(optionalStrain.get());	
			}
			refreshGrid();
        }));
		
		refreshGrid();
		
		wrapper.add(outputGrid);
		add(wrapper);
	}
	
	private void openAddOutput() {
		addOutputDialog = new Dialog();
		
		VerticalLayout headerLayout = new VerticalLayout();
		
		H2 header = new H2("Abgabe");
		Hr hr = new Hr();		
		headerLayout.add(header, hr);
		
		FormLayout formLayout = new FormLayout();
		formLayout.setWidth(400, Unit.PIXELS);
		
		DateTimePicker date = new DateTimePicker();
		date.setLabel("Datum");
		date.setStep(Duration.ofSeconds(1));
		date.setValue(LocalDateTime.now());
		
		NumberField strainInfoAmount = new NumberField("Menge in Gramm");
		
		ComboBox<Person> memberBox = new ComboBox<Person>("Mitglied");
		memberBox.setItems(personService.findAllByAssociation(associationId));
		memberBox.setItemLabelGenerator(e -> e.getFirstName() + " " + e.getLastName());
		
		ComboBox<Strain> strainBox = new ComboBox<Strain>("Sorte");
		strainBox.setItems(strainService.findAllByAssociation(associationId));
		strainBox.setItemLabelGenerator(e -> e.getName() + " (" + e.getThc() + "% THC)");
		
		TextField noteField = new TextField("Notiz");
		
		formLayout.add(date, strainBox, strainInfoAmount, memberBox, noteField);
		
		addOutputDialog.add(headerLayout);
		addOutputDialog.add(formLayout);
		
		Button saveButton = new Button("Hinzufügen", e -> {
			
			if(checkInput(strainInfoAmount, memberBox, strainBox)) {
				addNewOutput(date.getValue(), strainBox.getValue(), strainInfoAmount.getValue(), memberBox.getValue(), noteField.getValue());
				addOutputDialog.close();
				refreshGrid();
			}
		});

		Button cancelButton = new Button("Abbrechen", e -> addOutputDialog.close());

		addOutputDialog.getFooter().add(cancelButton);
		addOutputDialog.getFooter().add(saveButton);
		addOutputDialog.open();
	}
	
	private boolean checkInput(NumberField strainInfoAmount, ComboBox<Person> memberBox, ComboBox<Strain> strainBox) {
		
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
	
	private void addNewOutput(LocalDateTime date, Strain strain, Double amount, Person person, String note) {
		Output output = new Output();
		
		output.setDate(date.toLocalDate());
		output.setStrainId(strain.getId().intValue());
		output.setAmount(amount);
		output.setAssociationId(associationId);
		output.setPersonId(person.getId().intValue());
		if(note != null) {			
			output.setNote(note);
		}
		
		outputService.update(output);
		
		strain.setAmount(strain.getAmount() - amount);
		strainService.update(strain);		
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
