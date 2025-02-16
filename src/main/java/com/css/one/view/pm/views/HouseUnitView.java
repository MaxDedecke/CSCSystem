package com.css.one.view.pm.views;

import com.css.one.pm.services.HouseUnitService;
import com.css.one.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import jakarta.annotation.security.PermitAll;

@PageTitle("Liegenschaften")
@Route(value = "liegenschaften/", layout = MainLayout.class)
@RouteAlias(value = "liegenschaften", layout = MainLayout.class)
@PermitAll
public class HouseUnitView extends FlexLayout {

	private static final long serialVersionUID = 5326082228917237685L;
	
	private HouseUnitService houseUnitService;
	
	public HouseUnitView(HouseUnitService houseUnitService) {
		this.houseUnitService = houseUnitService;
		
		addClassName("houseunit-view");
	}

}
