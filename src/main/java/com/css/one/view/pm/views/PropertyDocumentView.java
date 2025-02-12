package com.css.one.view.pm.views;

import com.css.one.pm.services.PropertyDocumentService;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class PropertyDocumentView extends FlexLayout{

	private static final long serialVersionUID = 150420459961310738L;

	private PropertyDocumentService propertyDocumentService;
	
	public PropertyDocumentView(PropertyDocumentService propertyDocumentService) {
		this.propertyDocumentService = propertyDocumentService;
		
		addClassName("propertydocument-view");
		
	}
	
}
