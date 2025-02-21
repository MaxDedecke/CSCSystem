package com.css.one.view.pm.views;

import com.css.one.pm.services.PropertyAnnouncementService;
import com.css.one.security.AuthenticatedUser;
import com.css.one.views.MainLayout;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.PermitAll;

@PageTitle("Aushang")
@Route(value = "aushang/", layout = MainLayout.class)
@RouteAlias(value = "aushang", layout = MainLayout.class)
@PermitAll
public class PropertyAnnouncementView extends FlexLayout {

	private static final long serialVersionUID = 1309744637062184098L;
	
	private PropertyAnnouncementService propertyAnnouncementService;
	private AuthenticatedUser authenticatedUser;
	
	public PropertyAnnouncementView(PropertyAnnouncementService propertyAnnouncementService, AuthenticatedUser authenticatedUser) {
		this.propertyAnnouncementService = propertyAnnouncementService;
		this.authenticatedUser = authenticatedUser;
		
		addClassName("propertyannouncement-view");
		
		createMainLayout();
	}
	
	private void createMainLayout() {
		// TODO
		VerticalLayout mainWrapper = new VerticalLayout();
		mainWrapper.addClassNames("complaint-box");
		
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setHeight("100%");
		mainLayout.setWidthFull();
		mainLayout.addClassName(LumoUtility.AlignItems.CENTER);
		H1 help = new H1("Work in progress");

		StreamResource imageResource = new StreamResource("Mousepad.jpeg",
				() -> getClass().getResourceAsStream("/Mousepad.jpeg"));

		Image avatar = new Image(imageResource,"logo_club");
		
		avatar.setWidth(800, Unit.PIXELS);
		avatar.setHeight(600, Unit.PIXELS);
		avatar.addClassName("logo-padding");
		
		mainLayout.add(avatar, help);
		mainWrapper.add(mainLayout);
		add(mainWrapper);
	}
}
