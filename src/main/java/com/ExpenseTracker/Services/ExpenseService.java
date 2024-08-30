package com.ExpenseTracker.Services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Service;

import com.ExpenseTracker.Models.Expense;
import com.ExpenseTracker.Repositories.ExpenseRepository;

@Service
public class ExpenseService {

	@Autowired
	public ExpenseRepository expenseRepo;

	/*
	 * public List<Expense> getExpenses() {
	 * 
	 * return expenseRepo.findAll(); }
	 */

	//@PostFilter("filterObject.assignee == authentication.name")
	public Page<Expense> getExpenses(Pageable paging) {
		return (Page<Expense>) expenseRepo.findAll(paging);

	}
	
	//@PostFilter("filterObject.user.id == principal.id")
	public List<Expense> getExpenses() {
		return expenseRepo.findAll();

	}
	
	
	public Optional<Expense> getExpense(Long id) {
		return expenseRepo.findById(id);
	}

	public void saveExpense(Expense expense) {
		expenseRepo.save(expense);
	}

	public void deleteExpense(Long id) {
		expenseRepo.deleteById(id);
	}

	public Page<Expense> findByCategory(String category, Pageable pageable) {
		return (Page<Expense>) expenseRepo.findByCategory(category,  pageable);	
	}


	public Page<Expense> filterByDates(LocalDate startDate, LocalDate endDate, Pageable pageable) {
		return expenseRepo.findByDateBetween(startDate, endDate, pageable);

	}

}
