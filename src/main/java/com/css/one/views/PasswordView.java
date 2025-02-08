package com.css.one.views;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCrypt;

import com.css.one.data.PasswordResetToken;
import com.css.one.data.Person;
import com.css.one.data.User;
import com.css.one.services.EmailService;
import com.css.one.services.PasswordResetTokenService;
import com.css.one.services.PersonService;
import com.css.one.services.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.mail.MessagingException;

@PageTitle("Passwort erstellen")
@Route(value = "passwordreset/:token", layout = MainLayout.class)
@RouteAlias("passwordreset")
@AnonymousAllowed
public class PasswordView extends VerticalLayout implements BeforeEnterObserver {
	
	private static final long serialVersionUID = -7194925821596350470L;
	private String token;
	
	private PasswordResetTokenService passwordResetTokenService;
	private UserService userService;	
	private PersonService personService;
	private PasswordResetToken passwordResetToken;
	
	private PasswordField newPasswordField = new PasswordField("Neues Passwort");
	private PasswordField repeatePasswordField = new PasswordField("Neues Passwort wiederholen");

	private EmailService emailService = new EmailService();
	
	private Button savePasswordButton = new Button("Passwort speichern");
	
	public PasswordView(PasswordResetTokenService passwordResetTokenService, UserService userService, PersonService personService) {
		
		this.passwordResetTokenService = passwordResetTokenService;
		this.userService = userService;
		this.personService = personService;
		
		addClassNames("password-view", LumoUtility.Padding.NONE);
		
		setWidth("100%");
	}

	private void createLayout() {
		
		VerticalLayout wrapper = new VerticalLayout();
		wrapper.setWidthFull();
		wrapper.addClassNames(LumoUtility.AlignItems.CENTER);
		
		VerticalLayout mainWrapper = new VerticalLayout();
		mainWrapper.addClassNames(LumoUtility.AlignItems.CENTER);
		mainWrapper.setMaxWidth(600, Unit.PIXELS);
		mainWrapper.addClassName("bestand-box");
		
		H3 header = new H3("Erstelle dein Passwort");
		header.addClassNames("customheader");
		
		savePasswordButton.addClassName("save-button");
		
		newPasswordField.setWidthFull();
		newPasswordField.addClassName(LumoUtility.Margin.MEDIUM);
		newPasswordField.setPattern("^(?=.*[0-9])(?=.*[a-zA-Z]).{8}.*$");
		
		repeatePasswordField.setWidthFull();
		repeatePasswordField.addClassName(LumoUtility.Margin.MEDIUM);
		repeatePasswordField.setPattern("^(?=.*[0-9])(?=.*[a-zA-Z]).{8}.*$");
		
		VerticalLayout layout = new VerticalLayout();
		layout.add(savePasswordButton);
		layout.addClassNames(LumoUtility.AlignItems.END);
		
		savePasswordButton.addClickListener(e -> {
			
			//if both inputs are equal	
			if(validatePasswords()) {		
				
				//set new password
				setNewPassword();
				
				//show please close tab text
				setUIClossable();
			}
		});
		
		mainWrapper.add(header, newPasswordField, repeatePasswordField, layout);	
		wrapper.add(mainWrapper);
		add(wrapper);
	}
	
	private boolean validatePasswords() {
		
		boolean isValidated = true;
		
		if(!newPasswordField.getValue().equals(repeatePasswordField.getValue())) {		
			
			repeatePasswordField.setErrorMessage("Passwörter müssen identintisch sein!");
			
			isValidated = false;
		} else {			
			repeatePasswordField.setErrorMessage("");
		}
		
		if(newPasswordField.getValue().isEmpty()|| repeatePasswordField.getValue().isEmpty()) {
			repeatePasswordField.setErrorMessage("Passwörter müssen identintisch sein!");

			isValidated = false;

		} else {
			repeatePasswordField.setErrorMessage("");
		}
		

		return isValidated;
	}

	private void setUIClossable() {
		
		this.removeAll();
		
		VerticalLayout endLayout = new VerticalLayout();
		endLayout.setWidthFull();
		
		H3 h3 = new H3("Dein Passwort wurde gespeichert");
		H2 h2 = new H2("Du kannst den Tab jetzt schließen.");
		
		h3.addClassName("customheader");
		h2.addClassName("customheader");
		
		endLayout.add(h3, h2);
		
		add(endLayout);
	}

	private void setNewPassword() {

		//Hash pwd, set it and update user account
		User user = passwordResetToken.getUser();
		user.setHashedPassword(BCrypt.hashpw(repeatePasswordField.getValue(), BCrypt.gensalt()));
		
		userService.update(user);
		
		try {
			
			Optional<Person> optionalPerson = personService.get(user.getEntityId());
			
			if(optionalPerson.isPresent()) {			
				String to = optionalPerson.get().getEmail();
				String subject = "Login - " + " Dein Nutzername";
				emailService.sendInitialMemberDataEmail(to, subject, user.getName(), user.getUsername());
				
			} else {
				Notification show = Notification.show("Es ist ein Fehler aufgetreten. Kontaktiere deinen Verein!");
				show.addThemeVariants(NotificationVariant.LUMO_ERROR);
			}
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		
		token = event.getRouteParameters().get("token").orElse("");

        if (token.isBlank()) {
            // No Token
            event.forwardTo("login");
            Notification.show("Kein gültiger Link!");
        } else {
        	//refresh URL
            getElement().executeJs("window.history.replaceState({}, '', window.location.pathname);");
            validateToken();
        }
	}

	private void validateToken() {
		
		Optional<PasswordResetToken> optionalToken = passwordResetTokenService.findByToken(token);
		
		if(optionalToken.isPresent()) {
			LocalDate expirationDate = optionalToken.get().getExpirationDate();
			passwordResetToken = optionalToken.get();

			if(LocalDate.now().isAfter(expirationDate)) {
				UI.getCurrent().navigate("login");
				passwordResetTokenService.delete(passwordResetToken.getId());
				passwordResetToken = null;
				Notification show = Notification.show("Passwort Link abgelaufen. Starte den Prozess bitte neu!");
				show.addThemeVariants(NotificationVariant.LUMO_ERROR);
			} else {
				createLayout();
			}
		} else {			
			UI.getCurrent().navigate("login");
			Notification show = Notification.show("Password Link funktioniert nicht. Starte den Prozess bitte neu!");
			show.addThemeVariants(NotificationVariant.LUMO_ERROR);
		}
	}
}