package com.css.one.components;

import java.util.HashMap;
import java.util.Map;

import com.css.one.data.MemberData;
import com.css.one.data.OnboardingData;
import com.css.one.data.WaitingPerson;
import com.css.one.data.enums.OnboardingStatus;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class CompareDataComponent extends VerticalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2666409461606964998L;
	
	private WaitingPerson person;
	private HorizontalLayout mainLayout = new HorizontalLayout();
	private H2 headerCompare = new H2("Angaben vergleichen");
	
	private VerticalLayout headerWrapper = new VerticalLayout();
	private VerticalLayout prevDataWrapper = new VerticalLayout();
	private VerticalLayout personInputDataWrapper = new VerticalLayout();
	
	private FormLayout prevDataLayout = new FormLayout();
	private FormLayout personInputDataLayout = new FormLayout();
	
	private H3 headerPrevData;
	private H3 headerPersonData;
	
	private HorizontalLayout firstNameSystemWrapper = new HorizontalLayout();
	private TextField firstNameInSystem = new TextField("Vorname");
	
	private HorizontalLayout firstNameUserInputWrapper = new HorizontalLayout();
	private Checkbox firstNameUserInputBox = new Checkbox();
	private TextField firstNameOfUserInput = new TextField("Vorname");
	
	private HorizontalLayout lastNameSystemWrapper = new HorizontalLayout();
	private TextField lastNameInSystem = new TextField("Nachname");
	
	private HorizontalLayout lastNameUserInputWrapper = new HorizontalLayout();
	private Checkbox lastNameUserInputBox = new Checkbox();
	private TextField lastNameOfUserInput = new TextField("Nachname");
	
	private HorizontalLayout phoneSystemWrapper = new HorizontalLayout();
	private TextField phoneInSystem = new TextField("Telefonnummber");
	
	private HorizontalLayout phoneUserInputWrapper = new HorizontalLayout();
	private Checkbox phoneUserInputBox = new Checkbox();
	private TextField phoneOfUserInput = new TextField("Telefonnummer");
	
	private HorizontalLayout emailSystemWrapper = new HorizontalLayout();
	private TextField emailInSystem = new TextField("Email");
	
	private HorizontalLayout emailUserInputWrapper = new HorizontalLayout();
	private Checkbox emailUserInputBox = new Checkbox();
	private TextField emailOfUserInput = new TextField("Email");
	
	private HorizontalLayout birthDateSystemWrapper = new HorizontalLayout();
	private DatePicker birthDateInSystem = new DatePicker("Geburtsdatum");
	
	private HorizontalLayout birthDateUserInputWrapper = new HorizontalLayout();
	private Checkbox birthDateUserInputBox = new Checkbox();
	private DatePicker birthDateOfUserInput = new DatePicker("Geburtsdatum");
	
	private HorizontalLayout streetNameSystemWrapper = new HorizontalLayout();
	private TextField streetNameInSystem = new TextField("Straße");
	
	private HorizontalLayout streetNameUserInputWrapper = new HorizontalLayout();
	private Checkbox streetNameUserInputBox = new Checkbox();
	private TextField streetNameOfUserInput = new TextField("Straße");
	
	private HorizontalLayout streetNumberSystemWrapper = new HorizontalLayout();
	private TextField streetNumberInSystem = new TextField("Hausnummer");
	
	private HorizontalLayout streetNumberUserInputWrapper = new HorizontalLayout();
	private Checkbox streetNumberUserInputBox = new Checkbox();
	private TextField streetNumberOfUserInput = new TextField("Hausnummer");
	
	private HorizontalLayout postalCodeSystemWrapper = new HorizontalLayout();
	private TextField postalCodeInSystem = new TextField("PLZ");
	
	private HorizontalLayout postalCodeUserInputWrapper = new HorizontalLayout();
	private Checkbox postalCodeUserInputBox = new Checkbox();
	private TextField postalCodeOfUserInput = new TextField("PLZ");
	
	private HorizontalLayout citySystemWrapper = new HorizontalLayout();
	private TextField cityInSystem = new TextField("Ort");
	
	private HorizontalLayout cityUserInputWrapper = new HorizontalLayout();
	private Checkbox cityUserInputBox = new Checkbox();
	private TextField cityOfUserInput = new TextField("Ort");
	
	private Map<Checkbox, Component> mappings = new HashMap<Checkbox, Component>();
	
	public CompareDataComponent() {
		
		super();
		
		mainLayout.addClassNames(LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
		
		headerCompare.addClassName("customheader");
		headerWrapper.add(headerCompare);
		
		//left side
		headerPrevData = new H3("Daten im System");
		headerPrevData.addClassName("customheader");
		prevDataWrapper.add(headerPrevData, prevDataLayout);
		
		createPrevDataLayout();
		
		//right side
		headerPersonData = new H3("Selbstauskunft der Person");
		headerPersonData.addClassName("customheader");
		personInputDataWrapper.add(headerPersonData, personInputDataLayout);
		
		createPersonInputDataLayout();
		
		mainLayout.add(prevDataWrapper, personInputDataWrapper);
		add(headerCompare, mainLayout);		
	}
	
	private void createPersonInputDataLayout() {
		
		firstNameUserInputBox.addClassName("margin-extra-top-css");
		firstNameUserInputBox.addValueChangeListener(e -> {			
			if(e.getValue()) {				
				firstNameUserInputWrapper.addClassName("box-shadow-green");
			} else {
				firstNameUserInputWrapper.removeClassName("box-shadow-green");
			}
		});
		
		firstNameUserInputWrapper.add(firstNameUserInputBox, firstNameOfUserInput);
		firstNameOfUserInput.addClassName("width-inner-popup-component");
		firstNameOfUserInput.setReadOnly(true);
		mappings.put(firstNameUserInputBox, firstNameOfUserInput);
		
		lastNameUserInputBox.addClassName("margin-extra-top-css");
		lastNameUserInputBox.addValueChangeListener(e -> {
			if(e.getValue()) {				
				lastNameUserInputWrapper.addClassName("box-shadow-green");
			} else {
				lastNameUserInputWrapper.removeClassName("box-shadow-green");
			}
		});
		
		lastNameUserInputWrapper.add(lastNameUserInputBox, lastNameOfUserInput);
		lastNameOfUserInput.addClassName("width-inner-popup-component");
		lastNameOfUserInput.setReadOnly(true);
		mappings.put(lastNameUserInputBox, lastNameOfUserInput);

		birthDateUserInputBox.addClassName("margin-extra-top-css");
		birthDateUserInputBox.addValueChangeListener(e -> {
			if(e.getValue()) {				
				birthDateUserInputWrapper.addClassName("box-shadow-green");
			} else {
				birthDateUserInputWrapper.removeClassName("box-shadow-green");
			}
		});
		
		birthDateUserInputWrapper.add(birthDateUserInputBox, birthDateOfUserInput);
		birthDateOfUserInput.addClassName("width-inner-popup-component");
		birthDateOfUserInput.setReadOnly(true);
		mappings.put(birthDateUserInputBox, birthDateOfUserInput);

		phoneUserInputBox.addClassName("margin-extra-top-css");
		phoneUserInputBox.addValueChangeListener(e -> {
			if(e.getValue()) {				
				phoneUserInputWrapper.addClassName("box-shadow-green");
			} else {
				phoneUserInputWrapper.removeClassName("box-shadow-green");
			}
		});
		
		phoneOfUserInput.addClassName("width-inner-popup-component");
		phoneUserInputWrapper.add(phoneUserInputBox, phoneOfUserInput);
		phoneOfUserInput.setReadOnly(true);
		mappings.put(phoneUserInputBox, phoneOfUserInput);

		emailUserInputBox.addClassName("margin-extra-top-css");
		emailUserInputBox.addValueChangeListener(e -> {
			if(e.getValue()) {				
				emailUserInputWrapper.addClassName("box-shadow-green");
			} else {
				emailUserInputWrapper.removeClassName("box-shadow-green");
			}
		});
		
		emailOfUserInput.addClassName("width-inner-popup-component");
		emailUserInputWrapper.add(emailUserInputBox, emailOfUserInput);
		emailOfUserInput.setReadOnly(true);
		mappings.put(emailUserInputBox, emailOfUserInput);

		streetNameUserInputBox.addClassName("margin-extra-top-css");
		streetNameUserInputBox.addValueChangeListener(e -> {
			if(e.getValue()) {				
				streetNameUserInputWrapper.addClassName("box-shadow-green");
			} else {
				streetNameUserInputWrapper.removeClassName("box-shadow-green");
			}
		});
		
		streetNameOfUserInput.addClassName("width-inner-popup-component");
		streetNameUserInputWrapper.add(streetNameUserInputBox, streetNameOfUserInput);
		streetNameOfUserInput.setReadOnly(true);
		mappings.put(streetNameUserInputBox, streetNameOfUserInput);

		streetNumberUserInputBox.addClassName("margin-extra-top-css");
		streetNumberUserInputBox.addValueChangeListener(e -> {
			if(e.getValue()) {				
				streetNumberUserInputWrapper.addClassName("box-shadow-green");
			} else {
				streetNumberUserInputWrapper.removeClassName("box-shadow-green");
			}
		});
		
		streetNumberOfUserInput.addClassName("width-inner-popup-component");
		streetNumberUserInputWrapper.add(streetNumberUserInputBox, streetNumberOfUserInput);
		streetNumberOfUserInput.setReadOnly(true);
		mappings.put(streetNumberUserInputBox, streetNumberOfUserInput);

		postalCodeUserInputBox.addClassName("margin-extra-top-css");
		postalCodeUserInputBox.addValueChangeListener(e -> {
			if(e.getValue()) {				
				postalCodeUserInputWrapper.addClassName("box-shadow-green");
			} else {
				postalCodeUserInputWrapper.removeClassName("box-shadow-green");
			}
		});
		
		postalCodeOfUserInput.addClassName("width-inner-popup-component");
		postalCodeUserInputWrapper.add(postalCodeUserInputBox, postalCodeOfUserInput);
		postalCodeOfUserInput.setReadOnly(true);
		mappings.put(postalCodeUserInputBox, postalCodeOfUserInput);

		cityUserInputBox.addClassName("margin-extra-top-css");
		cityUserInputBox.addValueChangeListener(e -> {
			if(e.getValue()) {				
				cityUserInputWrapper.addClassName("box-shadow-green");
			} else {
				cityUserInputWrapper.removeClassName("box-shadow-green");
			}
		});
		
		cityOfUserInput.addClassName("width-inner-popup-component");
		cityUserInputWrapper.add(cityUserInputBox, cityOfUserInput);
		cityOfUserInput.setReadOnly(true);
		mappings.put(cityUserInputBox, cityOfUserInput);
		
		personInputDataLayout.add(
				firstNameUserInputWrapper,
				lastNameUserInputWrapper,
				birthDateUserInputWrapper,
				phoneUserInputWrapper,
				emailUserInputWrapper,
				streetNameUserInputWrapper,
				streetNumberUserInputWrapper,
				postalCodeUserInputWrapper,
				cityUserInputWrapper);
		
		personInputDataLayout.addClassNames(LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
		personInputDataWrapper.addClassNames(LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
	}

	private void createPrevDataLayout() {
		
		//TODO add changes from user input method
		
		firstNameInSystem.addClassName("width-inner-popup-component");
		firstNameSystemWrapper.add(firstNameInSystem);
		firstNameInSystem.setReadOnly(true);

		lastNameInSystem.addClassName("width-inner-popup-component");
		lastNameSystemWrapper.add(lastNameInSystem);
		lastNameInSystem.setReadOnly(true);

		birthDateInSystem.addClassName("width-inner-popup-component");
		birthDateSystemWrapper.add(birthDateInSystem);
		birthDateInSystem.setReadOnly(true);

		phoneInSystem.addClassName("width-inner-popup-component");
		phoneSystemWrapper.add(phoneInSystem);
		phoneInSystem.setReadOnly(true);

		emailInSystem.addClassName("width-inner-popup-component");
		emailSystemWrapper.add(emailInSystem);
		emailInSystem.setReadOnly(true);

		streetNameInSystem.addClassName("width-inner-popup-component");
		streetNameSystemWrapper.add(streetNameInSystem);
		streetNameInSystem.setReadOnly(true);

		streetNumberInSystem.addClassName("width-inner-popup-component");
		streetNumberSystemWrapper.add(streetNumberInSystem);
		streetNumberInSystem.setReadOnly(true);

		postalCodeInSystem.addClassName("width-inner-popup-component");
		postalCodeSystemWrapper.add(postalCodeInSystem);
		postalCodeInSystem.setReadOnly(true);

		cityInSystem.addClassName("width-inner-popup-component");
		citySystemWrapper.add(cityInSystem);
		cityInSystem.setReadOnly(true);
		
		prevDataLayout.add(
				firstNameSystemWrapper,
				lastNameSystemWrapper,
				birthDateSystemWrapper,
				phoneSystemWrapper,
				emailSystemWrapper,
				streetNameSystemWrapper,
				streetNumberSystemWrapper,
				postalCodeSystemWrapper,
				citySystemWrapper);
		
		prevDataLayout.addClassNames(LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
		prevDataWrapper.addClassNames(LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
	}

	public void initDataLeftSide(WaitingPerson person) {
		this.person = person;
		
		firstNameInSystem.setValue(person.getFirstName());
		lastNameInSystem.setValue(person.getLastName());
		birthDateInSystem.setValue(person.getDateOfBirth());
		phoneInSystem.setValue(person.getPhone());
		emailInSystem.setValue(person.getEmail());
		
		MemberData memberData = person.getMemberData();
		streetNameInSystem.setValue(memberData.getStreetName());
		streetNumberInSystem.setValue(memberData.getStreetNumber());
		postalCodeInSystem.setValue(String.valueOf(memberData.getPostalCode()));
		cityInSystem.setValue(memberData.getCityName());
		
	}
	
	public void initDataRightSide(OnboardingData data) {
		firstNameOfUserInput.setValue(data.getFirstName());
		lastNameOfUserInput.setValue(data.getLastName());
		birthDateOfUserInput.setValue(data.getDateOfBirth());
		phoneOfUserInput.setValue(data.getPhone());
		emailOfUserInput.setValue(data.getEmail());
		
		MemberData memberData = data.getMemberData();
		streetNameOfUserInput.setValue(memberData.getStreetName());
		streetNumberOfUserInput.setValue(memberData.getStreetNumber());
		postalCodeOfUserInput.setValue(String.valueOf(memberData.getPostalCode()));
		cityOfUserInput.setValue(memberData.getCityName());
		
	}	
	
	//return person with ticked info to persist outside of the component
	public WaitingPerson returnPersonWithFinalInfo() {
		assignInfo();
		return this.person;
	}

	private void assignInfo() {
		
		this.person.setFirstName(firstNameUserInputBox.getValue() ? firstNameOfUserInput.getValue() : firstNameInSystem.getValue());
		this.person.setLastName(lastNameUserInputBox.getValue() ? lastNameOfUserInput.getValue() : lastNameInSystem.getValue());
		this.person.setDateOfBirth(birthDateUserInputBox.getValue() ? birthDateOfUserInput.getValue() : birthDateInSystem.getValue());
		this.person.setPhone(phoneUserInputBox.getValue() ? phoneOfUserInput.getValue() : phoneInSystem.getValue());
		this.person.setEmail(emailUserInputBox.getValue() ? emailOfUserInput.getValue() : emailInSystem.getValue());
		
		MemberData memberData = this.person.getMemberData();	
		memberData.setStreetName(streetNameUserInputBox.getValue() ? streetNameOfUserInput.getValue() : streetNameInSystem.getValue());
		memberData.setStreetNumber(streetNumberUserInputBox.getValue() ? streetNumberOfUserInput.getValue() : streetNumberInSystem.getValue());
		memberData.setPostalCode(postalCodeUserInputBox.getValue() ? Integer.valueOf(postalCodeOfUserInput.getValue()) : Integer.valueOf(postalCodeInSystem.getValue()));
		memberData.setCityName(cityUserInputBox.getValue() ? cityOfUserInput.getValue() : cityInSystem.getValue());
		
		this.person.setMemberData(memberData);
		this.person.setOnboardingStatus(OnboardingStatus.CAN_BE_MEMBER);
	}
}
