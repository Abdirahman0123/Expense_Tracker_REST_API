package com.ExpenseTracker.Repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
//import org.springframework.data.mongodb.repository.MongoRepository;
//import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Repository;

import com.ExpenseTracker.Models.Expense;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long>{
	//@PostFilter("filterObject.assignee == authentication.name")
	Page<Expense> findByCategory(String category,  Pageable pageable);
	//@Query(value="SELECT * FROM expenses  WHERE expenses.user_id=?#{principal.username}", nativeQuery = true)
	//@PostFilter("filterObject.user.getId() == principal.id")
	Page<Expense> findByDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
	

}
