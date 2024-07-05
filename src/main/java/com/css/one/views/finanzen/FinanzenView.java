package com.css.one.views.finanzen;

import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.vaadin.addons.MoneyField;
import org.vaadin.lineawesome.LineAwesomeIcon;

import com.css.one.data.MemberSubscription;
import com.css.one.data.PaymentMethod;
import com.css.one.data.Person;
import com.css.one.data.Transaction;
import com.css.one.data.TransactionType;
import com.css.one.services.AssociationService;
import com.css.one.services.MemberSubscriptionService;
import com.css.one.services.PersonService;
import com.css.one.services.TransactionService;
import com.css.one.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.Height;

@PageTitle("Finanzen")
@Route(value = "finanzen", layout = MainLayout.class)
@AnonymousAllowed
public class FinanzenView extends Div implements BeforeEnterObserver {

	private static final long serialVersionUID = -4231560701323089634L;

	private final TransactionService transactionService;
	private final PersonService personService;
	private final MemberSubscriptionService memberSubscriptionService;
	private final AssociationService associationService;
	private final BeanValidationBinder<Transaction> binder;

	private final String TRANSACTION_ID = "transactionID";
//	private final String TRANSACTION_EDIT_ROUTE_TEMPLATE = "finanzen/%s/edit";

	private final Grid<Transaction> grid = new Grid<>(Transaction.class, false);
	private final Grid<MemberSubscription> gridMemberSubscription = new Grid<>(MemberSubscription.class, false);
	private Grid<Transaction> memberTransactionGrid = new Grid<>(Transaction.class, false);
	Optional<Person> optionalMember;
	Transaction memberSubscriptionTransaction;
	
	private TextField note;
	private DateTimePicker date;
	private ComboBox<TransactionType> type;
	private ComboBox<PaymentMethod> methodForSubscriptionTransaction;

	private ComboBox<PaymentMethod> paymentMethodBox;
	private ComboBox<Person> optionalPersonBox;
	
	private MoneyField amount;
	private Transaction transaction;

	private TabSheet tabSheet = new TabSheet();
	private final Button cancel = new Button("Abbrechen");
	private final Button save = new Button("Buchen");

	private Button costButton;
	private Button incomeButton;
	private Button allTransactionsButton;
	
	private Dialog confirmMonthlyPaymentDialog;
	private TextField nameFieldMember;

	private Text month;
	private int monthValue;
	private int year;
	
	private H2 sum;
	private H2 balance;
	
	private int associationId;
	
	private MemberSubscription selectedSubscription;
	
