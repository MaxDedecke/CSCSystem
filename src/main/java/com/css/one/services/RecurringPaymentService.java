package com.css.one.services;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.css.one.data.RecurringPayment;
import com.css.one.data.Transaction;
import com.css.one.data.enums.TimeDelcaration;
import com.css.one.data.repos.RecurringPaymentRepository;

@Service
public class RecurringPaymentService {
	private final RecurringPaymentRepository repository;

	public RecurringPaymentService(RecurringPaymentRepository repository) {
		this.repository = repository;
	}

	public Optional<RecurringPayment> get(Long id) {
		return repository.findById(id);
	}

	public RecurringPayment update(RecurringPayment entity) {
		return repository.save(entity);
	}

	public void delete(Long id) {
		repository.deleteById(id);
	}
	
	public boolean deleteConnectedTransaction(int associationId, Long transactionId) {
		List<Boolean> returnValue = new ArrayList<>();
		
		findAllByAssociation(associationId).forEach(e -> {
			Transaction transactionToDelete = null;
			
			for(Transaction transaction : e.getTransactions()) {
				if(transaction.getId().equals(transactionId)) {
					transactionToDelete = transaction;
				}
			}
			
			if(transactionToDelete != null) {
				e.getTransactions().remove(transactionToDelete);
				update(e);
				returnValue.add(true);
			}
		});

		return (!returnValue.isEmpty());
	}

	public int count() {
		return (int) repository.count();
	}

	public List<RecurringPayment> findAllByAssociation(int associationId) {
		return repository.findAll().stream().filter(e -> e.getAssociationId() == associationId).toList();
	}

