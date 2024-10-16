package com.css.one.views.mitglieder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import com.css.one.data.AssociationRole;
import com.css.one.data.Blossom;
import com.css.one.data.MemberSubscription;
import com.css.one.data.Person;
import com.css.one.services.BlossomService;
import com.css.one.services.MemberSubscriptionService;
import com.css.one.services.PersonService;
import com.css.one.views.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.PermitAll;

@PageTitle("Mitglieder")
@Route(value = "mitglieder/:samplePersonID?/:action?(edit)", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class MitgliederView extends Div {

    private static final long serialVersionUID = -4968177781532500867L;

    private final Grid<Person> grid = new Grid<>(Person.class, false);

    private TextField firstName;
    private TextField lastName;
    private TextField email;
    private TextField phone;
    private PasswordField password;
    private DatePicker dateOfBirth;
    private TextField numberField;
    private ComboBox<AssociationRole> role;

    private final Button cancel = new Button("Abbrechen");
    private final Button save = new Button("Speichern");
    private Text memberCount;
    
    private Dialog confirmDeleteDialog;
    private Dialog memberDetailDialog;    
    private Person samplePerson;
    
    private H3 textForDeletion;

    private final PersonService samplePersonService;
    private final MemberSubscriptionService subscriptionService;
    private final BlossomService blossomService;
    
    private int associationId;

    public MitgliederView(PersonService samplePersonService, MemberSubscriptionService subscriptionService, BlossomService blossomService) {
        this.samplePersonService = samplePersonService;
        this.subscriptionService = subscriptionService;
        this.blossomService = blossomService;
        
        addClassNames("mitglieder-view");

        associationId = MainLayout.getAssociationId();

        // Create UI
        createGridLayout();

        // Configure Grid       
        createMemberDetailsDialog();
        createConfirmDeletionDialog();
        
        grid.addColumn(p -> p.getMemberNumber()).setAutoWidth(true).setHeader("Mitgliedsnummer").setWidth("200px").setFlexGrow(0);
//		grid.addComponentColumn(e -> {
//
//			Avatar avatar = new Avatar("member_picture");
//			
//			StreamResource imageResource = new StreamResource("potteriepng.png",
//	                () -> getClass().getResourceAsStream("/potteriepng.png"));
//			
//			avatar.setImageResource(imageResource);
//			avatar.setHeight(48, Unit.PIXELS);
//			avatar.setWidth(48, Unit.PIXELS);
//			avatar.addClassNames(LumoUtility.Border.ALL, LumoUtility.BorderColor.CONTRAST_50);
//			
//			return avatar;
//		}).setWidth("100px").setFlexGrow(0);
        
        grid.addColumn(p -> p.getFirstName() + " " + p.getLastName()).setAutoWidth(true).setHeader("Name");
        grid.addColumn(p -> p.getPhone()).setAutoWidth(true).setHeader("Telefonnummer");
        grid.addColumn(p -> p.getEmail()).setAutoWidth(true).setHeader("Email");      
        
        grid.addComponentColumn(item -> {
        	MenuBar menuBar = new MenuBar();
			
        	menuBar.addItem("Infos", event -> {
        		this.samplePerson = item;
            	putValuesInDialog();
            	memberDetailDialog.open();
                refreshGrid();
			});
        	
			menuBar.addItem("Mitglied löschen", event -> {
        		this.samplePerson = item;
        		this.textForDeletion.setText("Bestätige das Löschen des Mitglieds: " + this.samplePerson.getFirstName() + " " + this.samplePerson.getLastName());
        		confirmDeleteDialog.open();
			});
        	
        	return menuBar;
        }).setWidth("100px").setFlexGrow(0);
        
        grid.addClassNames(LumoUtility.Height.FULL);
        grid.setItems(samplePersonService.findAllByAssociation(associationId));
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        cancel.addClickListener(e -> {
            refreshGrid();
        });

		save.addClickListener(e -> {
			try {

				if (role.getValue() != null) {
					
					boolean newMember = false;
					if (this.samplePerson == null) {
						this.samplePerson = new Person();
						newMember = true;
					}
					samplePerson.setAssociationId(associationId);
					samplePerson.setAssociationRole(role.getValue());
					samplePerson.setDateOfRegistration(LocalDate.now());
					if(newMember) {						
						samplePerson.setMemberNumber(samplePersonService.getFreeMemberNumber(associationId));
					}
					
					if (role.getValue() != AssociationRole.MEMBER) {
						samplePerson.setDateOfHigherRole(LocalDate.now());
					}
					this.samplePerson = samplePersonService.update(this.samplePerson);
					if(newMember) {						
						createSingleSubscriptionForNewMember(this.samplePerson);
					}
					refreshGrid();	
					Notification.show("Mitglied hinzugefügt");
					UI.getCurrent().navigate(MitgliederView.class);
				} else {
					Notification n = Notification.show("Eine neue Person muss eine Rolle haben!");
					n.setPosition(Position.MIDDLE);
					n.addThemeVariants(NotificationVariant.LUMO_ERROR);
				}
			} catch (ObjectOptimisticLockingFailureException exception) {
				Notification n = Notification.show(
						"Error updating the data. Somebody else has updated the record while you were making changes.");
				n.setPosition(Position.MIDDLE);
				n.addThemeVariants(NotificationVariant.LUMO_ERROR);
			} 
		});
    }

    private void createConfirmDeletionDialog() {
    	confirmDeleteDialog = new Dialog();
    	textForDeletion = new H3("Löschen von Mitglied");
    	
    	Button cancelButton = new Button("Abbrechen", e -> confirmDeleteDialog.close());
		cancelButton.addClassName("cancel-button");
		
		Button confirmButton = new Button("Bestätigen",e -> {	
			
			if(deleteMember()) {				
				
				Notification notification = Notification.show("Mitglied erfolgreich gelöscht.");
				notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
				refreshGrid();
			} else {
				Notification notification = Notification.show("Löschen nicht möglich.");
				notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
			}
			
			this.confirmDeleteDialog.close();
		});
		confirmButton.addClassName("save-button");
		
		confirmDeleteDialog.getFooter().add(cancelButton, confirmButton);
		confirmDeleteDialog.add(textForDeletion);
	}

	private boolean deleteMember() {
    	
    	boolean isDeleted = true;
    	
    	//remove possible relations to member subscription
    	LocalDate now = LocalDate.now();
		Optional<MemberSubscription> any = subscriptionService
				.findByMonthAndYear(now.getMonthValue(), now.getYear(), associationId).stream()
				.filter(s -> s.getPersonId() == samplePerson.getId().intValue()).findAny();
		
		if(any.isPresent()) {
			subscriptionService.delete(any.get().getId());
		}
		
		//delete possible relations to blossom
		List<Blossom> allBlossomsOfAssociation = blossomService.findAllByAssociation(associationId);
		
		for(Blossom b : allBlossomsOfAssociation) {
			if(b.getWeighedByMembers().stream().filter(e -> e.getId().equals(samplePerson.getId())).findAny().isPresent()) {
				List<Person> tmpPeople = new ArrayList<Person>();
				b.getWeighedByMembers().forEach(p -> {
					if(!p.getId().equals(samplePerson.getId())) {
						tmpPeople.add(p);
					}
				});
				b.setWeighedByMembers(tmpPeople);
				blossomService.update(b);
			}
		}
		
		samplePersonService.delete(this.samplePerson.getId());
		return isDeleted;
	}

	private void putValuesInDialog() {
    	numberField.setValue(String.valueOf(this.samplePerson.getMemberNumber()));
        firstName.setValue(this.samplePerson.getFirstName());
        lastName.setValue(this.samplePerson.getLastName());
        email.setValue(this.samplePerson.getEmail());
        phone.setValue(this.samplePerson.getPhone());
        dateOfBirth.setValue(this.samplePerson.getDateOfBirth());
        role.setValue(this.samplePerson.getAssociationRole());	
	}

	private void createMemberDetailsDialog() {
		memberDetailDialog = new Dialog();
		memberDetailDialog.addClassName(LumoUtility.MaxWidth.SCREEN_MEDIUM);
		
		VerticalLayout mainWrapper = new VerticalLayout();
		mainWrapper.addClassNames("form-size");
		
		H1 h1 = new H1();
		h1.add("Mitgliedsinformationen");
		h1.addClassName("customheader");
		
		FormLayout layout = new FormLayout();
		
		numberField = new TextField("Mitgliedsnummer");
		numberField.setEnabled(false);
        firstName = new TextField("Vorname");
        lastName = new TextField("Nachname");
        email = new TextField("Email");
        phone = new TextField("Telefonnummer");
        phone.setAllowedCharPattern("[0-9/]");
        dateOfBirth = new DatePicker("Geburtstag");
        dateOfBirth.setEnabled(false);
        password = new PasswordField("Password");
        
        role = new ComboBox<AssociationRole>("Rolle im Verein");
        role.setItems(Arrays.asList(AssociationRole.values()));
        role.setItemLabelGenerator(e -> e.getLabel());
		
		layout.add(numberField, firstName, lastName, email, phone, role, dateOfBirth, password);
		
		Button cancelButton = new Button("Zurück", e -> memberDetailDialog.close());
		cancelButton.addClassName("cancel-button");
		
		Button confirmButton = new Button("Aktualisieren",e -> {	
			updatePerson();
			this.memberDetailDialog.close();
			Notification notification = Notification.show("Informationen aktualisiert!");
			notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
		});
		confirmButton.addClassName("save-button");
		
		memberDetailDialog.getFooter().add(cancelButton, confirmButton);
		mainWrapper.add(h1, layout);
		memberDetailDialog.add(mainWrapper);
	}
	
	private void updatePerson() {
		
		if (role.getValue() != null) {
			
			boolean newMember = false;
			if (this.samplePerson == null) {
				this.samplePerson = new Person();
				newMember = true;
			}
			samplePerson.setFirstName(firstName.getValue());
			samplePerson.setLastName(lastName.getValue());
			samplePerson.setEmail(email.getValue());
			samplePerson.setPhone(phone.getValue());
			
			samplePerson.setAssociationId(associationId);
			samplePerson.setAssociationRole(role.getValue());
			samplePerson.setDateOfRegistration(LocalDate.now());
			
			if(newMember) {						
				samplePerson.setMemberNumber(samplePersonService.getFreeMemberNumber(associationId));
			} else {
				samplePerson.setMemberNumber(Integer.valueOf(numberField.getValue()));
			}
			
			if (role.getValue() != AssociationRole.MEMBER) {
				samplePerson.setDateOfHigherRole(LocalDate.now());
			}
			
			this.samplePerson = samplePersonService.update(this.samplePerson);
			if(newMember) {						
				createSingleSubscriptionForNewMember(this.samplePerson);
			}
			refreshGrid();	
		} else {
			Notification n = Notification.show("Eine neue Person muss eine Rolle haben!");
			n.setPosition(Position.MIDDLE);
			n.addThemeVariants(NotificationVariant.LUMO_ERROR);
		}
	}

	private void createSingleSubscriptionForNewMember(Person member) {
		
    	LocalDate now = LocalDate.now();
    	MemberSubscription subscription = new MemberSubscription();
		subscription.setAssociationId(associationId);
		subscription.setMonth(now.getMonthValue());
		subscription.setYear(now.getYear());
		subscription.setPersonId(member.getId().intValue());
		subscription.setPayed(false);

		subscriptionService.update(subscription);
	}

    private void createGridLayout() {
    	
    	VerticalLayout mainLayout = new VerticalLayout();
    	mainLayout.addClassNames(LumoUtility.Padding.NONE);
        
        HorizontalLayout bottomLayout = new HorizontalLayout();
        bottomLayout.setWidth("100%");
        bottomLayout.addClassNames(LumoUtility.Padding.NONE, LumoUtility.Padding.Bottom.SMALL, LumoUtility.Padding.Left.MEDIUM, LumoUtility.JustifyContent.CENTER);
        memberCount = new Text("Mitglieder: " + samplePersonService.count());
        bottomLayout.add(memberCount);
        
        mainLayout.add(grid, bottomLayout);
        add(mainLayout);
    }

    private void refreshGrid() {
        List<Person> allByAssociation = samplePersonService.findAllByAssociation(associationId);
        grid.setItems(allByAssociation);
        memberCount.setText("Mitglieder: " + allByAssociation.size());
    }
}
