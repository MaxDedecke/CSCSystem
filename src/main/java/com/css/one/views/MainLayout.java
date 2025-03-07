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
import com.css.one.security.BusinessCase;
import com.css.one.services.PropertyService;
import com.css.one.view.pm.views.HouseComplaintView;
import com.css.one.view.pm.views.HousePartyView;
import com.css.one.view.pm.views.HouseUnitView;
import com.css.one.view.pm.views.PropertyAnnouncementView;
import com.css.one.view.pm.views.PropertyAppointmentView;
import com.css.one.view.pm.views.PropertyDocumentView;
import com.css.one.view.pm.views.PropertyManagementDataView;
import com.css.one.views.arbeitsplanung.ArbeitsplanungView;
import com.css.one.views.diary.DiaryView;
import com.css.one.views.finanzen.FinanzenView;
import com.css.one.views.mitglieder.MitgliederView;
import com.css.one.views.output.OutputView;
import com.css.one.views.rechtliches.RechtlichesView;
import com.css.one.views.settings.ConfigurationView;
import com.css.one.views.userprofile.UserProfileView;
import com.css.one.views.verein.VereinView;
import com.css.one.views.waitinglist.WaitingListView;
import com.css.one.views.warenlager.WarenlagerView;
import com.css.one.views.übersicht.ÜbersichtView;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
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
import com.vaadin.flow.component.icon.SvgIcon;
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
    static int propertyManagementId;
    
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

        nav.setWidth("100%");
        
		if (authenticatedUser.get().isPresent()) {
			
			if(authenticatedUser.get().get().getBusinessCase() == BusinessCase.CSC) {
				nav.addClassNames("vaadin-app-layout");
				addClassNames("green-navbar", "green-menu-icon");
				createNavigationForCsc();
				
				if (accessChecker.hasAccess(UserProfileView.class)) {
					nav.addItem( new SideNavItem("Profil", UserProfileView.class,
							LineAwesomeIcon.USER_CIRCLE_SOLID.create()));					
				}
				
				addClassNames("green", "green-active", "set-green-color", "green-theme-icon", "green-theme-menu-bar-overlay-frame");
			} else {
				//else until now can only property management
				addClassNames("blue-navbar", "blue-menu-icon", "blue-theme-icon", "blue-theme-menu-bar-overlay-frame");
				createNavigationForPropertyManagement();
				addClassNames("blue", "blue-active");

			}
			
//        if (accessChecker.hasAccess(AiWizzardView.class)) {
//            nav.addItem(new SideNavItem("AI Wizzard", AiWizzardView.class, LineAwesomeIcon.MAGIC_SOLID.create()));
//
//        }

		}
		
		return nav;
    }

    private void createNavigationForPropertyManagement() {
    	
    	addClassName("blue-navbar");
    	
    	if (accessChecker.hasAccess(PropertyManagementDataView.class)) {
			SvgIcon svgIcon = LineAwesomeIcon.EXCLAMATION_SOLID.create();
			svgIcon.addClassName("blue");
			SideNavItem sideNavItem =  new SideNavItem("Infocenter", PropertyManagementDataView.class,
					svgIcon);
			
			sideNavItem.addClassNames("blue", "blue-icon");
			nav.addItem(sideNavItem);			
		}
    	
    	if (accessChecker.hasAccess(PropertyDocumentView.class)) {
			SvgIcon svgIcon = LineAwesomeIcon.COPY_SOLID.create();
			svgIcon.addClassName("blue");
			SideNavItem sideNavItem =  new SideNavItem("Dokumente", PropertyDocumentView.class,
					svgIcon);
			
			sideNavItem.addClassNames("blue", "blue-icon");
			nav.addItem(sideNavItem);			
		}
    	
    	if (accessChecker.hasAccess(PropertyAppointmentView.class)) {
			SvgIcon svgIcon = LineAwesomeIcon.CALENDAR_ALT_SOLID.create();
			svgIcon.addClassName("blue");
			SideNavItem sideNavItem =  new SideNavItem("Termine", PropertyAppointmentView.class,
					svgIcon);
			
			sideNavItem.addClassNames("blue", "blue-icon");
			nav.addItem(sideNavItem);			
		}
    	
    	if (accessChecker.hasAccess(PropertyAnnouncementView.class)) {
			SvgIcon svgIcon = LineAwesomeIcon.WINDOW_RESTORE_SOLID.create();
			svgIcon.addClassName("blue");
			SideNavItem sideNavItem =  new SideNavItem("Schwarzes Brett", PropertyAnnouncementView.class,
					svgIcon);
			
			sideNavItem.addClassNames("blue", "blue-icon");
			nav.addItem(sideNavItem);			
		}
    	
    	if (accessChecker.hasAccess(HousePartyView.class)) {
			SvgIcon svgIcon = LineAwesomeIcon.ADDRESS_BOOK_SOLID.create();
			svgIcon.addClassName("blue");
			SideNavItem sideNavItem =  new SideNavItem("Parteien", HousePartyView.class,
					svgIcon);
			
			sideNavItem.addClassNames("blue", "blue-icon");
			nav.addItem(sideNavItem);			
		}
    	
    	if (accessChecker.hasAccess(HouseUnitView.class)) {
			SvgIcon svgIcon = LineAwesomeIcon.CITY_SOLID.create();
			svgIcon.addClassName("blue");
			SideNavItem sideNavItem =  new SideNavItem("Liegenschaften", HouseUnitView.class,
					svgIcon);
			
			sideNavItem.addClassNames("blue", "blue-icon");
			nav.addItem(sideNavItem);			
		}
    	
		if (accessChecker.hasAccess(HouseComplaintView.class)) {
			SvgIcon svgIcon = LineAwesomeIcon.EXCLAMATION_CIRCLE_SOLID.create();
			svgIcon.addClassName("blue");
			SideNavItem sideNavItem = new SideNavItem("Anfragen/Anträge", HouseComplaintView.class, svgIcon);
			sideNavItem.addClassNames("blue", "blue-icon");
			
			nav.addItem(sideNavItem);

		}
    	
		if (accessChecker.hasAccess(UserProfileView.class)) {
			SvgIcon svgIcon = LineAwesomeIcon.USER_CIRCLE_SOLID.create();
			svgIcon.addClassName("blue");
			SideNavItem sideNavItem =  new SideNavItem("Profil", UserProfileView.class,
					svgIcon);
			
			sideNavItem.addClassNames("blue", "blue-icon");
			nav.addItem(sideNavItem);				
		}
	}

	private void createNavigationForCsc() {
    	
    	if (accessChecker.hasAccess(ÜbersichtView.class)) {
			
			SvgIcon svgIcon = LineAwesomeIcon.GLOBE_SOLID.create();
			svgIcon.addClassName("green");
			SideNavItem sideNavItem = new SideNavItem("Übersicht", ÜbersichtView.class, svgIcon);
			sideNavItem.addClassName("green");
			
			nav.addItem(sideNavItem);

			clearNavigationFromItem(nav, sideNavItemOnboarding);
		}

		if (authenticatedUser.get().get().getRoles().iterator().next().equals(Role.ADMIN)) {

			if (accessChecker.hasAccess(FinanzenView.class)) {
				
				SvgIcon svgIcon = LineAwesomeIcon.MONEY_BILL_WAVE_SOLID.create();
				svgIcon.addClassName("green");
				SideNavItem sideNavItem = new SideNavItem("Finanzen", FinanzenView.class, svgIcon);
				sideNavItem.addClassName("green");
				nav.addItem(sideNavItem);

			}
			
			if (accessChecker.hasAccess(DiaryView.class)) {				
				SvgIcon svgIcon = LineAwesomeIcon.BOOK_OPEN_SOLID.create();
				svgIcon.addClassName("green");
				SideNavItem sideNavItem = new SideNavItem("Tagebuch", DiaryView.class, svgIcon);
				sideNavItem.addClassName("green");	
				nav.addItem(sideNavItem);

			}
			if (accessChecker.hasAccess(ArbeitsplanungView.class)) {				
				SvgIcon svgIcon = LineAwesomeIcon.BUSINESS_TIME_SOLID.create();
				svgIcon.addClassName("green");
				SideNavItem sideNavItem = new SideNavItem("Zeiterfassung", ArbeitsplanungView.class, svgIcon);
				sideNavItem.addClassName("green");	
				nav.addItem(sideNavItem);

			}

			if (accessChecker.hasAccess(RechtlichesView.class)) {
				
				SvgIcon svgIcon = LineAwesomeIcon.BALANCE_SCALE_SOLID.create();
				svgIcon.addClassName("green");
				SideNavItem sideNavItem = new SideNavItem("Rechtliches", RechtlichesView.class, svgIcon);
				sideNavItem.addClassName("green");
				nav.addItem(sideNavItem);


			}
			if (accessChecker.hasAccess(WarenlagerView.class)) {
				SvgIcon svgIcon = LineAwesomeIcon.CANNABIS_SOLID.create();
				svgIcon.addClassName("green");
				SideNavItem sideNavItem = new SideNavItem("Bestand", WarenlagerView.class, svgIcon);
				sideNavItem.addClassName("green");
				nav.addItem(sideNavItem);


			}
			if (accessChecker.hasAccess(OutputView.class)) {
				SvgIcon svgIcon = LineAwesomeIcon.HANDSHAKE.create();
				svgIcon.addClassName("green");
				SideNavItem sideNavItem = new SideNavItem("Abgabe", OutputView.class, svgIcon);
				sideNavItem.addClassName("green");
				nav.addItem(sideNavItem);


			}
			if (accessChecker.hasAccess(MitgliederView.class)) {
				SvgIcon svgIcon = LineAwesomeIcon.USERS_SOLID.create();
				svgIcon.addClassName("green");
				SideNavItem sideNavItem = new SideNavItem("Mitglieder", MitgliederView.class, svgIcon);
				sideNavItem.addClassName("green");
				nav.addItem(sideNavItem);


			}
			if (accessChecker.hasAccess(WaitingListView.class)) {		
				SvgIcon svgIcon = LineAwesomeIcon.LIST_SOLID.create();
				svgIcon.addClassName("green");
				SideNavItem sideNavItem = new SideNavItem("Wartebereich", WaitingListView.class, svgIcon);
				sideNavItem.addClassName("green");
				nav.addItem(sideNavItem);


			}
			if (accessChecker.hasAccess(VereinView.class)) {
				SvgIcon svgIcon = LineAwesomeIcon.STORE_ALT_SOLID.create();
				svgIcon.addClassName("green");
				SideNavItem sideNavItem = new SideNavItem("Verein", VereinView.class,  svgIcon);
				sideNavItem.addClassName("green");
				nav.addItem(sideNavItem);

			}
			if (accessChecker.hasAccess(ConfigurationView.class)) {
				SvgIcon svgIcon = LineAwesomeIcon.COG_SOLID.create();
				svgIcon.addClassName("green");
				SideNavItem sideNavItem = new SideNavItem("Konfiguration", ConfigurationView.class,  svgIcon);
				sideNavItem.addClassName("green");
				nav.addItem(sideNavItem);

			}

		}	
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
            
			if (user.getBusinessCase() == BusinessCase.CSC) {
				associationId = user.getAssociationId();

			} else {
				propertyManagementId = user.getAssociationId();
			}
            
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
            
            if(maybeUser.get().getBusinessCase() == BusinessCase.CSC) {
            	userName.addClassName("green-menu-bar-item");
            } else {
            	userName.addClassName("blue-menu-bar-item");
            }
            	
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
    	
		// check if url must result in different menu
		if (authenticatedUser.get().isPresent()) {

			if (authenticatedUser.get().get().getBusinessCase() == BusinessCase.CSC) {

				if (event.getLocation().getSegments().get(0).isBlank()) {
					UI.getCurrent().navigate("/uebersicht");
				}
			} else {
				//event.forwardTo("profil/");
				if (event.getLocation().getSegments().get(0).isBlank()) {
					UI.getCurrent().navigate("profil/");
					event.forwardTo("profil/");
				}
			}
			
		} else {
			if (event.getLocation().getSegments().contains("onboarding")) {
				// url contaings onboarding means we only need onboarding side nav

				// add it to navigation menu
				if (accessChecker.hasAccess(OnboardingView.class)) {
					clearAllOtherNavigationItems(nav, sideNavItemOnboarding);
				}
			}

			else if (event.getLocation().getSegments().contains("passwordreset")) {

				if (accessChecker.hasAccess(PasswordView.class)) {
					clearAllOtherNavigationItems(nav, sideNavItemPasswordReset);
				}

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
