package com.css.one.views.finanzen;

import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.vaadin.addons.MoneyField;
import org.vaadin.lineawesome.LineAwesomeIcon;

import com.css.one.data.Transaction;
import com.css.one.data.TransactionType;
import com.css.one.services.TransactionService;
import com.css.one.views.MainLayout;
import com.css.one.views.arbeitsplanung.ArbeitsplanungView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Finanzen")
@Route(value = "finanzen", layout = MainLayout.class)
@AnonymousAllowed
public class FinanzenView extends Div implements BeforeEnterObserver {

	private static final long serialVersionUID = -4231560701323089634L;

	private final TransactionService transactionService;
	private final BeanValidationBinder<Transaction> binder;

	private final String TRANSACTION_ID = "transactionID";
//	private final String TRANSACTION_EDIT_ROUTE_TEMPLATE = "finanzen/%s/edit";

	private final Grid<Transaction> grid = new Grid<>(Transaction.class, false);

	private TextField note;
	private DateTimePicker date;
	private ComboBox<TransactionType> type;
	private MoneyField amount;

	private Transaction transaction;

	private final Button cancel = new Button("Abbrechen");
	private final Button save = new Button("Buchen");

	private Button costButton;
	private Button incomeButton;
	private Button allTransactionsButton;

	private H2 sum;
	private H2 balance;
	
	private int associationId;
	
	private TransactionType currentType;
	
	public FinanzenView(TransactionService transactionService) {
		this.transactionService = transactionService;
		addClassNames("finanzen-view");

		// Create UI
		SplitLayout splitLayout = new SplitLayout();

		createGridLayout(splitLayout);
		createEditorLayout(splitLayout);

		add(splitLayout);

		NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("de", "DE"));
		associationId = MainLayout.getAssociationId();

		grid.addColumn(t -> t.getType().getDisplayName()).setAutoWidth(true).setHeader("Typ");
		grid.addColumn(t -> formatter.format(t.getAmount())).setAutoWidth(true).setHeader("Betrag");
		grid.addColumn(t -> renderDate(t.getDateOfTransaction())).setAutoWidth(true).setHeader("Zeitpunkt");
		grid.addColumn(t -> t.getNote()).setAutoWidth(true).setHeader("Notiz");
		grid.addComponentColumn(item -> new Button("Löschen", click -> {
			transactionService.delete(item.getId());
			refreshGrid();
		}));
		
		this.currentType = null;


//		grid.setItems(query -> transactionService.list(
//				PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
//				.stream());
		grid.setItems(transactionService.findAllByAssociation(associationId));
		
		grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

		binder = new BeanValidationBinder<>(Transaction.class);

		// when a row is selected or deselected, populate form
		grid.asSingleSelect().addValueChangeListener(event -> {
			if (event.getValue() != null) {

			} else {
				clearForm();
				UI.getCurrent().navigate(ArbeitsplanungView.class);
			}
		});

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
				
				if (amount.getValue() == null || amount.getValue().equals("0,00")) {
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
			} catch (ValidationException validationException) {
				Notification.show("Failed to update the data. Check again that all values are valid");
			}

			clearForm();
		});

		sum.setText(formatter.format(transactionService.getBalanceForType(null, associationId).getAmount()));
	}

	private void createGridLayout(SplitLayout splitLayout) {

		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setMargin(true);
		horizontalLayout.setAlignItems(Alignment.CENTER);
		addComponentsForTransactionTypes(horizontalLayout);
		horizontalLayout.setWidth(1000, Unit.PIXELS);

		Div wrapper = new Div();
		wrapper.setClassName("grid-wrapper");

		splitLayout.setSplitterPosition(70);
		splitLayout.addToPrimary(wrapper);

		wrapper.add(horizontalLayout);
		wrapper.add(new Hr());
		wrapper.add(grid);
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

	private void addComponentsForTransactionTypes(HorizontalLayout horizontalLayout) {

		VerticalLayout layout = new VerticalLayout();
		layout.add(LineAwesomeIcon.MONEY_CHECK_SOLID.create());
		layout.add(new H2("Alle"));
		layout.setAlignItems(Alignment.CENTER);
		allTransactionsButton = new Button(layout);
		allTransactionsButton.setHeight(100, Unit.PIXELS);
		allTransactionsButton.setWidth(250, Unit.PIXELS);
		allTransactionsButton.addClickListener(e -> {
			refreshGridWithType(null);
			this.currentType = null;
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
		});

		horizontalLayout.add(costButton);

		layout = new VerticalLayout();
		layout.add(LineAwesomeIcon.PLUS_CIRCLE_SOLID.create());
		layout.add(new H2(TransactionType.INCOME.getLabel()));
		layout.setAlignItems(Alignment.CENTER);
		incomeButton = new Button(layout);
		incomeButton.setHeight(100, Unit.PIXELS);
		incomeButton.setWidth(200, Unit.PIXELS);

		incomeButton.addClickListener(e -> {
			refreshGridWithType(TransactionType.INCOME);
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
		formLayout.add(type, amount, date, note);

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
		grid.select(null);
		
		if(currentType == null) {
			grid.setItems(transactionService.findAllByAssociation(associationId));
		} else {
			grid.setItems(transactionService.findByType(currentType, associationId));
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
