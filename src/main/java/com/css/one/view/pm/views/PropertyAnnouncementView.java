package com.css.one.view.pm.views;

import com.css.one.pm.services.PropertyAnnouncementService;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class PropertyAnnouncementView extends FlexLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1309744637062184098L;
	
	private PropertyAnnouncementService propertyAnnouncementService;
	
	public PropertyAnnouncementView(PropertyAnnouncementService propertyAnnouncementService) {
		this.propertyAnnouncementService = propertyAnnouncementService;
		
		addClassName("propertyannouncement-view");
		
	}
	
}
