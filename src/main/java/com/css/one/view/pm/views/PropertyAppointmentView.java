package com.css.one.view.pm.views;

import com.css.one.pm.services.PropertyAppointmentService;
import com.css.one.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import jakarta.annotation.security.PermitAll;

@PageTitle("Termine")
@Route(value = "termine/", layout = MainLayout.class)
@RouteAlias(value = "termine", layout = MainLayout.class)
@PermitAll
public class PropertyAppointmentView extends FlexLayout{

	private static final long serialVersionUID = -7934982152244390633L;
	
	private PropertyAppointmentService propertyAppointmentService;
	
	public PropertyAppointmentView(PropertyAppointmentService propertyAppointmentService) {
		this.propertyAppointmentService = propertyAppointmentService;
		
		addClassName("propertyappointment-view");
	}
	
}
