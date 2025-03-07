package com.css.one.view.pm.views;

import org.vaadin.lineawesome.LineAwesomeIcon;

import com.css.one.pm.services.PropertyDocumentService;
import com.css.one.security.AuthenticatedUser;
import com.css.one.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.PermitAll;

@PageTitle("Dokumente")
@Route(value = "hausverwaltung/dokumente/", layout = MainLayout.class)
@RouteAlias(value = "dokumente", layout = MainLayout.class)
@PermitAll
public class PropertyDocumentView extends FlexLayout{

	private static final long serialVersionUID = 150420459961310738L;

	private PropertyDocumentService propertyDocumentService;
	private AuthenticatedUser authenticatedUser;
	
	public PropertyDocumentView(PropertyDocumentService propertyDocumentService, AuthenticatedUser authenticatedUser) {
		this.propertyDocumentService = propertyDocumentService;
		this.authenticatedUser = authenticatedUser;
		
		addClassName("propertydocument-view");
		
		createMainLayout();
	}
	
	private void createMainLayout() {
		// TODO
		VerticalLayout mainWrapper = new VerticalLayout();
		
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.addClassNames("complaint-box", LumoUtility.Padding.NONE, LumoUtility.Margin.NONE);
		
		mainLayout.setHeight("100%");
		mainLayout.setWidthFull();
		mainLayout.addClassName(LumoUtility.AlignItems.CENTER);
		H1 help = new H1("Work in progress");

		mainLayout.add(help);
		mainWrapper.add(createButtonAndInfoComponent(), mainLayout);
		add(mainWrapper);
	}
	
	private Component createButtonAndInfoComponent() {
		HorizontalLayout wrapper = new HorizontalLayout();
		wrapper.setWidthFull();
		wrapper.addClassNames("header-bar-custom-blue");

		Button buttonAddPerson = new Button("Dokument hochladen");
		buttonAddPerson.setIcon(LineAwesomeIcon.UPLOAD_SOLID.create());
		buttonAddPerson.addClassName("button-neutral");

		buttonAddPerson.addClickListener(e -> {
//			headerPersonInfo.setText("Person hinzufügen");
//			this.waitingPerson = null;
//			personInfoDialog.open();
		});

		FlexLayout flexWrapper = new FlexLayout();
		flexWrapper.addClassNames(LumoUtility.AlignItems.END);
		flexWrapper.setWidthFull();

		VerticalLayout secondWrapper = new VerticalLayout();
		secondWrapper.addClassNames(LumoUtility.JustifyContent.END, LumoUtility.AlignContent.END);
		secondWrapper.setSpacing(false);
		secondWrapper.setPadding(false);

		VerticalLayout innerWrapper = new VerticalLayout();
		innerWrapper.addClassNames(LumoUtility.Border.LEFT, LumoUtility.BorderRadius.NONE, "padding-extra-top");
		innerWrapper.setSpacing(false);
		innerWrapper.setPadding(false);

		HorizontalLayout bottomLayout = new HorizontalLayout();
		bottomLayout.addClassNames(LumoUtility.Padding.NONE, LumoUtility.Padding.Left.MEDIUM, LumoUtility.Margin.NONE);
//		memberCount = new H3("Statistik");
//		memberCount.addClassNames("header-statistics", "no-extra-space");
//		bottomLayout.add(memberCount);

		HorizontalLayout statisticsLayout = new HorizontalLayout();
		statisticsLayout.addClassNames(LumoUtility.Padding.NONE, LumoUtility.Padding.Left.MEDIUM,
				LumoUtility.Margin.NONE, "header-statistics-item");
//		memberCountNumber = new H3(
//				"Wartendene Personen: " + waitingPersonService.findAllByAssociation(associationId).size());
//		memberCountNumber.addClassName("no-extra-space");
//		statisticsLayout.add(memberCountNumber);

		innerWrapper.add(bottomLayout, statisticsLayout);
		flexWrapper.add(secondWrapper, innerWrapper);

		wrapper.add(buttonAddPerson, flexWrapper);
		flexWrapper.setFlexGrow(3, secondWrapper);
		flexWrapper.setFlexGrow(2, innerWrapper);
		return wrapper;
	}
}