	private TransactionType currentType;
	NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("de", "DE"));
	
	public enum ViewStatus {
		GENERAL, SUBSCRIPTIONS, RECURRING_PAYMENTS
	}
	
	public ViewStatus status = ViewStatus.GENERAL;
	
	public FinanzenView(TransactionService transactionService, PersonService personService, MemberSubscriptionService memberSubscriptionService, AssociationService associationService) {
		this.transactionService = transactionService;
		this.personService = personService;
		this.memberSubscriptionService = memberSubscriptionService;
		this.associationService = associationService;
		
		addClassNames("finanzen-view");
		
		associationId = MainLayout.getAssociationId();
		startCheckingRoutines();
		
		// Create UI
		SplitLayout splitLayout = new SplitLayout();
		createGridLayout(splitLayout);
		createEditorLayout(splitLayout);
		refreshGrid();
		add(splitLayout);

		binder = new BeanValidationBinder<>(Transaction.class);

		cancel.addClickListener(e -> {
			clearForm();
			refreshGrid();
		});

		save.addClickListener(e -> {
			try {
				this.transaction = new Transaction();
				transaction.setNote(note.getValue());
				transaction.setType(type.getValue());
				transaction.setDateOfTransaction(date.getValue().toLocalDate());
				transaction.setAssociationId(associationId);
				transaction.setPaymentMethod(paymentMethodBox.getValue());
				
				if(optionalPersonBox.getValue() != null) {
					transaction.setMemberId(optionalPersonBox.getValue().getId().intValue());
				}
				
				if (amount.getValue() == null || amount.getValue().toString().equals("0,00")) {
					Notification.show("Ohne Betrag kann keine Ausgabe/Einnahme gebucht werden !");
				} else {
					transaction.setAmount(amount.getValue().getNumber().doubleValue());
					binder.writeBean(this.transaction);
					transactionService.update(this.transaction);
					clearForm();
					refreshGrid();
					Notification.show("Data updated");
					UI.getCurrent().navigate(FinanzenView.class);
				}
			} catch (ObjectOptimisticLockingFailureException exception) {
				Notification n = Notification.show(
						"Error updating the data. Somebody else has updated the record while you were making changes.");
				n.setPosition(Position.MIDDLE);
				n.addThemeVariants(NotificationVariant.LUMO_ERROR);
			} 
			catch (ValidationException validationException) {
				Notification.show("Failed to update the data. Check again that all values are valid");
			}

			clearForm();
		});

		sum.setText(formatter.format(transactionService.getBalanceForType(null, associationId).getAmount()));
	}

	private void startCheckingRoutines() {		
		LocalDate now = LocalDate.now();
		if(memberSubscriptionService.needToCreateNewSubscriptions(associationId, now.getYear(), now.getMonthValue())) {
			memberSubscriptionService.createSubscriptionsForMonth(personService.findAllByAssociation(associationId), now.getYear(), now.getMonthValue(), associationId);
			
		
		}
	}

	private String resolveMember(int memberId) {
		optionalMember = personService.get(Integer.toUnsignedLong(memberId));
		return optionalMember.isPresent() ? optionalMember.get().getFirstName() + " " + optionalMember.get().getLastName() : "-";
	}

	private void createGridLayout(SplitLayout splitLayout) {
		
//		tabSheet.addThemeVariants(TabSheetVariant.MATERIAL_BORDERED);
		tabSheet.setSizeFull();
		tabSheet.add("Allgemein", createGeneralTab());
		tabSheet.add("Regelmäßige Zahlungen", createRecurringPaymentsTab());
		tabSheet.add("Mitgliedsbeiträge", createMemberPaymentTab());
		
		tabSheet.addSelectedChangeListener(e -> {
			
			if(e.getSelectedTab().getLabel().equals("Allgemein")) {
				status = ViewStatus.GENERAL;
				splitLayout.getSecondaryComponent().setVisible(true);
			} else if(e.getSelectedTab().getLabel().equals("Mitgliedsbeiträge")) {
				status = ViewStatus.SUBSCRIPTIONS;
				splitLayout.getSecondaryComponent().setVisible(false);
			} else {
				status = ViewStatus.RECURRING_PAYMENTS;
				splitLayout.getSecondaryComponent().setVisible(true);
			}
			
			refreshSecondary(e.getSelectedTab().getLabel());
			refreshGrid();
		});
		
		splitLayout.setSplitterPosition(70);
		splitLayout.addToPrimary(tabSheet);
	}
	
	private void refreshSecondary(String label) {
		//refresh input on the right side according to selected tab
		
		if (status == ViewStatus.GENERAL) {

		} else if(status == ViewStatus.RECURRING_PAYMENTS) {
			
		}
	}

	private Component createRecurringPaymentsTab() {
		Div wrapper = new Div();
		wrapper.setClassName("grid-wrapper");
		
		
		return wrapper;
	}

	private Component createMemberPaymentTab() {
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.addClassNames(Height.FULL, LumoUtility.Padding.NONE);
		Div wrapper = new Div();
		wrapper.setClassName("grid-wrapper");
		wrapper.setHeight("100%");
		
		buildConfirmDialog();
		
		LocalDate now = LocalDate.now();
		HorizontalLayout layoutMonthSelection = new HorizontalLayout();
		layoutMonthSelection.setWidth("100%");
		layoutMonthSelection.add(createMonthSelectionComponent(now));
		
		gridMemberSubscription.addThemeVariants(GridVariant.LUMO_NO_BORDER);
		gridMemberSubscription.addColumn(e -> resolveMember(e.getPersonId())).setAutoWidth(true).setHeader("Mitglied");
		
		LitRenderer<MemberSubscription> isPayedRenderer = LitRenderer.<MemberSubscription>of(
				"<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>")
				.withProperty("icon", important -> important.isPayed() ? "check" : "minus")
				.withProperty("color", important -> important.isPayed() ? "var(--lumo-success-color)"
						: "var(--lumo-error-color)");
		 
		gridMemberSubscription.addColumn(isPayedRenderer).setAutoWidth(true).setHeader("Bezahlt").setKey("payed").setComparator((sub1, sub2) -> Boolean.compare(sub1.isPayed(), sub2.isPayed()));
		gridMemberSubscription.addColumn(e -> resolveTransaction(e.getTransactionId())).setAutoWidth(true).setHeader("Bezahlt am");
		
		gridMemberSubscription.addComponentColumn(item -> new Button("Beitrag verbuchen", click -> {
			if (item.isPayed()) {
				Notification.show("Das Mitglied hat seinen Monatsbeitrag bereits gezahlt.");
			} else {
				this.selectedSubscription = item;
				this.nameFieldMember.setValue(resolveMember(this.selectedSubscription.getPersonId()));
				confirmMonthlyPaymentDialog.open();
			}
        }));

		gridMemberSubscription.setItems(memberSubscriptionService.findByMonthAndYear(now.getMonthValue(), now.getYear(), associationId));
		gridMemberSubscription.addClassNames(LumoUtility.Height.FULL, LumoUtility.Border.ALL);
		
		wrapper.add(gridMemberSubscription);
		mainLayout.add(layoutMonthSelection);
		mainLayout.add(wrapper);
		return mainLayout;
	}

	private void refreshMemberTransactionsGrid() {
		if(optionalMember.isPresent()) {
			
			List<Transaction> listMemberTransactions = transactionService.findAllByAssociation(associationId).stream().filter(e -> e.getMemberId() == optionalMember.get().getId().intValue()).toList();
			memberTransactionGrid.setItems(listMemberTransactions.stream()
					.filter(e -> (e.getDateOfTransaction().getMonthValue() == monthValue
							&& e.getDateOfTransaction().getYear() == year))
					.toList().stream()
					.filter(e -> e.getType() == TransactionType.INCOME).toList());
		}
	}

	private void buildConfirmDialog() {
		
		this.confirmMonthlyPaymentDialog = new Dialog();
		double amountMemberSubscription = associationService.get(Integer.toUnsignedLong(associationId)).get().getAmountMemberSubscription();
		
		FormLayout layout = new FormLayout();
		layout.setMinWidth("50%");
		layout.setMaxWidth("100%");
		
		nameFieldMember = new TextField("Name");
		nameFieldMember.setEnabled(false);
		TextField fieldAmount = new TextField("Mitgliedsbeitrag");
		fieldAmount.setValue(String.valueOf(amountMemberSubscription) + "€");
		fieldAmount.setEnabled(false);
		
		HorizontalLayout checkboxLayout = new HorizontalLayout();
		checkboxLayout.addClassNames(LumoUtility.Margin.NONE, LumoUtility.Width.AUTO, LumoUtility.Padding.NONE);
		
		Checkbox createTransactionBox = new Checkbox("Buchung erstellen");
		createTransactionBox.addClassNames(LumoUtility.Margin.Top.SMALL);
		createTransactionBox.setValue(true);
		
		Checkbox linkTransactionBox = new Checkbox("Buchung verknüpfen");
		linkTransactionBox.addClassNames(LumoUtility.Margin.SMALL);
		
		checkboxLayout.add(createTransactionBox, linkTransactionBox);
		
		layout.add(nameFieldMember, fieldAmount, checkboxLayout);
		layout.setColspan(checkboxLayout, 2);
		
		methodForSubscriptionTransaction = new ComboBox<PaymentMethod>("Zahlungsart");
		methodForSubscriptionTransaction.setItems(PaymentMethod.values());
		methodForSubscriptionTransaction.setItemLabelGenerator(e -> e.getLabel());
		methodForSubscriptionTransaction.setValue(methodForSubscriptionTransaction.getListDataView().getItem(0));
		methodForSubscriptionTransaction.addClassNames(LumoUtility.Margin.Bottom.MEDIUM);
		
		memberTransactionGrid.setMinHeight(100, Unit.PIXELS);
		memberTransactionGrid.addColumn(t -> t.getDateOfTransaction()).setAutoWidth(true).setHeader("Datum");
		memberTransactionGrid.addColumn(t -> t.getAmount()).setAutoWidth(true).setHeader("Betrag");
		memberTransactionGrid.addColumn(t -> t.getNote()).setAutoWidth(true).setHeader("Notiz");
		memberTransactionGrid.setSelectionMode(SelectionMode.SINGLE);
		memberTransactionGrid.addSelectionListener(e -> this.memberSubscriptionTransaction = e.getAllSelectedItems().iterator().next());
		memberTransactionGrid.setEnabled(false);
		
		this.confirmMonthlyPaymentDialog.add(layout, methodForSubscriptionTransaction, memberTransactionGrid);
		
		createTransactionBox.addValueChangeListener(e -> {
			if (e.getValue()) {
				linkTransactionBox.setValue(false);
				methodForSubscriptionTransaction.setEnabled(true);
				methodForSubscriptionTransaction.setValue(methodForSubscriptionTransaction.getListDataView().getItem(0));
			}
		});
		
		linkTransactionBox.addValueChangeListener(e -> {
			if (e.getValue()) {
				createTransactionBox.setValue(false);
				refreshMemberTransactionsGrid();
				methodForSubscriptionTransaction.setEnabled(false);
				methodForSubscriptionTransaction.setValue(methodForSubscriptionTransaction.getEmptyValue());
			}
			memberTransactionGrid.setEnabled(e.getValue());
		});
		
		Button cancelButton = new Button("Zurück", e -> confirmMonthlyPaymentDialog.close());
		
		Button confirmButton = new Button("Bestätigen", e -> {
			if (createTransactionBox.getValue() || linkTransactionBox.getValue()) {
				this.selectedSubscription.setPayed(true);
				if (linkTransactionBox.getValue()) {
					if (memberSubscriptionTransaction != null) {
						this.selectedSubscription.setTransactionId(memberSubscriptionTransaction.getId().intValue());
					} else {
						Notification.show("Es muss eine Transaktion hinterlegt sein!");
					}
				} else {
					memberSubscriptionTransaction = new Transaction();
					memberSubscriptionTransaction.setAmount(amountMemberSubscription);
					memberSubscriptionTransaction.setAssociationId(associationId);
					memberSubscriptionTransaction.setDateOfTransaction(LocalDate.now());
					memberSubscriptionTransaction.setNote("Mitgliedsbeitrag");
					memberSubscriptionTransaction.setType(TransactionType.INCOME);
					memberSubscriptionTransaction.setPaymentMethod(methodForSubscriptionTransaction.getValue());
					if(optionalMember.isPresent()) {						
						memberSubscriptionTransaction.setMemberId(optionalMember.get().getId().intValue());
					}
					Transaction update = this.transactionService.update(memberSubscriptionTransaction);
					this.selectedSubscription.setTransactionId(update.getId().intValue());
				}
				
				memberSubscriptionService.update(this.selectedSubscription);
				refreshGrid();
				this.selectedSubscription = null;
				this.confirmMonthlyPaymentDialog.close();
				Notification.show("Mitgliedsbeitrag gebucht!");
			} else {
				Notification.show("Es muss eine Transaktion hinterlegt sein!");
			}
		});
		
		this.confirmMonthlyPaymentDialog.getFooter().add(cancelButton, confirmButton);
		
	}

	private Component createMonthSelectionComponent(LocalDate now) {
		
		HorizontalLayout innerLayout = new HorizontalLayout();
		innerLayout.addClassNames(LumoUtility.Border.ALL, LumoUtility.BorderColor.CONTRAST_10, LumoUtility.BorderRadius.LARGE, LumoUtility.JustifyContent.CENTER, LumoUtility.Padding.MEDIUM);
		
		innerLayout.setWidth("100%");
		
		HorizontalLayout monthLayout = new HorizontalLayout();
		month = new Text(renderTime(now.getMonthValue(), now.getYear()));
		H3 h3 = new H3(month);
		monthLayout.add(h3);
		monthLayout.setWidth(200, Unit.PIXELS);
		monthLayout.addClassNames(LumoUtility.JustifyContent.CENTER, LumoUtility.Margin.Top.SMALL);
		
		Button buttonLeft = new Button("<");
		LocalDate registrationDate = associationService.get(Integer.toUnsignedLong(associationId)).get().getRegistrationDate();
	
		buttonLeft.addClickListener(e -> {

			if (registrationDate.getYear() < this.year || registrationDate.getMonthValue() < this.monthValue) {
				if (this.monthValue == 1) {
					this.monthValue = 12;
					this.year = this.year - 1;
				} else {
					this.monthValue = this.monthValue - 1;
				}
				refreshGrid();
				month.setText(renderTime(this.monthValue, this.year));
			} else {
				Notification.show("Der Verein wurde erst zum " + registrationDate.getDayOfMonth() + "." + registrationDate.getMonthValue() + "." + registrationDate.getYear() + " registriert.");
			}
		});
		
		Button buttonRight = new Button(">");
		
		buttonRight.addClickListener(e -> {
			
			if (now.getYear() > this.year || this.monthValue < now.getMonthValue()) {
				if (this.monthValue == 12) {
					this.monthValue = 1;
					this.year = this.year + 1;
				} else {
					this.monthValue = this.monthValue + 1;
				}

				refreshGrid();
				month.setText(renderTime(this.monthValue, this.year));
			} else {
				Notification.show("Die Übersicht ist erst ab dem 01. des Monats verfügbar");
			}
		});
		
		innerLayout.add(buttonLeft, monthLayout, buttonRight);
		return innerLayout;
	}

	private String resolveTransaction(int transactionId) {
		
		Optional<Transaction> optionalTransaction = transactionService.get(Integer.toUnsignedLong(transactionId));
		
		if(optionalTransaction.isPresent()) {
			LocalDate dateOfTransaction = optionalTransaction.get().getDateOfTransaction();
			return dateOfTransaction.getDayOfMonth() + "." + dateOfTransaction.getMonthValue() + "." + dateOfTransaction.getYear();
		} else {			
			return "-";
		}
	}

	private Div createGeneralTab() {
		Div wrapperGeneralTab = new Div();
		wrapperGeneralTab.setHeight("100%");
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setMargin(true);
		horizontalLayout.setAlignItems(Alignment.CENTER);
		addComponentsForTransactionTypes(horizontalLayout);
		horizontalLayout.setWidth("100%");

		wrapperGeneralTab.setClassName("grid-wrapper");
		wrapperGeneralTab.add(horizontalLayout);
		wrapperGeneralTab.add(new Hr());
		
		this.currentType = null;
		grid.addColumn(t -> t.getType().getDisplayName()).setAutoWidth(true).setHeader("Typ");
		grid.addColumn(t -> formatter.format(t.getAmount())).setAutoWidth(true).setHeader("Betrag");
		grid.addColumn(t -> renderDate(t.getDateOfTransaction())).setAutoWidth(true).setHeader("Zeitpunkt");
		grid.addColumn(t -> t.getNote()).setAutoWidth(true).setHeader("Notiz");
		grid.addColumn(t -> t.getPaymentMethod().getLabel()).setAutoWidth(true).setHeader("Zahlungsmethode");
		grid.addColumn(t -> resolveMember(t.getMemberId())).setAutoWidth(true).setHeader("Mitglied");
		
		grid.addComponentColumn(item -> new Button("Löschen", click -> {
			transactionService.delete(item.getId());
			refreshGrid();
		}));
		
		//		grid.setItems(query -> transactionService.list(
		//		PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
		//		.stream());
		grid.setItems(transactionService.findAllByAssociation(associationId));
		grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
		grid.setHeight("100%");

		// when a row is selected or deselected, populate form
		grid.asSingleSelect().addValueChangeListener(event -> {
			if (event.getValue() != null) {

			} else {
				clearForm();
				UI.getCurrent().navigate(FinanzenView.class);
			}
		});

		wrapperGeneralTab.add(grid);
		
		return wrapperGeneralTab;
	}

	private String renderDate(LocalDate date) {
		String day = "";
		String month = "";

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
	}
	
	private String renderTime(int month, int year) {
		
		this.monthValue = month;
		this.year = year;
		
		switch(this.monthValue) {
		case 1: return "Januar " + String.valueOf(year);
		case 2: return "Februar "+ String.valueOf(year);
		case 3: return "März "+ String.valueOf(year);
		case 4: return "April "+ String.valueOf(year);
		case 5: return "Mai " + String.valueOf(year);
		case 6: return "Juni " + String.valueOf(year);
		case 7: return "Juli " + String.valueOf(year);
		case 8: return "August " + String.valueOf(year);
		case 9: return "September " + String.valueOf(year);
		case 10: return "Oktober " + String.valueOf(year);
		case 11: return "November " + String.valueOf(year);
		case 12: return "Dezember " + String.valueOf(year);
		default: return "Kein Monat ausgewählt";
		}
	}
	
	private void addComponentsForTransactionTypes(HorizontalLayout horizontalLayout) {

		VerticalLayout layout = new VerticalLayout();
		layout.add(LineAwesomeIcon.MONEY_CHECK_SOLID.create());
		layout.add(new H2("Alle"));
		layout.setAlignItems(Alignment.CENTER);
		
		allTransactionsButton = new Button(layout);
		allTransactionsButton.addClassNames(LumoUtility.Border.ALL, LumoUtility.BorderColor.PRIMARY_50);
		allTransactionsButton.setHeight(100, Unit.PIXELS);
		allTransactionsButton.setWidth(250, Unit.PIXELS);
		allTransactionsButton.addClickListener(e -> {
			refreshGridWithType(null);
			this.currentType = null;
			refreshButtonStyle();
			allTransactionsButton.addClassNames(LumoUtility.Border.ALL, LumoUtility.BorderColor.PRIMARY_50);
		});
		

		horizontalLayout.add(allTransactionsButton);

		layout = new VerticalLayout();
		layout.add(LineAwesomeIcon.MINUS_CIRCLE_SOLID.create());
		layout.add(new H2(TransactionType.COST.getLabel()));
		layout.setAlignItems(Alignment.CENTER);
		costButton = new Button(layout);
		costButton.setHeight(100, Unit.PIXELS);
		costButton.setWidth(200, Unit.PIXELS);

		costButton.addClickListener(e -> {
			refreshGridWithType(TransactionType.COST);
			refreshButtonStyle();
			costButton.addClassNames(LumoUtility.Border.ALL, LumoUtility.BorderColor.PRIMARY_50);
		});

		horizontalLayout.add(costButton);

		layout = new VerticalLayout();
		layout.add(LineAwesomeIcon.PLUS_CIRCLE_SOLID.create());
		layout.add(new H2(TransactionType.INCOME.getLabel() + "n"));
		layout.setAlignItems(Alignment.CENTER);
		incomeButton = new Button(layout);
		incomeButton.setHeight(100, Unit.PIXELS);
		incomeButton.setWidth(200, Unit.PIXELS);

		incomeButton.addClickListener(e -> {
			refreshGridWithType(TransactionType.INCOME);
			refreshButtonStyle();
			incomeButton.addClassNames(LumoUtility.Border.ALL, LumoUtility.BorderColor.PRIMARY_50);
		});

		horizontalLayout.add(incomeButton);

		sum = new H2("0,00€");
		balance = new H2("Balance");

		layout = new VerticalLayout();
		layout.add(balance);
		layout.add(sum);
		layout.setAlignItems(Alignment.CENTER);

		horizontalLayout.add(layout);
	}

	
	private void refreshButtonStyle() {
		incomeButton.removeClassNames(LumoUtility.Border.ALL, LumoUtility.BorderColor.PRIMARY_50);
		costButton.removeClassNames(LumoUtility.Border.ALL, LumoUtility.BorderColor.PRIMARY_50);
		allTransactionsButton.removeClassNames(LumoUtility.Border.ALL, LumoUtility.BorderColor.PRIMARY_50);
	}

	
	private void refreshGridWithType(TransactionType type) {

		NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("de", "DE"));
		sum.setText(formatter.format(transactionService.getBalanceForType(type, associationId).getAmount()));

		if (type == null) {
			balance.setText("Balance");
			grid.setItems(transactionService.findAllByAssociation(associationId));
		} else {
			balance.setText("Summe");
			grid.setItems(transactionService.findByType(type, associationId));
		}

	}
	
	
	private void createEditorLayout(SplitLayout splitLayout) {
		Div editorLayoutDiv = new Div();
		editorLayoutDiv.setClassName("editor-layout");

		Div editorDiv = new Div();
		editorDiv.setClassName("editor");
		editorLayoutDiv.add(editorDiv);

		FormLayout formLayout = new FormLayout();

		type = new ComboBox<TransactionType>("Typ");
		type.setItems(TransactionType.values());
		type.setItemLabelGenerator(e -> e.getDisplayName());
		type.setValue(type.getListDataView().getItem(0));

		amount = new MoneyField();
		amount.setLabel("Betrag");
		amount.setCurrency("EUR");

		date = new DateTimePicker();
		date.setLabel("Zeitpunkt");
		date.setStep(Duration.ofSeconds(1));
		date.setValue(LocalDateTime.now());

		note = new TextField("Notiz");
		
		optionalPersonBox = new ComboBox<Person>("Optional - Mitglied");
		optionalPersonBox.setItems(personService.findAllByAssociation(associationId));
		optionalPersonBox.setItemLabelGenerator(e -> e.getFirstName() + " " + e.getLastName());
		
		paymentMethodBox = new ComboBox<PaymentMethod>("Zahlungsmethode");
		paymentMethodBox.setItems(PaymentMethod.values());
		paymentMethodBox.setItemLabelGenerator(e -> e.getLabel());
		paymentMethodBox.setValue(paymentMethodBox.getListDataView().getItem(0));
		
		formLayout.add(type, amount, date, note, paymentMethodBox, optionalPersonBox);

		editorDiv.add(formLayout);
		createButtonLayout(editorLayoutDiv);
		splitLayout.addToSecondary(editorLayoutDiv);
	}

	
	
	private void createButtonLayout(Div editorLayoutDiv) {
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setClassName("button-layout");
		cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		buttonLayout.add(save, cancel);
		editorLayoutDiv.add(buttonLayout);
	}

	
	
	private void clearForm() {
		type.setValue(type.getListDataView().getItem(0));
		amount.setValue(amount.getEmptyValue());
		amount.setCurrency("EUR");
		date.setValue(LocalDateTime.now());
		note.setValue("");
	}
	

	
	private void refreshGrid() {
		
		if (status == ViewStatus.GENERAL) {
			grid.select(null);
			
			refreshGridWithType(null);
			
			if (currentType == null) {
				grid.setItems(transactionService.findAllByAssociation(associationId));
			} else {
				grid.setItems(transactionService.findByType(currentType, associationId));
			}
		} else if(status == ViewStatus.SUBSCRIPTIONS) {
			gridMemberSubscription.setItems(memberSubscriptionService.findByMonthAndYear(this.monthValue, this.year, associationId));
			List<GridSortOrder<MemberSubscription>> sortOrder = new ArrayList<>();	
			sortOrder.add(new GridSortOrder<MemberSubscription>(gridMemberSubscription.getColumnByKey("payed"),SortDirection.DESCENDING));
			gridMemberSubscription.sort(sortOrder);
		} else {
			
		}
	}

	
	
	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		Optional<Long> transactionId = event.getRouteParameters().get(TRANSACTION_ID).map(Long::parseLong);
		if (transactionId.isPresent()) {
			Optional<Transaction> samplePersonFromBackend = transactionService.get(transactionId.get());
			if (samplePersonFromBackend.isPresent()) {
//	                populateForm(samplePersonFromBackend.get());
			} else {
				Notification.show(
						String.format("The requested Transaction was not found, ID = %s", transactionId.get()), 3000,
						Notification.Position.BOTTOM_START);
				// when a row is selected but the data is no longer available,
				// refresh grid
				
				refreshGrid();
				event.forwardTo(FinanzenView.class);
			}
		}

	}

}
