package com.css.one.views.diary;

import com.css.one.services.DiaryEntryService;
import com.css.one.views.MainLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;

@PageTitle("Tagebuch")
@Route(value = "tagebuch", layout = MainLayout.class)
@AnonymousAllowed
public class DiaryView extends VerticalLayout {

	private static final long serialVersionUID = -474632761341117537L;
	
	private DiaryEntryService diaryEntryService;
	
	public DiaryView(DiaryEntryService diaryEntryService) {
		this.diaryEntryService = diaryEntryService;
       
    }

}
