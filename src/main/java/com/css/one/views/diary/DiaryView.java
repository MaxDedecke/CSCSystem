package com.css.one.views.diary;

import com.css.one.data.DiaryEntry;
import com.css.one.data.OutputEntity;
import com.css.one.data.OutputType;
import com.css.one.services.CuttingService;
import com.css.one.services.DiaryEntryService;
import com.css.one.services.SeedService;
import com.css.one.services.StrainService;
import com.css.one.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;

@PageTitle("Tagebuch")
@Route(value = "tagebuch", layout = MainLayout.class)
@AnonymousAllowed
public class DiaryView extends VerticalLayout {

	private static final long serialVersionUID = -474632761341117537L;
	
	private DiaryEntryService diaryEntryService;
	private StrainService strainService;
	private SeedService seedService;
	private CuttingService cuttingService;
	
	private int associationId;
	
	private Grid<DiaryEntry> entriesGrid = new Grid<DiaryEntry>();
	
	public DiaryView(DiaryEntryService diaryEntryService, StrainService strainService, SeedService seedService, CuttingService cuttingService) {
		this.diaryEntryService = diaryEntryService;
		this.seedService = seedService;
		this.strainService = strainService;
		this.cuttingService = cuttingService;
		
		associationId = MainLayout.getAssociationId();
		
		addClassName("diary-view");
		setWidth("100%");
		add(createGridComponent(), new Hr(), createNewEntryComponent());
		
		refreshGrid(null);
	}

	private void refreshGrid(OutputEntity entity) {
		if(entity == null) {
			entriesGrid.setItems(diaryEntryService.findAllByAssociation(associationId));
		} else {
			
		}
	}

	private Component createGridComponent() {
		VerticalLayout layout = new VerticalLayout();
		layout.setHeight("100%");
		layout.addClassNames(LumoUtility.Margin.Left.NONE, LumoUtility.Padding.Left.NONE);
		entriesGrid.addColumn(e -> createAvatarRenderer(e)).setAutoWidth(true);
		entriesGrid.addColumn(e -> e.getText()).setAutoWidth(true);
		entriesGrid.addThemeVariants(GridVariant.LUMO_NO_ROW_BORDERS);
		entriesGrid.setMinHeight(200, Unit.PIXELS);
		
		layout.add(entriesGrid);
		return layout;
	}

	private Component createNewEntryComponent() {
		VerticalLayout layout = new VerticalLayout();
		layout.setMinHeight("30%");
		
		Details details = new Details("Neuer Eintrag", layout);
		details.addClassName("diary-view-details-summary-1");
		details.setWidth("100%");
		details.setOpened(true);
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		
		ComboBox<OutputType> outputTypeBox = new ComboBox<OutputType>("Art");
		outputTypeBox.setItems(OutputType.values());
		outputTypeBox.setItemLabelGenerator(e -> e.getLabel());
		outputTypeBox.setWidthFull();
		outputTypeBox.setValue(outputTypeBox.getListDataView().getItem(0));
		
		outputTypeBox.addValueChangeListener(e -> {
			setItemsOfEntityBox(e.getValue());
		});
		
		ComboBox<OutputEntity> entityBox = new ComboBox<OutputEntity>("Sorte");
		entityBox.setOverlayClassName("diary-view-combo-box-1");
		entityBox.addClassName("diary-view-combo-box-1");
		entityBox.setItemLabelGenerator(e -> e.getName());
		entityBox.setWidthFull();
		setItemsOfEntityBox(OutputType.BLOSSOM);
		
		horizontalLayout.add(outputTypeBox, entityBox);
		
		TextArea textArea = new TextArea("Eintrag");
		textArea.addClassName("diary-view-text-area-1");
		textArea.setWidthFull();
		
		layout.add(horizontalLayout, textArea);
		return details;
	}
	
	private void setItemsOfEntityBox(OutputType blossom) {
//		entityBox.setItems(null);
	}

	private Renderer<DiaryEntry> createAvatarRenderer(DiaryEntry entry) {
		
		if(entry.getCutting() != null) {
			return LitRenderer.<DiaryEntry> of(
					"<vaadin-avatar img=\"${item.pictureUrl}\" name=\"${item.fullName}\" alt=\"User avatar\"></vaadin-avatar>")
					.withProperty("pictureUrl", e -> "/seed.png");
		} else if (entry.getSeed() != null) {
			return LitRenderer.<DiaryEntry> of(
					"<vaadin-avatar img=\"${item.pictureUrl}\" name=\"${item.fullName}\" alt=\"User avatar\"></vaadin-avatar>")
					.withProperty("pictureUrl", e -> "/seed.png");
		} else {
			return LitRenderer.<DiaryEntry> of(
					"<vaadin-avatar img=\"${item.pictureUrl}\" name=\"${item.fullName}\" alt=\"User avatar\"></vaadin-avatar>")
					.withProperty("pictureUrl", e -> "/seed.png");
		}
    }
}
