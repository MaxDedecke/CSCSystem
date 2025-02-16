package com.css.one.view.pm.views;

import com.css.one.pm.services.PropertyAppointmentService;
import com.css.one.views.MainLayout;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;

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
		
		createMainLayout();
	}
	
	private void createMainLayout() {
		// TODO
		VerticalLayout mainWrapper = new VerticalLayout();
		mainWrapper.addClassNames("complaint-box");
		
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setHeight("100%");
		mainLayout.setWidthFull();
		mainLayout.addClassName(LumoUtility.JustifyContent.CENTER);
		H1 help = new H1("Work in progress.");

		StreamResource imageResource = new StreamResource("potteriepng.png",
				() -> getClass().getResourceAsStream("/potteriepng.png"));

		Avatar avatar = new Avatar("logo_club");
		avatar.setImageResource(imageResource);
		avatar.setHeight(250, Unit.PIXELS);
	    avatar.setWidth(250, Unit.PIXELS);
		
		mainLayout.add(avatar, help);
		mainWrapper.add(mainLayout);
		add(mainWrapper);
	}
}
