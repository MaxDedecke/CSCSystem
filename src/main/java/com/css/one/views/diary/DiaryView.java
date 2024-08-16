package com.css.one.views.diary;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.css.one.data.Cutting;
import com.css.one.data.DiaryEntry;
import com.css.one.data.OutputEntity;
import com.css.one.data.OutputType;
import com.css.one.data.Seed;
import com.css.one.data.Blossom;
import com.css.one.services.CuttingService;
import com.css.one.services.DiaryEntryService;
import com.css.one.services.SeedService;
import com.css.one.services.BlossomService;
import com.css.one.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.PermitAll;

@PageTitle("Tagebuch")
@Route(value = "tagebuch", layout = MainLayout.class)
@PermitAll
public class DiaryView extends VerticalLayout {

	private static final long serialVersionUID = -474632761341117537L;
	
	private DiaryEntryService diaryEntryService;
	private BlossomService strainService;
	private SeedService seedService;
	private CuttingService cuttingService;
	
	private int associationId;
	
	private Grid<DiaryEntry> entriesGrid = new Grid<DiaryEntry>();
	private ComboBox<OutputEntity> entityBox = new ComboBox<OutputEntity>("Sorte");
	private ComboBox<OutputType> outputTypeBox = new ComboBox<OutputType>("Art");
	
	ComboBox<OutputEntity> filterEntityBox = new ComboBox<OutputEntity>("Filtern nach");

	private TextArea textArea = new TextArea("Eintrag");
	private VirtualList<DiaryEntry> virtualList = new VirtualList<>();
	private Button addEntryButton = new Button("Neuer Eintrag");
	
	private Dialog addEntryDialog = new Dialog();
	
	
	public DiaryView(DiaryEntryService diaryEntryService, BlossomService strainService, SeedService seedService, CuttingService cuttingService) {
		this.diaryEntryService = diaryEntryService;
		this.seedService = seedService;
		this.strainService = strainService;
		this.cuttingService = cuttingService;
		
		associationId = MainLayout.getAssociationId();
		
		addClassNames("diary-view");
		createNewEntryDialogContent();
		
		add(createButtonComponent(), new Hr(), createListComponent());
		
		refreshGrid(null);
	}

	private Component createButtonComponent() {
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setWidthFull();
		addEntryButton.addClassName("button-category");
		addEntryButton.addClickListener(e -> {
			addEntryDialog.open();
		});
		
		//Filter Component
		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.addClassNames("filter-layout");
		filterLayout.setWidthFull();
		
		filterEntityBox.setItemLabelGenerator(e -> e.getName());
		filterEntityBox.addValueChangeListener(e -> {
			
			if(filterEntityBox.isEmpty()) {
				refreshGrid(null);
			} else {
				refreshGrid(e.getValue());
			}
		});
		filterEntityBox.setEnabled(false);
		filterEntityBox.setClearButtonVisible(true);
		
		ComboBox<OutputType> typeBox = new ComboBox<OutputType>("Typ");
		typeBox.setItemLabelGenerator(e -> e.getLabel());
		typeBox.setItems(OutputType.values());
		typeBox.setClearButtonVisible(true);
		typeBox.addValueChangeListener(e -> {
			filterEntityBox.setEnabled(e.getValue() != typeBox.getEmptyValue());
			if(e.getValue() != typeBox.getEmptyValue()) {				
				setItemsOfEntityBox(e.getValue(), true);
			} else {
				refreshGrid(null);
			}
		});
		typeBox.setEnabled(false);
		
		Checkbox specificEntityBox = new Checkbox("Filtern");
		specificEntityBox.addClassName(LumoUtility.Margin.Bottom.SMALL);
		specificEntityBox.setValue(false);
		specificEntityBox.addValueChangeListener(e -> {
			typeBox.setEnabled(e.getValue());
			filterEntityBox.setEnabled(e.getValue());
		});
		
		filterLayout.add(specificEntityBox, typeBox, filterEntityBox);
		buttonLayout.add(addEntryButton, filterLayout);
		return buttonLayout;
	}

