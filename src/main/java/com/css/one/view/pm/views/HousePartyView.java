package com.css.one.view.pm.views;

import com.css.one.pm.services.HousePartyService;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class HousePartyView extends FlexLayout {

	private static final long serialVersionUID = -6751551326625170584L;

	private HousePartyService housePartyService;
	
	public HousePartyView(HousePartyService housePartyService) {
		this.housePartyService = housePartyService;
		
		addClassName("houseparty-view");
	}
}
