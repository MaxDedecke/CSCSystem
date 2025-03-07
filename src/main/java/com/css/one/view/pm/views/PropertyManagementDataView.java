package com.css.one.view.pm.views;

import org.vaadin.lineawesome.LineAwesomeIcon;

import com.css.one.data.enums.Role;
import com.css.one.pm.services.PropertyManagementDataService;
import com.css.one.security.AuthenticatedUser;
import com.css.one.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.PermitAll;

@PageTitle("Infocenter")
@Route(value = "hausverwaltung/informationen/", layout = MainLayout.class)
@RouteAlias(value = "hausverwaltungy", layout = MainLayout.class)
@PermitAll
public class PropertyManagementDataView extends FlexLayout {

	private static final long serialVersionUID = 6135077053412044104L;
	
	private PropertyManagementDataService propertyManagementDataService;
	private AuthenticatedUser authenticatedUser;
	
	private H3 memberCount = new H3("Statistik");
	private H3 memberCountNumber;
	
	private SvgIcon iconEditGeneral = LineAwesomeIcon.PEN_SOLID.create();
	private SvgIcon iconEditFacilityMgt = LineAwesomeIcon.PEN_SOLID.create();
	private SvgIcon iconEditPlumber = LineAwesomeIcon.PEN_SOLID.create();
	private SvgIcon iconEditKeyService = LineAwesomeIcon.PEN_SOLID.create();
	
	public PropertyManagementDataView(PropertyManagementDataService propertyManagementDataService, AuthenticatedUser authenticatedUser) {
		this.propertyManagementDataService = propertyManagementDataService;
		this.authenticatedUser = authenticatedUser;
		
		addClassNames("propertymanagementdata-view");
		
		createMainLayout();
	}
	
