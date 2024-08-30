package com.ExpenseTracker;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import com.ExpenseTracker.Models.Expense;
import com.ExpenseTracker.Repositories.ExpenseRepository;
import com.ExpenseTracker.Services.ExpenseService;

/*
 https://ashok-s-nair.medium.com/java-unit-testing-a-spring-boot-service-with-mockito-2362a32fe217
 */

/* this @ExtendWith(MockitoExtension.class) annotations integrates Mockito with Junit.
 * It also enables features like @Mock and @InjectMock
 * MockitoExtension creates and injects mock instances.
 */
@ExtendWith(MockitoExtension.class)
public class ExpenseService_Test {

	private List<Expense> expenses;
	private Pageable pagingSort;
	private Page<Expense> pageExpenses;
	private Expense anExpense;
	private LocalDate startDate;
	private LocalDate endDate;
	private String category;

	// @Mock creates a mock of ExpenseRepository
	@Mock
	public ExpenseRepository expenseRepo;

	// @InjectMocks mocks the dependency of ExpenseService
	// to ExpenseRepository
	@InjectMocks
	public ExpenseService expenseService;

	/*
	 * This method sets up the objects to be used for testing and represents the
	 * arrage in the AAA pattern
	 */
	@BeforeEach //Arrange
	public void setUp() {
		startDate = LocalDate.now().minusDays(7);
		endDate = LocalDate.now();
		
		category = "Food";
		/*String id1 = "66b4e04570f7563f805a96b1";
		String id2 = "66b4e04570f7563f805a96b2";
		String id3 = "66b4e04570f7563f805a96b3";
		String id4 = "66b4e04570f7563f805a96b3";*/
		
		Long id1 = 1l;
		Long id2 = 2l;
		Long id3 = 3l;
		Long id4 = 4l;

		anExpense = new Expense(id4, "Wings Meal", "KFC", 5, "Food", LocalDate.now(), LocalTime.now());
		expenses = new ArrayList<Expense>();

		Expense ex1 = new Expense(id1, "Wings Meal", "KFC", 5, "Food", LocalDate.now(), LocalTime.now());
		Expense ex3 = new Expense(id2, "Zone1", "Tfl", 5, "Travel", LocalDate.now(), LocalTime.now());
		Expense ex2 = new Expense(id3, "Pen", "WH Smith", 5, "category", LocalDate.now(), LocalTime.now());

		expenses.add(ex1);
		expenses.add(ex2);
		expenses.add(ex3);

		List<Order> orders = new ArrayList<>();

		pageExpenses = new PageImpl<Expense>(expenses);
		orders.add(new Order(Sort.Direction.ASC, "date"));

		Pageable pagingSort = PageRequest.of(0, 3, Sort.by(orders));

	}

	@Test
	public void getExpenses_Test() {

		when(expenseRepo.findAll(pagingSort)).thenReturn(pageExpenses);

		// Act
		Page<Expense> result = expenseService.getExpenses(pagingSort);
		int size = result.getSize();

		// Assert
		Assertions.assertThat(size).isGreaterThan(1);

	}

	@Test
	public void getExpense_Test() {
		// Arrange
		Long id5 = 5l;

		Optional<Expense> optinalExpense = Optional.ofNullable(new Expense());

		when(expenseRepo.findById(id5)).thenReturn(optinalExpense);

		// Act
		Expense result = expenseService.getExpense(id5).get();

		// Assert
		Assertions.assertThat(result).isNotNull();
	}

	@Test
	public void saveExpense_Test() {
		
		//Arrange
		when(expenseRepo.save(anExpense)).thenReturn(anExpense);

		// Act
		expenseService.saveExpense(anExpense);

		// Assert
		// verify makes that save method is called
		verify(expenseRepo, times(1)).save(anExpense);
	}

	@Test
	public void deleteExpense_Test() {
		// Arrange
		Long id4 = 10L;

		doNothing().when(expenseRepo).deleteById(id4);

		// Act
		expenseService.deleteExpense(id4);

		verify(expenseRepo, times(1)).deleteById(id4);
	}
		
	@Test
	public void findByCategory_Test() {
		//Arrange
		when(expenseRepo.findByCategory(category, pagingSort)).thenReturn(pageExpenses);
		
		// Act
		Page<Expense> result = expenseService.findByCategory(category, pagingSort);
		
		// Assertion
		Assertions.assertThat(result.getSize()).isGreaterThan(1);
	}
	
	@Test
	public void filterByDates_Test() {
		
		//Arrange
		when(expenseRepo.findByDateBetween(startDate, endDate, pagingSort)).thenReturn(pageExpenses);
		
		// Act
		Page<Expense> result = expenseService.filterByDates(startDate, endDate, pagingSort);
		// Assertion
		Assertions.assertThat(result.getSize()).isGreaterThan(1);
	}
}