	private Component createListComponent() {
		VerticalLayout layout = new VerticalLayout();
		layout.addClassNames(LumoUtility.Padding.NONE, LumoUtility.Margin.Top.NONE);
		layout.setHeightFull();
        virtualList.setRenderer(new ComponentRenderer<>(entry -> {
        	EntryLayout entryLayout = new EntryLayout();
        	entryLayout.addClassName("diary-view-horizontal-layout-1");
        	entryLayout.setEntry(entry);
            return entryLayout;
        }));
        virtualList.addClassNames(LumoUtility.Padding.NONE, LumoUtility.Margin.Top.NONE, "custom-scrollbar");
        layout.add(virtualList);
        return layout;
	}

	private void refreshGrid(OutputEntity entity) {
		if(entity == null) {
			entriesGrid.setItems(diaryEntryService.findAllByAssociation(associationId));
			
			virtualList.setItems(diaryEntryService.findAllByAssociation(associationId));
		} else {
			entriesGrid.setItems(diaryEntryService.findAllByAssociation(associationId).stream().filter(e ->{
				if(e.getCutting() != null) {
					if(e.getCutting().getId().equals(entity.getId())) {
						return true;
					} else {
						return false;
					}
				} else if(e.getSeed() != null) {
					if(e.getSeed().getId().equals(entity.getId())) {
						return true;
					} else {
						return false;
					}
				} else if(e.getStrain() != null) {
					if(e.getStrain().getId().equals(entity.getId())) {
						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			}).toList());
			
			
			virtualList.setItems(diaryEntryService.findAllByAssociation(associationId).stream().filter(e ->{
				if(e.getCutting() != null) {
					if(e.getCutting().getId().equals(entity.getId())) {
						return true;
					} else {
						return false;
					}
				} else if(e.getSeed() != null) {
					if(e.getSeed().getId().equals(entity.getId())) {
						return true;
					} else {
						return false;
					}
				} else if(e.getStrain() != null) {
					if(e.getStrain().getId().equals(entity.getId())) {
						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			}).toList());
		}
	}

	private void createNewEntryDialogContent() {	
		VerticalLayout layout = new VerticalLayout();
		layout.setMinHeight("30%");
		
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setWidthFull();
		
		outputTypeBox.setItems(OutputType.values());
		outputTypeBox.setItemLabelGenerator(e -> e.getLabel());
		outputTypeBox.setWidthFull();
		outputTypeBox.setValue(outputTypeBox.getListDataView().getItem(0));
		
		outputTypeBox.addValueChangeListener(e -> {
			setItemsOfEntityBox(e.getValue(), false);
		});
		
		entityBox.setOverlayClassName("diary-view-combo-box-1");
		entityBox.addClassName("diary-view-combo-box-1");
		entityBox.setItemLabelGenerator(e -> e.getName());
		entityBox.setWidthFull();
		setItemsOfEntityBox(OutputType.BLOSSOM, false);
		
		Button addEntryButton = new Button("hinzufügen");
		addEntryButton.addClassName("save-button");
		addEntryButton.addClickListener(e -> {
			addNewEntry();
			clearDialog();
			refreshGrid(null);
		});
		
		Button cancelButton = new Button("zurück");
		cancelButton.addClassName("cancel-button");
		cancelButton.addClickListener(e -> {
			addEntryDialog.close();
			clearDialog();
		});
		
		horizontalLayout.add(outputTypeBox, entityBox);
		
		textArea.addClassName("diary-view-text-area-1");
		textArea.setWidthFull();
		
		layout.add(horizontalLayout, textArea);
		
		addEntryDialog.getFooter().add(cancelButton, addEntryButton);
		addEntryDialog.add(layout);
	}
	
	private void addNewEntry() {
		
		DiaryEntry entry = new DiaryEntry();
		entry.setAssociationId(associationId);
		entry.setText(textArea.getValue());
		entry.setDate(LocalDate.now());

		if (!entityBox.isEmpty()) {
			if (outputTypeBox.getValue() == OutputType.BLOSSOM) {
				Optional<Blossom> optional = strainService.get(entityBox.getValue().getId());
				if (optional.isPresent()) {
					entry.setStrain(optional.get());
				}
			} else if (outputTypeBox.getValue() == OutputType.CUTTING) {
				Optional<Cutting> optional = cuttingService.get(entityBox.getValue().getId());
				if (optional.isPresent()) {
					entry.setCutting(optional.get());
				}
			} else {
				Optional<Seed> optional = seedService.get(entityBox.getValue().getId());
				if (optional.isPresent()) {
					entry.setSeed(optional.get());
				}
			}
		}
		diaryEntryService.update(entry);
		addEntryDialog.close();
	}

	private void clearDialog() {
		outputTypeBox.setValue(outputTypeBox.getListDataView().getItem(0));
		textArea.setValue("");
		setItemsOfEntityBox(OutputType.BLOSSOM, false);
	}

	private void setItemsOfEntityBox(OutputType outputType, boolean isForFilter) {
		List<OutputEntity>entities = new ArrayList<>();
		
		if(outputType == OutputType.BLOSSOM) {
			strainService.findAllReadyForOutput(associationId).forEach(e -> {
				entities.add((OutputEntity)e);
			});
		} else if(outputType == OutputType.CUTTING) {
			cuttingService.findAllByAssociation(associationId).forEach(e -> {
				entities.add((OutputEntity)e);
			});
		} else {
			seedService.findAllByAssociation(associationId).forEach(e -> {
				entities.add((OutputEntity)e);
			});
		}
		
		if(isForFilter) {
			filterEntityBox.setItems(entities);
		} else {			
			entityBox.setItems(entities);
		}
	}
	
	public class EntryLayout extends HorizontalLayout {

		private static final long serialVersionUID = 8080325977391846535L;
		private TextArea textField;
	    private H3 entityField;
	    private Text date;
	    private Avatar avatar;
	    private VerticalLayout innerLayout;
	    
	    public EntryLayout() {
			entityField = new H3("");

			entityField.addClassNames(LumoUtility.Padding.Left.SMALL, "diary-view-h3-1");
			textField = new TextArea();
			textField.setWidthFull();
			textField.setReadOnly(true);
			textField.addClassNames("textarea");
			date = new Text("Datum");

			avatar = new Avatar("");
			StreamResource imageResource = new StreamResource("seed.png",
					() -> getClass().getResourceAsStream("/seed.png"));

			avatar.setImageResource(imageResource);
            avatar.setHeight("64px");
            avatar.setWidth("64px");
            avatar.getElement().setAttribute("tabindex", "-1");
            avatar.addClassName(LumoUtility.Margin.Top.MEDIUM);
	        setWidthFull();
	        addClassNames(LumoUtility.Padding.Top.MEDIUM, LumoUtility.Padding.Left.MEDIUM, LumoUtility.Padding.Right.MEDIUM,
	        		LumoUtility.Margin.Top.XSMALL, "diary-view-horizontal-layout-1");
	        
	        innerLayout = new VerticalLayout();
	        innerLayout.addClassName("diary-view-vertical-layout-1");
	        innerLayout.setMinHeight(100, Unit.PIXELS);
	        
	        HorizontalLayout dateWrapper = new HorizontalLayout();
	        dateWrapper.setWidthFull();
	        dateWrapper.add(date);
	        dateWrapper.addClassName("right-to-left-layout");
	        
	        innerLayout.add(entityField, textField, dateWrapper);
	        
	        VerticalLayout avatarLayout = new VerticalLayout();
	        avatarLayout.addClassName("diary-view-vertical-layout-2");
	        avatarLayout.add(avatar);
	        add(avatarLayout, innerLayout);
	    }

	    public void setEntry(DiaryEntry entry) {
	    	
	        textField.setValue(renderText(entry.getText()));
	        
	        if(entry.getSeed() != null) {
	        	entityField.setText("Samen: " + entry.getSeed().getName());
	        } else if(entry.getCutting() != null) {
	        	entityField.setText("Steckling: " + entry.getCutting().getName());
	        } else if(entry.getStrain() != null) {
	        	entityField.setText("Sorte: " + entry.getStrain().getName());
	        }
	        
	        date.setText(renderDate(entry.getDate()));
	    }
	    
	    private String renderText(String text) {
	    	  // Füge nach maxLineLength Zeichen einen Zeilenumbruch hinzu
	        return text.replaceAll("(.{" + 180 + "})", "$1\n");
		}

		public String renderDate(LocalDate date) {
	    	String day = "";
			String month = "";

			if (date != null) {

				if (date.getDayOfMonth() < 10) {
					day = "0" + String.valueOf(date.getDayOfMonth());
				} else {
					day = String.valueOf(date.getDayOfMonth());
				}

				if (date.getMonthValue() < 10) {
					month = "0" + String.valueOf(date.getMonthValue());
				} else {
					month = String.valueOf(date.getMonthValue());
				}

				return day + "." + month + "." + date.getYear();
			} else {
				return "-";
			}
	    }
	}
}
