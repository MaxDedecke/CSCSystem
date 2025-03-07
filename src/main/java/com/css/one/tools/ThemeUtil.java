package com.css.one.tools;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;

public class ThemeUtil {

    private static final String THEME_SESSION_ATTRIBUTE = "theme";

    public static void setTheme(String themeName) {
        // Speichere Theme in der Session
        VaadinSession.getCurrent().setAttribute(THEME_SESSION_ATTRIBUTE, themeName);
        UI.getCurrent().getElement().setAttribute("theme", themeName);
    }

    public static String getTheme() {
        // Hole Theme aus der Session, falls vorhanden
        String theme = (String) VaadinSession.getCurrent().getAttribute(THEME_SESSION_ATTRIBUTE);
        if (theme != null) {
            return theme;
        }

        // Fallback auf Standard-Theme
        return "css-system-one";
    }

	public static String getThemeByModule(String moduleIdentifier) {
		
		switch(moduleIdentifier) {
		
		case "MAIN": {
			 String theme = (String) VaadinSession.getCurrent().getAttribute(THEME_SESSION_ATTRIBUTE);
		        if (theme != null) {
		        	
		            if(theme.equals("css-system-one")) {
		            	return "main-layout";
		            } else {
		            	return "pm-main-layout";
		            }
		        }
		}
		case "WAITINGLIST": {
			 String theme = (String) VaadinSession.getCurrent().getAttribute(THEME_SESSION_ATTRIBUTE);
		        if (theme != null) {
		        	
		            if(theme.equals("css-system-one")) {
		            	return "waitinglist-view";
		            } else {
		            	return "pm-waitinglist-view";
		            }
		        }
		}

		default:
			
		}
		
		return "";
	}
}

