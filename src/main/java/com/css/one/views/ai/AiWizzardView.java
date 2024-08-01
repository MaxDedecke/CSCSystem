package com.css.one.views.ai;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.css.one.services.OpenAIClient;
import com.css.one.views.MainLayout;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

@PageTitle("AI Wizzard")
@Route(value = "wizzard", layout = MainLayout.class)
@PermitAll
public class AiWizzardView extends VerticalLayout {

	private static final long serialVersionUID = -513711368156085890L; 
	private TextArea responseArea = new TextArea("Antwort");
	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
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
        
        responseArea.setWidthFull();
        responseArea.setHeightFull();
        responseArea.setEnabled(true);
        
        sendButton.addClickListener(click -> {
            String prompt = promptField.getValue();
            try {
                OpenAIClient client = new OpenAIClient();
                String response = client.getCompletion(prompt);
                responseArea.setValue(response);
                Notification.show("Error: " + responseArea.getValue());
            } catch (IOException e) {
                Notification.show("Error: " + e.getMessage());
            }
        });
        
        setSizeFull();
        add(h1, wrapper, responseArea);
	}
	
	private void startTypingEffect(String text) {
		responseArea.clear();
		final int[] index = { 0 };
		getUI().ifPresent(ui -> ui.access(() -> {
			scheduler.scheduleAtFixedRate(() -> {

				if (index[0] < text.length()) {
					responseArea.setValue(responseArea.getValue() + text.charAt(index[0]));
					index[0]++;
				} else {
					scheduler.shutdown();
				}
			}, 0, 100, TimeUnit.MILLISECONDS);
		}));
	}
	
	 @Override
	    protected void onDetach(DetachEvent detachEvent) {
	        super.onDetach(detachEvent);
	        // Shutdown the scheduler when the view is detached
	        if (scheduler != null && !scheduler.isShutdown()) {
	            scheduler.shutdown();
	        }
	    }
}
