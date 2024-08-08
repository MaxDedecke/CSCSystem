package com.css.one.views.login;

import com.css.one.security.AuthenticatedUser;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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
        this.addClassNames("css-system-one");
        LoginI18n i18n = LoginI18n.createDefault(); 
        i18n.setHeader(new LoginI18n.Header());    
        i18n.getHeader().setTitle("Ceres"); 
        i18n.setAdditionalInformation(null);
        i18n.getForm().setSubmit("Login");
        i18n.getForm().setPassword("Passwort"); 
        i18n.getErrorMessage().setPassword("Passwort fehlt!");
        i18n.getForm().setUsername("Nutzername");
        i18n.getErrorMessage().setUsername("Nutzername fehlt!");
        i18n.getForm().setTitle("Einloggen");
        setI18n(i18n);
        
        VerticalLayout plant = new VerticalLayout();
        plant.setWidthFull();
        plant.addClassNames(LumoUtility.JustifyContent.CENTER);
        
        StreamResource imageResource = new StreamResource("CLOS.png",
                () -> getClass().getResourceAsStream("/CLOS.png"));
 
        Image logoImage = new Image(imageResource, ""); 
        logoImage.addClassNames(LumoUtility.Margin.Left.XLARGE);
        
        logoImage.setHeight(250, Unit.PIXELS);
        plant.add(logoImage);
  
        setTitle(plant); 
        i18n.getForm().setPassword("Passwort");
        
        setForgotPasswordButtonVisible(false);
        setOpened(true); 
        addClassName("login-view-login-overlay-1");

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
