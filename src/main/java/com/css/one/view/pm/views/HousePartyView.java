package com.css.one.view.pm.views;

import com.css.one.pm.services.HousePartyService;
import com.css.one.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import jakarta.annotation.security.PermitAll;

@PageTitle("Parteien")
@Route(value = "parteien/", layout = MainLayout.class)
@RouteAlias(value = "parteien", layout = MainLayout.class)
@PermitAll
public class HousePartyView extends FlexLayout {

	private static final long serialVersionUID = -6751551326625170584L;

	private HousePartyService housePartyService;
	
	public HousePartyView(HousePartyService housePartyService) {
		this.housePartyService = housePartyService;
		
		addClassName("houseparty-view");
	}
}
