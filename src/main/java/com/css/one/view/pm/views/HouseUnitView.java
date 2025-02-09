package com.css.one.view.pm.views;

import com.css.one.pm.services.HouseUnitService;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class HouseUnitView extends FlexLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5326082228917237685L;
	
	private HouseUnitService houseUnitService;
	
	public HouseUnitView(HouseUnitService houseUnitService) {
		this.houseUnitService = houseUnitService;
	}

}