	public Map<RecurringPayment, Boolean> createUpdateMap(int associationId) {

		Map<RecurringPayment, Boolean> map = new HashMap<>();

		LocalDate now = LocalDate.now();
		List<RecurringPayment> allByAssociation = findAllByAssociation(associationId);

		allByAssociation.forEach(e -> {

			if (e.isActive()) {
				TimeDelcaration timeDeclaration = e.getTimeDeclaration();

				if (timeDeclaration.getType() == 1) {
					switch (timeDeclaration) {

					case DAY_FRIDAY: {
						if (now.getDayOfWeek() == DayOfWeek.FRIDAY) {
							Optional<Transaction> any = e.getTransactions().stream()
									.filter(t -> t.getDateOfTransaction().getDayOfWeek() == DayOfWeek.FRIDAY
											&& t.getDateOfTransaction().getDayOfMonth() <= now.getDayOfMonth())
									.findAny();
							map.put(e, !any.isPresent());
						} else {
							map.put(e, false);
						}
						break;
					}
					case DAY_MONDAY: {
						if (now.getDayOfWeek() == DayOfWeek.MONDAY) {
							Optional<Transaction> any = e.getTransactions().stream()
									.filter(t -> t.getDateOfTransaction().getDayOfWeek() == DayOfWeek.MONDAY
											&& t.getDateOfTransaction().getDayOfMonth() <= now.getDayOfMonth())
									.findAny();
							map.put(e, !any.isPresent());
						} else {
							map.put(e, false);
						}
						break;
					}
					case DAY_SATURDAY: {
						if (now.getDayOfWeek() == DayOfWeek.SATURDAY) {
							Optional<Transaction> any = e.getTransactions().stream()
									.filter(t -> t.getDateOfTransaction().getDayOfWeek() == DayOfWeek.SATURDAY
											&& t.getDateOfTransaction().getDayOfMonth() <= now.getDayOfMonth())
									.findAny();
							map.put(e, !any.isPresent());
						} else {
							map.put(e, false);
						}
						break;
					}
					case DAY_SUNDAY: {
						if (now.getDayOfWeek() == DayOfWeek.SUNDAY) {
							Optional<Transaction> any = e.getTransactions().stream()
									.filter(t -> t.getDateOfTransaction().getDayOfWeek() == DayOfWeek.SUNDAY
											&& t.getDateOfTransaction().getDayOfMonth() <= now.getDayOfMonth())
									.findAny();
							map.put(e, !any.isPresent());
						} else {
							map.put(e, false);
						}
						break;
					}
					case DAY_THURSDAY: {
						if (now.getDayOfWeek() == DayOfWeek.THURSDAY) {
							Optional<Transaction> any = e.getTransactions().stream()
									.filter(t -> t.getDateOfTransaction().getDayOfWeek() == DayOfWeek.THURSDAY
											&& t.getDateOfTransaction().getDayOfMonth() <= now.getDayOfMonth())
									.findAny();
							map.put(e, !any.isPresent());
						} else {
							map.put(e, false);
						}
						break;
					}
					case DAY_TUESDAY: {
						if (now.getDayOfWeek() == DayOfWeek.TUESDAY) {
							Optional<Transaction> any = e.getTransactions().stream()
									.filter(t -> t.getDateOfTransaction().getDayOfWeek() == DayOfWeek.TUESDAY
											&& t.getDateOfTransaction().getDayOfMonth() <= now.getDayOfMonth())
									.findAny();
							map.put(e, !any.isPresent());
						} else {
							map.put(e, false);
						}
						break;
					}
					case DAY_WENDSDAY: {
						if (now.getDayOfWeek() == DayOfWeek.WEDNESDAY) {
							Optional<Transaction> any = e.getTransactions().stream()
									.filter(t -> t.getDateOfTransaction().getDayOfWeek() == DayOfWeek.WEDNESDAY
											&& t.getDateOfTransaction().getDayOfMonth() <= now.getDayOfMonth())
									.findAny();
							map.put(e, !any.isPresent());
						} else {
							map.put(e, false);
						}
						break;
					}
					default:
						map.put(e, false);
					}
				}

				if (timeDeclaration.getType() == 2) {
					switch (timeDeclaration) {

					case BEGIN_OF_MONTH: {
						Optional<Transaction> any = e.getTransactions().stream()
								.filter(t -> t.getDateOfTransaction().getMonthValue() == now.getMonthValue()
										&& t.getDateOfTransaction().getDayOfMonth() == 1)
								.findAny();
						map.put(e, !any.isPresent());
						break;
					}
					case MIDDEL_OF_MONTH: {
						if (now.getDayOfMonth() >= 15) {
							Optional<Transaction> any = e.getTransactions().stream()
									.filter(t -> t.getDateOfTransaction().getMonthValue() == now.getMonthValue()
											&& t.getDateOfTransaction().getDayOfMonth() == 15)
									.findAny();
							map.put(e, !any.isPresent());
						} else {
							map.put(e, false);
						}
						break;
					}
					case END_OF_MONTH: {
						if (now.getDayOfMonth() >= 28) {
							Optional<Transaction> any = e.getTransactions().stream()
									.filter(t -> t.getDateOfTransaction().getMonthValue() == now.getMonthValue()
											&& t.getDateOfTransaction().getDayOfMonth() >= 28)
									.findAny();
							map.put(e, !any.isPresent());
						} else {
							map.put(e, false);
						}
						break;
					}
					default:
						map.put(e, false);
					}

				}

				if (timeDeclaration.getType() == 3) {
					Optional<Transaction> any = e.getTransactions().stream()
							.filter(t -> t.getDateOfTransaction().getMonthValue() == now.getMonthValue()).findAny();
					map.put(e, !any.isPresent());
				}

				if (timeDeclaration.getType() == 4) {

					switch (timeDeclaration) {
					case APRIL_OKTOBER: {
						if (now.getMonthValue() == 4) {
							Optional<Transaction> any = e.getTransactions().stream()
									.filter(t -> t.getDateOfTransaction().getMonthValue() == 4).findAny();
							map.put(e, !any.isPresent());
						} else if (now.getMonthValue() == 10) {
							Optional<Transaction> any = e.getTransactions().stream()
									.filter(t -> t.getDateOfTransaction().getMonthValue() == 10).findAny();
							map.put(e, !any.isPresent());
						} else {
							map.put(e, false);
						}
						break;
					}
					case FEBRUARY_AUGUST: {
						if (now.getMonthValue() == 2) {
							Optional<Transaction> any = e.getTransactions().stream()
									.filter(t -> t.getDateOfTransaction().getMonthValue() == 2).findAny();
							map.put(e, !any.isPresent());
						} else if (now.getMonthValue() == 8) {
							Optional<Transaction> any = e.getTransactions().stream()
									.filter(t -> t.getDateOfTransaction().getMonthValue() == 8).findAny();
							map.put(e, !any.isPresent());
						} else {
							map.put(e, false);
						}
						break;
					}
					case JANUARY_JULY: {
						if (now.getMonthValue() == 1) {
							Optional<Transaction> any = e.getTransactions().stream()
									.filter(t -> t.getDateOfTransaction().getMonthValue() == 1).findAny();
							map.put(e, !any.isPresent());
						} else if (now.getMonthValue() == 7) {
							Optional<Transaction> any = e.getTransactions().stream()
									.filter(t -> t.getDateOfTransaction().getMonthValue() == 7).findAny();
							map.put(e, !any.isPresent());
						} else {
							map.put(e, false);
						}
						break;
					}
					case JUNE_DECEMBER: {
						if (now.getMonthValue() == 6) {
							Optional<Transaction> any = e.getTransactions().stream()
									.filter(t -> t.getDateOfTransaction().getMonthValue() == 6).findAny();
							map.put(e, !any.isPresent());
						} else if (now.getMonthValue() == 12) {
							Optional<Transaction> any = e.getTransactions().stream()
									.filter(t -> t.getDateOfTransaction().getMonthValue() == 12).findAny();
							map.put(e, !any.isPresent());
						} else {
							map.put(e, false);
						}
						break;
					}
					case MAI_NOVEMBER: {
						if (now.getMonthValue() == 5) {
							Optional<Transaction> any = e.getTransactions().stream()
									.filter(t -> t.getDateOfTransaction().getMonthValue() == 5).findAny();
							map.put(e, !any.isPresent());
						} else if (now.getMonthValue() == 11) {
							Optional<Transaction> any = e.getTransactions().stream()
									.filter(t -> t.getDateOfTransaction().getMonthValue() == 11).findAny();
							map.put(e, !any.isPresent());
						} else {
							map.put(e, false);
						}

						break;
					}
					case MARCH_SEPTEMBER: {
						if (now.getMonthValue() == 3) {
							Optional<Transaction> any = e.getTransactions().stream()
									.filter(t -> t.getDateOfTransaction().getMonthValue() == 3).findAny();
							map.put(e, !any.isPresent());
						} else if (now.getMonthValue() == 9) {
							Optional<Transaction> any = e.getTransactions().stream()
									.filter(t -> t.getDateOfTransaction().getMonthValue() == 9).findAny();
							map.put(e, !any.isPresent());
						} else {
							map.put(e, false);
						}
						break;
					}

					default:
						map.put(e, false);
						break;
					}
				}

			}
		});

		return map;
	}
}
