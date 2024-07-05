package com.css.one.views.mitglieder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.orm.ObjectOptimisticLockingFailureException;

import com.css.one.data.AssociationRole;
import com.css.one.data.MemberSubscription;
import com.css.one.data.Person;
import com.css.one.services.MemberSubscriptionService;
import com.css.one.services.PersonService;
import com.css.one.views.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
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
    private DatePicker dateOfBirth;
    private TextField numberField;
    private ComboBox<AssociationRole> role;

    private final Button cancel = new Button("Abbrechen");
    private final Button save = new Button("Speichern");
    private Text memberCount;
    
    private Dialog memberDetailDialog;    
    private Person samplePerson;

    private final PersonService samplePersonService;
    private final MemberSubscriptionService subscriptionService;
    
    private int associationId;

    public MitgliederView(PersonService samplePersonService, MemberSubscriptionService subscriptionService) {
        this.samplePersonService = samplePersonService;
        this.subscriptionService = subscriptionService;
        
        addClassNames("mitglieder-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        
        associationId = MainLayout.getAssociationId();

        createGridLayout(splitLayout);
        add(splitLayout);

        // Configure Grid       
        createMemberDetailsDialog();
        
        grid.addColumn(p -> p.getMemberNumber()).setAutoWidth(true).setHeader("Mitgliedsnummer");
        grid.addColumn(p -> p.getFirstName() + " " + p.getLastName()).setAutoWidth(true).setHeader("Name");
        grid.addColumn(p -> p.getEmail()).setAutoWidth(true).setHeader("Email");      
        grid.addComponentColumn(item -> new Button("Details", click -> {
        	this.samplePerson = item;
        	putValuesInDialog();
        	memberDetailDialog.open();
            refreshGrid();
        }));
        
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
		
		H1 h1 = new H1();
		h1.add("Mitgliedsinformationen");
		
		Hr hr = new Hr();
		
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
        
        role = new ComboBox<AssociationRole>("Rolle im Verein");
        role.setItems(Arrays.asList(AssociationRole.values()));
        role.setItemLabelGenerator(e -> e.getLabel());
		
		layout.add(numberField, firstName, lastName, email, phone, role, dateOfBirth);
		
		Button removeButton = new Button("Löschen", e -> {
			samplePersonService.delete(this.samplePerson.getId());
			memberDetailDialog.close();
		});
		removeButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
		
		Button cancelButton = new Button("Zurück", e -> memberDetailDialog.close());
		
		Button confirmButton = new Button("Aktualisieren",e -> {	
			updatePerson();
			this.memberDetailDialog.close();
			Notification.show("Informationen aktualisiert!");
		});
		
		memberDetailDialog.getFooter().add(removeButton, cancelButton, confirmButton);
		memberDetailDialog.add(h1, hr, layout);
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

    private void createGridLayout(SplitLayout splitLayout) {
    	
    	VerticalLayout mainLayout = new VerticalLayout();
    	mainLayout.addClassNames(LumoUtility.Padding.NONE);
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        wrapper.add(grid);
        wrapper.setHeight("100%");
        
        HorizontalLayout bottomLayout = new HorizontalLayout();
        bottomLayout.setWidth("100%");
        bottomLayout.addClassNames(LumoUtility.Padding.NONE, LumoUtility.Padding.Bottom.SMALL, LumoUtility.Padding.Left.MEDIUM, LumoUtility.JustifyContent.CENTER);
        memberCount = new Text("Mitglieder: " + samplePersonService.count());
        
        bottomLayout.add(memberCount);
        
        mainLayout.add(wrapper, bottomLayout);
        splitLayout.addToPrimary(mainLayout);
    }

    private void refreshGrid() {
        List<Person> allByAssociation = samplePersonService.findAllByAssociation(associationId);
        grid.setItems(allByAssociation);
        memberCount.setText("Mitglieder: " + allByAssociation.size());
    }
}
