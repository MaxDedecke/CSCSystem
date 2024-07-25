package com.css.one.views.ai;

import java.io.IOException;

import com.css.one.services.OpenAIClient;
import com.css.one.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("AI Wizzard")
@Route(value = "wizzard", layout = MainLayout.class)
@AnonymousAllowed
public class AiWizzardView extends VerticalLayout {

	private static final long serialVersionUID = -513711368156085890L; 
	
	public AiWizzardView() {
		
		addClassName("aiwizzard-view");
		H1 h1 = new H1("Frage die KI 🤗");
		
		HorizontalLayout wrapper = new HorizontalLayout();
		wrapper.setWidthFull();
		TextField promptField = new TextField("Frage");
		promptField.setWidthFull();
        Button sendButton = new Button("Senden");
        sendButton.addClassNames("button-category");
        wrapper.add(promptField, sendButton);
        
        TextArea responseArea = new TextArea("Antwort");
        responseArea.setWidthFull();
        responseArea.setHeightFull();
        responseArea.setEnabled(false);
        
        sendButton.addClickListener(click -> {
            String prompt = promptField.getValue();
            try {
                OpenAIClient client = new OpenAIClient();
                String response = client.getCompletion(prompt);
                responseArea.setValue(response);
            } catch (IOException e) {
                Notification.show("Error: " + e.getMessage());
            }
        });
        
        setSizeFull();
        add(h1, wrapper, responseArea);
	}
}
