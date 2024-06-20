package com.css.one.views;

import com.css.one.data.User;
import com.css.one.security.AuthenticatedUser;
import com.css.one.views.arbeitsplanung.ArbeitsplanungView;
import com.css.one.views.finanzen.FinanzenView;
import com.css.one.views.mitglieder.MitgliederView;
import com.css.one.views.rechtliches.RechtlichesView;
import com.css.one.views.verein.VereinView;
import com.css.one.views.waitinglist.WaitingListView;
import com.css.one.views.warenlager.WarenlagerView;
import com.css.one.views.übersicht.ÜbersichtView;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.LumoUtility;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Optional;
import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private static final long serialVersionUID = 6836033218825579037L;

	private H1 viewTitle;

    private AuthenticatedUser authenticatedUser;
    private AccessAnnotationChecker accessChecker;
        
    static int associationId;

    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
    	
    	VerticalLayout layout = new VerticalLayout();
    	
        Span appName = new Span("CSCSystem");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);

        StreamResource imageResource = new StreamResource("logoCSCSystemWhite.png",
                () -> getClass().getResourceAsStream("/logoCSCSystemWhite.png"));

        Image logoImage = new Image(imageResource, "");
        logoImage.setHeight(250, Unit.PIXELS);
        
        layout.setAlignItems(Alignment.CENTER);
        layout.add(logoImage, appName, new Hr());
        
        Header header = new Header(layout);
        

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        if (accessChecker.hasAccess(ÜbersichtView.class)) {
            nav.addItem(new SideNavItem("Übersicht", ÜbersichtView.class, LineAwesomeIcon.GLOBE_SOLID.create()));

        }
        if (accessChecker.hasAccess(FinanzenView.class)) {
            nav.addItem(
                    new SideNavItem("Finanzen", FinanzenView.class, LineAwesomeIcon.MONEY_BILL_WAVE_SOLID.create()));

        }
        if (accessChecker.hasAccess(ArbeitsplanungView.class)) {
            nav.addItem(new SideNavItem("Arbeitsplanung", ArbeitsplanungView.class,
                    LineAwesomeIcon.BUSINESS_TIME_SOLID.create()));

        }
        if (accessChecker.hasAccess(RechtlichesView.class)) {
            nav.addItem(new SideNavItem("Rechtliches", RechtlichesView.class,
                    LineAwesomeIcon.BALANCE_SCALE_SOLID.create()));

        }
        if (accessChecker.hasAccess(WarenlagerView.class)) {
            nav.addItem(new SideNavItem("Ware | Abgabe", WarenlagerView.class, LineAwesomeIcon.CANNABIS_SOLID.create()));

        }
        if (accessChecker.hasAccess(MitgliederView.class)) {
            nav.addItem(new SideNavItem("Mitglieder", MitgliederView.class, LineAwesomeIcon.USERS_SOLID.create()));

        }
        if (accessChecker.hasAccess(WaitingListView.class)) {
        	nav.addItem(new SideNavItem("Warteliste", WaitingListView.class, LineAwesomeIcon.LIST_SOLID.create()));
        	
        }
        if (accessChecker.hasAccess(VereinView.class)) {
            nav.addItem(new SideNavItem("Verein", VereinView.class, LineAwesomeIcon.STORE_ALT_SOLID.create()));

        }

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            
            associationId = user.getAssociationId();
            Avatar avatar = new Avatar(user.getName());
            StreamResource resource = new StreamResource("profile-pic",
                    () -> new ByteArrayInputStream(user.getProfilePicture()));
            avatar.setImageResource(resource);
            avatar.setThemeName("xsmall");
            avatar.getElement().setAttribute("tabindex", "-1");

            MenuBar userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast");

            MenuItem userName = userMenu.addItem("");
            Div div = new Div();
            div.add(avatar);
            div.add(user.getName());
            div.add(new Icon("lumo", "dropdown"));
            div.getElement().getStyle().set("display", "flex");
            div.getElement().getStyle().set("align-items", "center");
            div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
            userName.add(div);
            userName.getSubMenu().addItem("Sign out", e -> {
                authenticatedUser.logout();
            });

            layout.add(userMenu);
        } else {
            Anchor loginLink = new Anchor("login", "Sign in");
            layout.add(loginLink);
        }

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
    
    public static int getAssociationId() {
    	return associationId;
    }
}
