package com.css.one.views.login;

import com.css.one.security.AuthenticatedUser;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

@AnonymousAllowed
@PageTitle("Login")
@Route(value = "login")
@CssImport(value = "/themes/css-system-one/views/login-view.css", themeFor = "vaadin-login-overlay-wrapper")
public class LoginView extends LoginOverlay implements BeforeEnterObserver {

    private static final long serialVersionUID = 5891493543997686089L; 
	private final AuthenticatedUser authenticatedUser;

    public LoginView(AuthenticatedUser authenticatedUser) {  
        this.authenticatedUser = authenticatedUser;
        setAction(RouteUtil.getRoutePath(VaadinService.getCurrent().getContext(), getClass())); 
        this.addClassNames("vaadin-login-form");
        LoginI18n i18n = LoginI18n.createDefault(); 
        i18n.setHeader(new LoginI18n.Header());    
        i18n.getHeader().setTitle("Ceres"); 
        i18n.getHeader().setDescription("We grow modern systems");
        i18n.setAdditionalInformation(null);
        i18n.getForm().setSubmit("Login");
        i18n.getForm().setPassword("Passwort");
        i18n.getForm().setUsername("Nutzername");
        i18n.getForm().setTitle("Einloggen");
        setI18n(i18n);
        
        Div plant = new Div(); 
        plant.addClassName("plant");
        
        StreamResource imageResource = new StreamResource("NewLogo050724_transparent.png",
                () -> getClass().getResourceAsStream("/NewLogo050724_transparent.png"));

        Image logoImage = new Image(imageResource, "");
        logoImage.addClassNames(LumoUtility.Margin.Left.LARGE);
        
        logoImage.setHeight(250, Unit.PIXELS);
        plant.add(logoImage);
  
        setTitle(plant);
        
        i18n.getForm().setPassword("Passwort");
        
        setForgotPasswordButtonVisible(false);
        setOpened(true); 
        
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticatedUser.get().isPresent()) {
            // Already logged in
            setOpened(false);
            event.forwardTo("");
        }

        setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }
}
