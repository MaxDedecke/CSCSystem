package com.css.one.views.übersicht;

import java.time.DayOfWeek;
import java.time.LocalDate;

import com.css.one.data.Person;
import com.css.one.services.PersonService;
import com.css.one.views.MainLayout;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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
    
    private HorizontalLayout layoutSearchMembers = new HorizontalLayout();
    private ComboBox<Person> searchMemberBox = new ComboBox<>();
    private PersonService personService;
    private Button buttonOpenPersonInfo = new Button("Informationen");
	private int associationId;
    
    public ÜbersichtView(PersonService personService) {    	
    	this.personService = personService;
    	addClassNames("uebersicht-view");
    	
    	layoutSearchMembers.setWidthFull();
    	layoutSearchMembers.setHeight(120, Unit.PIXELS);
    	
    	layoutSearchMembers.addClassNames(LumoUtility.AlignContent.CENTER,
//    			LumoUtility.Border.ALL, LumoUtility.BorderColor.ERROR, 
    			LumoUtility.JustifyContent.CENTER, LumoUtility.Margin.Top.LARGE); 
    	
		associationId = MainLayout.getAssociationId();
		
		LocalDate now = LocalDate.now();
		currentDateText = new H1(convertDayOfWeek(now.getDayOfWeek()) + ", den " + 
				now.getDayOfMonth() + "." + now.getMonth().getValue() + "." + now.getYear());
		
    	this.searchMemberBox.setItems(personService.findAllByAssociation(associationId));
    	this.searchMemberBox.setItemLabelGenerator(e -> e.getFirstName() + " " + e.getLastName());
    	this.searchMemberBox.setWidth(350, Unit.PIXELS);
    	this.searchMemberBox.setHeight(75, Unit.PIXELS);
    	
    	this.searchMemberBox.addValueChangeListener(e -> {
    		if(e.getValue() == null) {
    			buttonOpenPersonInfo.setEnabled(false);
    		} else {    			
    			buttonOpenPersonInfo.setEnabled(true);
    		}
    	});
    	
    	this.searchMemberBox.setClearButtonVisible(true);

    	H1 headerSearch = new H1("Mitgliedersuche: ");
    	buttonOpenPersonInfo.setEnabled(false);
    	buttonOpenPersonInfo.setWidth(200, Unit.PIXELS);
    	
    	//Add margin top
    	headerSearch.addClassNames(LumoUtility.Margin.Top.LARGE);
    	searchMemberBox.addClassNames(LumoUtility.Padding.SMALL, LumoUtility.Margin.Top.LARGE);
    	buttonOpenPersonInfo.addClassNames(LumoUtility.Padding.LARGE,LumoUtility.Margin.Top.LARGE);

		layoutSearchMembers.add(headerSearch, searchMemberBox, buttonOpenPersonInfo);
        add(currentDateText, layoutSearchMembers, new Hr());
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
