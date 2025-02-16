package com.css.one.view.pm.views;

import com.css.one.pm.services.PropertyManagementDataService;
import com.css.one.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import jakarta.annotation.security.PermitAll;

@PageTitle("Hausverwaltung")
@Route(value = "hausverwaltung/informationen/", layout = MainLayout.class)
@RouteAlias(value = "hausverwaltungy", layout = MainLayout.class)
@PermitAll
public class PropertyManagementDataView extends FlexLayout {

	private static final long serialVersionUID = 6135077053412044104L;
	
	private PropertyManagementDataService propertyManagementDataService;
	
	public PropertyManagementDataView(PropertyManagementDataService propertyManagementDataService) {
		this.propertyManagementDataService = propertyManagementDataService;
		
		addClassName("propertymanagementdata-service");
		
	}
	
}
