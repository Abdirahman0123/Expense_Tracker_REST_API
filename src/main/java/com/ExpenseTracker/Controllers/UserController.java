package com.ExpenseTracker.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ExpenseTracker.Models.Expense;
import com.ExpenseTracker.Models.User;
import com.ExpenseTracker.Repositories.ExpenseRepository;
import com.ExpenseTracker.Services.UserService;



@RequestMapping("/users")
@RestController
public class UserController {
	@Autowired
    private  UserService userService;
	@Autowired
	public ExpenseRepository expenseRepository;
    @GetMapping("/me")
    public ResponseEntity<User> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) authentication.getPrincipal();
        
        System.out.print(" currentUser + " + currentUser.getId());
        
        return ResponseEntity.ok(currentUser);
    }

    @GetMapping
    public ResponseEntity<List<User>> allUsers() {
        List <User> users = userService.allUsers();

        return ResponseEntity.ok(users);
    }
    
    /*@GetMapping("hello")
    public ResponseEntity<List<Expense>>  hello() {
    	
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) authentication.getPrincipal();
        List<Expense> w = expenseRepository.findByUser(currentUser.getId());
        return ResponseEntity.ok(w);
    }*/
    
}