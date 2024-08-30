package com.ExpenseTracker.Services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import com.ExpenseTracker.Models.Expense;
import com.ExpenseTracker.Models.User;
import com.ExpenseTracker.Repositories.UserRepository;


@Service
public class UserService {
	
	@Autowired
	private  UserRepository userRepository;
	
    /*private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }*/

    public List<User> allUsers() {
        List<User> users = new ArrayList<>();

        userRepository.findAll().forEach(users::add);

        return users;
    }
    
    
}
