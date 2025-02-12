package com.css.one.view.pm.views;

import com.css.one.pm.services.PropertyManagementDataService;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class PropertyManagementDataView extends FlexLayout {

	private static final long serialVersionUID = 6135077053412044104L;
	
	private PropertyManagementDataService propertyManagementDataService;
	
	public PropertyManagementDataView(PropertyManagementDataService propertyManagementDataService) {
		this.propertyManagementDataService = propertyManagementDataService;
		
		addClassName("propertymanagementdata-service");
		
	}
	
}
