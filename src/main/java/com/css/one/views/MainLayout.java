package com.css.one.views;

import java.io.ByteArrayInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import org.vaadin.lineawesome.LineAwesomeIcon;

import com.css.one.data.SystemVersion;
import com.css.one.data.User;
import com.css.one.data.enums.Role;
import com.css.one.migrations.DB;
import com.css.one.security.AuthenticatedUser;
import com.css.one.services.PropertyService;
import com.css.one.views.arbeitsplanung.ArbeitsplanungView;
import com.css.one.views.diary.DiaryView;
import com.css.one.views.finanzen.FinanzenView;
import com.css.one.views.mitglieder.MitgliederView;
import com.css.one.views.output.OutputView;
import com.css.one.views.rechtliches.RechtlichesView;
import com.css.one.views.settings.ConfigurationView;
import com.css.one.views.verein.VereinView;
import com.css.one.views.waitinglist.WaitingListView;
import com.css.one.views.warenlager.WarenlagerView;
import com.css.one.views.übersicht.ÜbersichtView;
import com.vaadin.flow.component.Text;
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
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout implements BeforeEnterObserver {

    private static final long serialVersionUID = 6836033218825579037L;

	private H1 viewTitle;

    private AuthenticatedUser authenticatedUser;
    private AccessAnnotationChecker accessChecker;
        
    static int associationId; 
    static Set<Role> currentUserRoles;
    
    private SideNav nav = new SideNav();
    
    //onboarding navigation item
    private SideNavItem sideNavItemOnboarding = new SideNavItem("Onboarding", OnboardingView.class, LineAwesomeIcon.HANDS_HELPING_SOLID.create());
    private SideNavItem sideNavItemPasswordReset = new SideNavItem("Passwort", PasswordView.class, LineAwesomeIcon.KEY_SOLID.create());

    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;
        addClassName("main-layout");
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent(); 
        addClassName("main-layout-app-layout-1");
        setDrawerOpened(false);
        new PropertyService();
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
        StreamResource imageResource = new StreamResource("logoCodeGreen.png",
                () -> getClass().getResourceAsStream("/logoCodeGreen.png"));

        Avatar avatar = new Avatar("logo_club");
        avatar.setImageResource(imageResource);
        avatar.addClassNames(LumoUtility.Padding.LARGE);
        avatar.setWidth(200, Unit.PIXELS);
        avatar.setHeight(150, Unit.PIXELS);

        layout.setAlignItems(Alignment.CENTER);
        layout.addClassNames(LumoUtility.Padding.XSMALL);
        layout.add(avatar);
        Header header = new Header(layout);
        
        Scroller scroller = new Scroller(createNavigation());

        HorizontalLayout versionLayout = new HorizontalLayout();
        versionLayout.setWidth("100%");
        versionLayout.addClassNames(LumoUtility.JustifyContent.CENTER, LumoUtility.Padding.NONE, "main");
        
        setCurrentVersion(versionLayout);
        
        layout.add(versionLayout);
        
        Hr hr2 = new Hr();
        hr2.addClassNames(LumoUtility.Margin.SMALL);
        
        addToDrawer(header, scroller, createFooter(), versionLayout);
    }

	private void setCurrentVersion(HorizontalLayout versionLayout) {
		// Load current version from database and set string

		String version = "";

		// Create a connection to database
		try (var connection = DB.connect()) {
			System.out.println("Load current version");
			
			var sql = "SELECT * FROM system_version WHERE is_active = true";
			
			try {
				var statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(sql);

				SystemVersion sysVersion = new SystemVersion();
				while (rs.next()) {
					sysVersion.setId(rs.getLong("id"));
					sysVersion.setVersionNumber(rs.getString("version_number"));
					sysVersion.setVersionInteger(rs.getInt("version_integer"));
					sysVersion.setReleaseDate(rs.getObject("release_date", LocalDate.class));
					sysVersion.setCreatedAt(rs.getObject("created_at", LocalDate.class));
					sysVersion.setUpdatedAt(rs.getObject("updated_at", LocalDate.class));
					sysVersion.setDescription(rs.getString("description"));
					sysVersion.setActive(rs.getBoolean("is_active"));
					sysVersion.setMigrated(rs.getBoolean("is_migrated"));
				}
				
				version = sysVersion.getVersionNumber();
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			

		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}

		versionLayout.add(new Text(version));
	}

	private SideNav createNavigation() {

        nav.addClassNames("vaadin-app-layout");
        nav.setWidth("100%");
        
		if (authenticatedUser.get().isPresent()) {

			if (accessChecker.hasAccess(ÜbersichtView.class)) {
				nav.addItem(new SideNavItem("Übersicht", ÜbersichtView.class, LineAwesomeIcon.GLOBE_SOLID.create()));

				clearNavigationFromItem(nav, sideNavItemOnboarding);
			}

			if (authenticatedUser.get().get().getRoles().iterator().next().equals(Role.ADMIN)) {

				if (accessChecker.hasAccess(FinanzenView.class)) {
					nav.addItem(new SideNavItem("Finanzen", FinanzenView.class,
							LineAwesomeIcon.MONEY_BILL_WAVE_SOLID.create()));

				}
				if (accessChecker.hasAccess(DiaryView.class)) {
					nav.addItem(new SideNavItem("Tagebuch", DiaryView.class, LineAwesomeIcon.BOOK_OPEN_SOLID.create()));

				}
				if (accessChecker.hasAccess(ArbeitsplanungView.class)) {
					nav.addItem(new SideNavItem("Zeiterfassung", ArbeitsplanungView.class,
							LineAwesomeIcon.BUSINESS_TIME_SOLID.create()));
				}

				if (accessChecker.hasAccess(RechtlichesView.class)) {
					nav.addItem(new SideNavItem("Rechtliches", RechtlichesView.class,
							LineAwesomeIcon.BALANCE_SCALE_SOLID.create()));

				}
				if (accessChecker.hasAccess(WarenlagerView.class)) {
					nav.addItem(
							new SideNavItem("Bestand", WarenlagerView.class, LineAwesomeIcon.CANNABIS_SOLID.create()));

				}
				if (accessChecker.hasAccess(OutputView.class)) {
					nav.addItem(new SideNavItem("Abgabe", OutputView.class, LineAwesomeIcon.HANDSHAKE.create()));

				}
				if (accessChecker.hasAccess(MitgliederView.class)) {
					nav.addItem(
							new SideNavItem("Mitglieder", MitgliederView.class, LineAwesomeIcon.USERS_SOLID.create()));

				}
				if (accessChecker.hasAccess(WaitingListView.class)) {
					nav.addItem(new SideNavItem("Wartebereich", WaitingListView.class,
							LineAwesomeIcon.LIST_SOLID.create()));

				}
				if (accessChecker.hasAccess(VereinView.class)) {
					nav.addItem(new SideNavItem("Verein", VereinView.class, LineAwesomeIcon.STORE_ALT_SOLID.create()));

				}
				if (accessChecker.hasAccess(ConfigurationView.class)) {
					nav.addItem(new SideNavItem("Konfiguration", ConfigurationView.class,
							LineAwesomeIcon.COG_SOLID.create()));

				}

			}
//        if (accessChecker.hasAccess(AiWizzardView.class)) {
//            nav.addItem(new SideNavItem("AI Wizzard", AiWizzardView.class, LineAwesomeIcon.MAGIC_SOLID.create()));
//
//        }

		}

		return nav;
    }

    private void clearNavigationFromItem(SideNav nav, SideNavItem sideNavItem) {
        if(nav.getItems().contains(sideNavItem)) {        	
        	nav.remove(sideNavItem);
        }
	}
    
    private void clearAllOtherNavigationItems(SideNav nav, SideNavItem sideNavItem) {        
        nav.removeAll();
        nav.addItem(sideNavItem);
	}

	private Footer createFooter() {
        Footer layout = new Footer();

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            
            associationId = user.getAssociationId();
                        
            Avatar avatar = new Avatar(user.getName());
    
            StreamResource resource;
            
            if(user.getProfilePicture() == null) {
            	 resource = new StreamResource("logoCodeGreen.png",
                         () -> getClass().getResourceAsStream("/logoCodeGreen.png"));
            } else {
            	  resource = new StreamResource("profile-pic",
                        () -> new ByteArrayInputStream(user.getProfilePicture()));
            }
             
            avatar.setImageResource(resource);
            avatar.setThemeName("xsmall");
            avatar.getElement().setAttribute("tabindex", "-1");

            MenuBar userMenu = new MenuBar();
            userMenu.setOverlayClassName("main-layout-menu-bar-1");
            userMenu.addClassName("main-layout-menu-bar-1");
            userMenu.setThemeName("tertiary-inline contrast");

            MenuItem userName = userMenu.addItem("");
            Div div = new Div();
            div.add(avatar);
            div.add(user.getUsername());
            div.add(new Icon("lumo", "dropdown"));
            div.getElement().getStyle().set("display", "flex");
            div.getElement().getStyle().set("align-items", "center");
            div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
            userName.add(div);
            userName.getSubMenu().addItem("Logout", e -> {
                authenticatedUser.logout();
            });

            layout.add(userMenu);
        } else {
            Anchor loginLink = new Anchor("login", "Einloggen");
            loginLink.addClassNames("custom-anchor");
            layout.add(loginLink);
        }

        layout.addClassNames(LumoUtility.JustifyContent.CENTER);
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
    
    @Override
	public void beforeEnter(BeforeEnterEvent event) {
		//check if url must result in different menu	
    	
        if (event.getLocation().getSegments().contains("onboarding")) {
            //url contaings onboarding means we only need onboarding side nav
        	
            //add it to navigation menu
            if (accessChecker.hasAccess(OnboardingView.class)) {           	
            	clearAllOtherNavigationItems(nav, sideNavItemOnboarding);   
            }      
        }
        
        else if (event.getLocation().getSegments().contains("passwordreset")) {     
        	
        	if (accessChecker.hasAccess(PasswordView.class)) {        	
        		clearAllOtherNavigationItems(nav, sideNavItemPasswordReset);
        	}
        	
//        if (accessChecker.hasAccess(OnboardingView.class)) {
//        	
//        	Optional<User> maybeUser = authenticatedUser.get();
//        	
//            if (maybeUser.isPresent()) {
//            	currentUserRoles = maybeUser.get().getRoles();
//            	if (maybeUser.get().getRoles().contains(Role.MEMBER)) {               	
//            		clearAllOtherNavigationItems(nav, sideNavItemOnboarding);
//                }
//            }         
//        }
        }
        
	}
}
