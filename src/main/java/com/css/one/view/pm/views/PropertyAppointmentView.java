package com.css.one.view.pm.views;

import com.css.one.pm.services.PropertyAppointmentService;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class PropertyAppointmentView extends FlexLayout{

	private static final long serialVersionUID = -7934982152244390633L;
	
	private PropertyAppointmentService propertyAppointmentService;
	
	public PropertyAppointmentView(PropertyAppointmentService propertyAppointmentService) {
		this.propertyAppointmentService = propertyAppointmentService;
	}
	
}