	private void createMainLayout() {
		
		VerticalLayout mainWrapper = new VerticalLayout();
		mainWrapper.addClassNames(LumoUtility.Margin.NONE, LumoUtility.Margin.Bottom.MEDIUM, LumoUtility.Padding.MEDIUM, "responsive-layout");
		
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setWidthFull();
		mainLayout.addClassNames("complaint-box", LumoUtility.AlignItems.CENTER, LumoUtility.Padding.MEDIUM);

		H1 h1 = new H1("Allgemeines");
		h1.addClassName("customHeader");
		
		HorizontalLayout propertyMgtHeaderWrapper = new HorizontalLayout();
		propertyMgtHeaderWrapper.setWidthFull();
		h1.addClassNames(LumoUtility.Margin.Top.MEDIUM);
		h1.setMinWidth(200, Unit.PIXELS);
		
		VerticalLayout innerIconEditWrapper = new VerticalLayout();
		innerIconEditWrapper.setWidthFull();
		innerIconEditWrapper.setHeightFull();
		innerIconEditWrapper.addClassNames(LumoUtility.AlignItems.END, "custom-margin-top-edit-icon");
		
		iconEditGeneral.addClassNames("icon-edit-blue");
		
		if (authenticatedUser.get().get().getRoles().contains(Role.ADMIN)) {
			innerIconEditWrapper.add(iconEditGeneral);
		}
		
		iconEditGeneral.addClickListener(e -> {
			
//			if(isOnEdit) {
//				//click when edit is active -> save data
//				iconEditGeneral.setSrc(LineAwesomeIcon.PEN_SOLID.create().getSrc());
//				
//				if(validateAssociationData()) {		
//					
//					saveAssociationData();
//					isOnEdit = false;
//					
//					if (hasChanged) {
//						Notification notification = Notification.show("Daten des Vereins aktualisiert!");
//						notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
//					}
//					hasChanged = false;
//				} else {
//					Notification notification = Notification.show("Validierung der Daten des Vereins fehlgeschlagen!");
//					notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
//				}
//				
//			} else {
//				//click when edit is not active -> edit data
//				iconEdit.setSrc(LineAwesomeIcon.SAVE_SOLID.create().getSrc());
//				isOnEdit = true;
//			}
//			
//			refreshDataFieldsReadOnly(!isOnEdit);
			
		});
		
		propertyMgtHeaderWrapper.add(h1, innerIconEditWrapper);
		
		HorizontalLayout innerWrapper = new HorizontalLayout();
		innerWrapper.addClassName("responsive-layout");
		
		FormLayout formLayout = new FormLayout();
		TextField textFieldNameOfManagement = new TextField("Name");
		TextField streetNameOfManagement = new TextField("Straße");
		TextField streetNumberOfManagement = new TextField("Hausnummer");
		TextField postalCodeOfManagement = new TextField("PLZ");
		TextField cityOfManagement = new TextField("Stadt");
		TextField emailOfManagement = new TextField("Email");
		TextField phoneOfManagement = new TextField("Telefonnummer");
		
		textFieldNameOfManagement.setReadOnly(true);
		streetNameOfManagement.setReadOnly(true);
		streetNumberOfManagement.setReadOnly(true);
		postalCodeOfManagement.setReadOnly(true);
		cityOfManagement.setReadOnly(true);
		emailOfManagement.setReadOnly(true);
		phoneOfManagement.setReadOnly(true);
		
		formLayout.setColspan(textFieldNameOfManagement, 2);
		formLayout.setColspan(streetNameOfManagement, 2);
		formLayout.setColspan(streetNumberOfManagement, 3);
		formLayout.setColspan(postalCodeOfManagement, 1);
		formLayout.setColspan(cityOfManagement, 1);
		formLayout.setColspan(emailOfManagement, 2);
		formLayout.setColspan(phoneOfManagement, 2);
		
		formLayout.setResponsiveSteps(
		        // Use one column by default
		        new ResponsiveStep("0", 1),
		        // Use two columns, if layout's width exceeds 500px
		        new ResponsiveStep("500px", 2));

		
		VerticalLayout additionalInfoWrapper = new VerticalLayout();
		additionalInfoWrapper.addClassName(LumoUtility.AlignItems.CENTER);
		
		H3 h3Help = new H3("Andersweitige Information");
		
		additionalInfoWrapper.add(h3Help);
		
		Span nameFacilityMgt = new Span("Hausmeister Krause");
		Span phoneFacilityMgt = new Span("0941/12345678910");

		VerticalLayout contentFacilityMgt = new VerticalLayout(nameFacilityMgt, phoneFacilityMgt);
		contentFacilityMgt.setSpacing(false);
		contentFacilityMgt.setPadding(false);

		Details detailsFacilityMgt = new Details("Hausmeister", contentFacilityMgt);
		detailsFacilityMgt.setOpened(true);
		
		additionalInfoWrapper.add(detailsFacilityMgt);
		
		Span nameKeyService = new Span("Schlüsseldienst Mayer");
		Span phoneKeyService = new Span("0941/12345678910");

		VerticalLayout contentKeyService = new VerticalLayout(nameKeyService, phoneKeyService);
		contentKeyService.setSpacing(false);
		contentKeyService.setPadding(false);

		Details detailsKeyService = new Details("Schlüsseldienst", contentKeyService);
		detailsKeyService.setOpened(true);
		
		additionalInfoWrapper.add(detailsKeyService);
		
		Span namePlumber = new Span("Sanitärdienst Gustav");
		Span phonePlumber = new Span("0941/12345678910");

		VerticalLayout contentPlumber = new VerticalLayout(namePlumber, phonePlumber);
		contentPlumber.setSpacing(false);
		contentPlumber.setPadding(false);

		Details detailsPlumber = new Details("Sanitärdienst", contentPlumber);
		detailsPlumber.setOpened(true);
		
		additionalInfoWrapper.add(detailsPlumber);
		
		formLayout.add(propertyMgtHeaderWrapper, textFieldNameOfManagement, streetNameOfManagement, streetNumberOfManagement, postalCodeOfManagement, cityOfManagement, emailOfManagement, phoneOfManagement);
		
		innerWrapper.add(formLayout, additionalInfoWrapper);
		
		mainLayout.add(innerWrapper);
		
		mainWrapper.add(mainLayout);
		add(mainWrapper);
	}
	
	
}
