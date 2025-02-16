package com.css.one.view.pm.views;

import com.css.one.pm.services.PropertyAnnouncementService;
import com.css.one.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import jakarta.annotation.security.PermitAll;

@PageTitle("Aushang")
@Route(value = "aushang/", layout = MainLayout.class)
@RouteAlias(value = "aushang", layout = MainLayout.class)
@PermitAll
public class PropertyAnnouncementView extends FlexLayout {

	private static final long serialVersionUID = 1309744637062184098L;
	
	private PropertyAnnouncementService propertyAnnouncementService;
	
	public PropertyAnnouncementView(PropertyAnnouncementService propertyAnnouncementService) {
		this.propertyAnnouncementService = propertyAnnouncementService;
		
		addClassName("propertyannouncement-view");
		
	}
	
}
