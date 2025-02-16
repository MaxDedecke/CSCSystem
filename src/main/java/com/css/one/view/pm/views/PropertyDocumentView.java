package com.css.one.view.pm.views;

import com.css.one.pm.services.PropertyDocumentService;
import com.css.one.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import jakarta.annotation.security.PermitAll;

@PageTitle("Dokumente")
@Route(value = "hausverwaltung/dokumente/", layout = MainLayout.class)
@RouteAlias(value = "dokumente", layout = MainLayout.class)
@PermitAll
public class PropertyDocumentView extends FlexLayout{

	private static final long serialVersionUID = 150420459961310738L;

	private PropertyDocumentService propertyDocumentService;
	
	public PropertyDocumentView(PropertyDocumentService propertyDocumentService) {
		this.propertyDocumentService = propertyDocumentService;
		
		addClassName("propertydocument-view");
		
	}
	
}
