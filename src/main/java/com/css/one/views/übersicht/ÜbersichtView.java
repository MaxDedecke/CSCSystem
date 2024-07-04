package com.css.one.views.übersicht;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import com.css.one.data.Output;
import com.css.one.data.Person;
import com.css.one.data.Strain;
import com.css.one.data.WorkingUnit;
import com.css.one.services.OutputService;
import com.css.one.services.PersonService;
import com.css.one.services.StrainService;
import com.css.one.services.WorkingUnitService;
import com.css.one.views.MainLayout;
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
import com.vaadin.flow.component.html.Hr;
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
    private HorizontalLayout layoutCurrentDate = new HorizontalLayout();

    private ComboBox<Person> searchMemberBox = new ComboBox<>();
    private ComboBox<Strain> searchStrainBox = new ComboBox<>();

    private PersonService personService;
    private StrainService strainService;
    private OutputService outputService;
    private WorkingUnitService workingUnitService;
    
    private Button buttonOpenPersonInfo = new Button("Info");
    private Button buttonOpenStrainInfo = new Button("Info");
    
    private Person selectedMember; 
    private Dialog memberInfoDialog = new Dialog();
    private TextField memberNameField = new TextField("Name");
    Grid<Strain> outputMemberGrid = new Grid<>();
    Text workloudMember = new Text("");
    
    private Dialog strainInfoDialog = new Dialog();
    private TextField strainNameField = new TextField("Name");
    private TextField strainStatusField = new TextField("Status");
    private TextField strainAmountField = new TextField("Verbleibende Menge");
    private TextField strainIngrediensThcField = new TextField("THC in %");
    
	private int associationId;
    
    public ÜbersichtView(PersonService personService, StrainService strainService, OutputService outputService, WorkingUnitService workingUnitService) {    	
    	this.personService = personService;
    	this.strainService = strainService;
    	this.outputService = outputService;
    	this.workingUnitService = workingUnitService;
    	
    	addClassNames("uebersicht-view", LumoUtility.AlignContent.CENTER);
    	
		associationId = MainLayout.getAssociationId();
		createCurrentDateLayout();
		createSearchMemberLayout();
		createSearchStrainLayout();
		
		Hr hr1 = new Hr();
		hr1.addClassName( LumoUtility.Margin.LARGE);
		Hr hr2 = new Hr();
		hr2.addClassName( LumoUtility.Margin.LARGE);
		
		HorizontalLayout firstLayerLayout = new HorizontalLayout();
		firstLayerLayout.add(layoutSearchMembers, layoutSearchStrains);
        add(layoutCurrentDate, hr1, firstLayerLayout, hr2);
    }
    
    private void createCurrentDateLayout() {
    	
    	layoutCurrentDate.addClassNames(LumoUtility.AlignContent.CENTER,
    			LumoUtility.JustifyContent.CENTER, LumoUtility.Margin.Top.LARGE, LumoUtility.Margin.LARGE, LumoUtility.BorderRadius.LARGE); 
    	
		LocalDate now = LocalDate.now();
		currentDateText = new H1(convertDayOfWeek(now.getDayOfWeek()) + ", der " + 
				now.getDayOfMonth() + "." + now.getMonth().getValue() + "." + now.getYear());
		
		layoutCurrentDate.add(currentDateText);
    }
    
    private void createSearchMemberLayout() {
    	
    	layoutSearchMembers.addClassNames("vaadin-horizontal-layout", LumoUtility.AlignContent.CENTER,
    			LumoUtility.Border.ALL, LumoUtility.BorderColor.PRIMARY_10, 
    			LumoUtility.JustifyContent.CENTER, 
    			LumoUtility.Margin.Top.LARGE, LumoUtility.Margin.LARGE, LumoUtility.BorderRadius.LARGE); 
    	 
    	HorizontalLayout headerLayout = new HorizontalLayout();
    	H1 headerSearch = new H1("Mitgliedersuche");
    	headerSearch.addClassNames(LumoUtility.Margin.MEDIUM);
    	headerLayout.add(headerSearch);
    	headerLayout.addClassNames(LumoUtility.JustifyContent.CENTER);
    	headerLayout.setWidth("100%");
    	
    	buttonOpenPersonInfo.setEnabled(false);
    	
    	HorizontalLayout boxLayout = new HorizontalLayout();
    	boxLayout.setWidth("100%");

    	this.searchMemberBox.setItems(personService.findAllByAssociation(associationId));
    	this.searchMemberBox.setItemLabelGenerator(e -> e.getFirstName() + " " + e.getLastName());
    	this.searchMemberBox.setHeight(75, Unit.PIXELS);
    	this.searchMemberBox.setWidth("100%");
    	
    	createMemberInfoDialogContent();

    	this.searchMemberBox.addValueChangeListener(e -> {
    		if(e.getValue() == null) {
    			buttonOpenPersonInfo.setEnabled(false);
    		} else {    			
    			buttonOpenPersonInfo.setEnabled(true);
    		}
    	});
    	
    	buttonOpenPersonInfo.addClickListener(e -> {
    		initMemberInfoDialog(searchMemberBox.getValue());
    		memberInfoDialog.open();
    	});
    	
    	this.searchMemberBox.setClearButtonVisible(true);	
    	boxLayout.add(searchMemberBox, buttonOpenPersonInfo);
    	
    	//Add margin top
    	headerSearch.addClassNames(LumoUtility.Margin.Top.LARGE);
    	searchMemberBox.addClassNames(LumoUtility.Padding.SMALL, LumoUtility.Margin.Top.LARGE);
    	buttonOpenPersonInfo.addClassNames(LumoUtility.Padding.LARGE,LumoUtility.Margin.Top.LARGE);

		layoutSearchMembers.add(headerLayout, new Hr(), boxLayout);

    }

    private void createSearchStrainLayout() {
    	layoutSearchStrains.addClassNames(LumoUtility.AlignContent.CENTER,
    			LumoUtility.Border.ALL, LumoUtility.BorderColor.PRIMARY_10, 
    			LumoUtility.Margin.Top.LARGE, LumoUtility.Margin.LARGE, LumoUtility.BorderRadius.LARGE);
    	
    	HorizontalLayout headerLayout = new HorizontalLayout();
    	headerLayout.setWidth("100%");
    	H1 headerSearch = new H1("Bestandssuche");
    	headerSearch.addClassNames(LumoUtility.Margin.MEDIUM);
    	headerLayout.addClassNames(LumoUtility.JustifyContent.CENTER);
    	headerLayout.add(headerSearch);

    	buttonOpenStrainInfo.setEnabled(false);
    	HorizontalLayout boxLayout = new HorizontalLayout();
    	boxLayout.setWidth("100%");
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
    	
    	//Add margin top
    	headerSearch.addClassNames(LumoUtility.Margin.Top.LARGE);
    	searchStrainBox.addClassNames(LumoUtility.Padding.SMALL, LumoUtility.Margin.Top.LARGE);
    	buttonOpenStrainInfo.addClassNames(LumoUtility.Padding.LARGE,LumoUtility.Margin.Top.LARGE);
    	
		layoutSearchStrains.add(headerLayout, new Hr(), boxLayout);

    }
    
    private void createMemberInfoDialogContent() {
    	    	
    	VerticalLayout mainDialogLayout = new VerticalLayout();
    	mainDialogLayout.setWidth("100%");
    	mainDialogLayout.setMinWidth(400, Unit.PIXELS);
    	
    	H1 h1 = new H1("Infos");
    	
    	FormLayout memberInfoLayout = new FormLayout();
    	memberInfoLayout.add(memberNameField);
    	
    	H2 h2 = new H2("Abgaben");
    	    	
    	outputMemberGrid.setMinWidth(600, Unit.PIXELS);
    	outputMemberGrid.setMaxHeight(200, Unit.PIXELS);
    	
    	outputMemberGrid.addColumn(o -> o.getName()).setAutoWidth(true).setHeader("Sorte");
    	outputMemberGrid.addColumn(o -> o.getDateFinished()).setAutoWidth(true).setHeader("Datum der Ernte");
    	outputMemberGrid.addColumn(o -> resolveOutputOfStrainPerMember(o)).setAutoWidth(true).setHeader("Kontigent");
	
    	H2 h2Work = new H2("Arbeitszeiten");
    	
    	mainDialogLayout.add(h1, memberInfoLayout, h2, new Hr(), outputMemberGrid, h2Work, workloudMember);
    	
    	memberInfoDialog.add(mainDialogLayout);
    	Button cancelButton = new Button("Zurück", e -> memberInfoDialog.close());
    	memberInfoDialog.getFooter().add(cancelButton);
    }
    
    private String resolveOutputOfStrainPerMember(Strain s) {
    	List<Output> outputOfMember = outputService.findOutputByMember(selectedMember.getId().intValue());
    	List<Output> outputOfSpecificStrain = outputOfMember.stream().filter(e -> e.getStrainId() == s.getId().intValue()).toList();
    	
    	Double amountOfStrain = 0.0;
    	
    	for(Output o : outputOfSpecificStrain) {
    		amountOfStrain = amountOfStrain + o.getAmount();
    	}
    	
    	return String.valueOf(amountOfStrain) + " / " + s.getAmountPerMember() + " Gramm";
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
    	strainInfoDialog.getFooter().add(cancelButton);
    }
    
    private void initStrainInfoDialog(Strain strain) {
//    	List<Output> outputOfStrain = outputService.findOutputByStrain(strain.getId().intValue());
    	
    	strainNameField.setValue(strain.getName());
    	strainNameField.setEnabled(false);
    	
    	strainStatusField.setValue(strain.getStatus().getLabel());
    	strainStatusField.setEnabled(false);
    	
    	strainAmountField.setValue(String.valueOf(strain.getAmount()));
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
