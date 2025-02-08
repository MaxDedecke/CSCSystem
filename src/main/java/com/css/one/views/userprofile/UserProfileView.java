package com.css.one.views.userprofile;

import java.io.ByteArrayInputStream;
import java.util.Optional;

import com.css.one.data.Association;
import com.css.one.data.MemberData;
import com.css.one.data.Person;
import com.css.one.data.User;
import com.css.one.security.AuthenticatedUser;
import com.css.one.services.AssociationService;
import com.css.one.services.MemberDataService;
import com.css.one.services.PersonService;
import com.css.one.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.PermitAll;

@PageTitle("Profil")
@Route(value = "profil/", layout = MainLayout.class)
@RouteAlias(value = "profil", layout = MainLayout.class)
@PermitAll
public class UserProfileView extends FlexLayout {

	private static final long serialVersionUID = 8629202302608434700L;
	
	private int associationId = MainLayout.getAssociationId();

    private AuthenticatedUser authenticatedUser;
    private PersonService personService;
    private AssociationService associationService;
    private MemberDataService memberDataService;
    
	public UserProfileView(AuthenticatedUser authenticatedUser, PersonService personService, 
			AssociationService associationService, MemberDataService memberDataService) {
		this.authenticatedUser = authenticatedUser;
		this.personService = personService;
		this.associationService = associationService;
		this.memberDataService = memberDataService;
		
		addClassNames("userprofile-view", LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
		setSizeFull();
		createUserDetailsLayout();
	}
	
	private void createUserDetailsLayout() {
		VerticalLayout mainWrapper = new VerticalLayout();
		mainWrapper.addClassName("profile-box");
		setSizeFull();
		
		HorizontalLayout headerWrapper = new HorizontalLayout();
		headerWrapper.setWidthFull();
		headerWrapper.addClassNames(LumoUtility.Margin.MEDIUM);
		
		HorizontalLayout avatarWrapper = new HorizontalLayout();
		avatarWrapper.setWidthFull();
		avatarWrapper.addClassNames(LumoUtility.Margin.Top.LARGE, LumoUtility.Margin.Bottom.LARGE, LumoUtility.Margin.Left.MEDIUM, "background-user-profile");
		
		Avatar userProfilAvatar = setUserAvatar();
				
		H2 h2General = new H2("Deine Angaben");
		h2General.addClassName("customHeader");
		
		headerWrapper.add(h2General);
		avatarWrapper.add(userProfilAvatar);
		
		mainWrapper.add(avatarWrapper, headerWrapper, createUserDataLayout());
		add(mainWrapper);
	}

	private Avatar setUserAvatar() {
		Avatar avatar = new Avatar("user-avatar");
		StreamResource resource;

		if (authenticatedUser.get().isPresent()) {

			if (authenticatedUser.get().get().getProfilePicture() == null) {
				resource = new StreamResource("logoCodeGreen.png",
						() -> getClass().getResourceAsStream("/logoCodeGreen.png"));
			} else {
				resource = new StreamResource("profile-pic",
						() -> new ByteArrayInputStream(authenticatedUser.get().get().getProfilePicture()));
			}

		} else {
			resource = new StreamResource("profile-pic",
					() -> new ByteArrayInputStream(authenticatedUser.get().get().getProfilePicture()));
		}

		avatar.setImageResource(resource);
		avatar.setMinHeight(200, Unit.PIXELS);
		avatar.setMinWidth(200, Unit.PIXELS);
		
		avatar.addClassNames("round-avatar");
		
		avatar.addThemeVariants(AvatarVariant.LUMO_LARGE);
		avatar.getElement().setAttribute("tabindex", "-1");
		return avatar;
	}

	private Component createUserDataLayout() {
		
		VerticalLayout innerWrapper = new VerticalLayout();
		innerWrapper.setSizeFull();
		innerWrapper.addClassNames("inner-scroll-bar");
		innerWrapper.getStyle().set("overflow", "auto"); // Scrollbarkeit aktivieren

		
		if(authenticatedUser.get().isPresent()) {
			
			User user = authenticatedUser.get().get();
			
			Optional<Person> personById = personService.findById(associationId, user.getEntityId());
			Optional<Association> associationById = associationService.get(Integer.toUnsignedLong(associationId));
			
			if (personById.isPresent() && associationById.isPresent()) {		

				Person person = personById.get();
				Association association = associationById.get();
				Optional<MemberData> memberDataByPerson = memberDataService.findByMember(person);
				
				TextField associationField = new TextField("Verein");
				associationField.setWidthFull();
				associationField.setValue(association.getName());
				associationField.setReadOnly(true);
				innerWrapper.add(associationField);
				
				innerWrapper.add(associationField, new Hr());
				
				H3 personalDataHeader = new H3("Infos zu deiner Person");
				personalDataHeader.addClassNames("customHeader");
				innerWrapper.add(new Hr(), personalDataHeader);
				
				TextField nameField = new TextField("Name");
				nameField.setWidthFull();
				nameField.setValue(user.getName());
				nameField.setReadOnly(true);
				innerWrapper.add(nameField);
				
				TextField userNameField = new TextField("Nutzername");
				userNameField.setWidthFull();
				userNameField.setValue(user.getUsername());
				userNameField.setReadOnly(true);
				innerWrapper.add(userNameField);
				
				TextField emailField = new TextField("Email");
				emailField.setWidthFull();
				emailField.setValue(person.getEmail());
				emailField.setReadOnly(true);
				innerWrapper.add(emailField);
				
				TextField phoneField = new TextField("Telefonnummer");
				phoneField.setWidthFull();
				phoneField.setValue(person.getPhone());
				phoneField.setReadOnly(true);
				innerWrapper.add(emailField);
				
				H3 addressDataHeader = new H3("Adressdaten");
				addressDataHeader.addClassNames("customHeader");
				innerWrapper.add(new Hr(), addressDataHeader);
				
				if (memberDataByPerson.isPresent()) {
					MemberData memberData = memberDataByPerson.get();
					
					TextField streetNameField = new TextField("Straße");
					streetNameField.setWidthFull();
					streetNameField.setValue(memberData.getStreetName());
					streetNameField.setReadOnly(true);
					innerWrapper.add(streetNameField);
					
					TextField houseNumberField = new TextField("Hausnummer");
					houseNumberField.setWidthFull();
					houseNumberField.setValue(memberData.getStreetNumber());
					houseNumberField.setReadOnly(true);
					innerWrapper.add(houseNumberField);
					
					TextField postalCodeField = new TextField("Postleitzahl");
					postalCodeField.setWidthFull();
					postalCodeField.setValue(String.valueOf(memberData.getPostalCode()));
					postalCodeField.setReadOnly(true);
					innerWrapper.add(postalCodeField);
					
					TextField cityField = new TextField("Stadt");
					cityField.setWidthFull();
					cityField.setValue(memberData.getCityName());
					cityField.setReadOnly(true);
					innerWrapper.add(cityField);
				}
			} else {
				
				H2 h2 = new H2("Keine Accountinformationen abrufbar, da der aktuelle Account ein Systemadmin ist.");
				h2.addClassName("customHeader");
				innerWrapper.add(h2);
			}	
			
		} else {
			
			H2 h2 = new H2("Keine Accountinformationen abrufbar. Wende dich bitte an den Support oder deinen Ansprechpartner im Verein.");
			h2.addClassName("customHeader");
			innerWrapper.add(h2);
		}	
		
		return innerWrapper;
	}
}
