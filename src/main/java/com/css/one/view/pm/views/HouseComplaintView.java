package com.css.one.view.pm.views;

import com.css.one.pm.services.HouseComplaintService;
import com.css.one.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import jakarta.annotation.security.PermitAll;

//@PageTitle("Profil")
//@Route(value = "profil/", layout = MainLayout.class)
//@RouteAlias(value = "profil", layout = MainLayout.class)
//@PermitAll
public class HouseComplaintView extends FlexLayout {

	private static final long serialVersionUID = 6007137345382107212L;

	private HouseComplaintService houseComplaintService;
	
	public HouseComplaintView(HouseComplaintService houseComplaintService) {
		this.houseComplaintService = houseComplaintService;
		
		
	}
	
	
}
