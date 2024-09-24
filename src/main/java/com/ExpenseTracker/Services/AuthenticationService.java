package com.ExpenseTracker.Services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ExpenseTracker.Models.LoginUserDto;
import com.ExpenseTracker.Models.RegisterUserDto;
import com.ExpenseTracker.Models.User;
import com.ExpenseTracker.Repositories.UserRepository;


@Service
public class AuthenticationService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	public PasswordEncoder passwordEncoder;
	@Autowired
	public AuthenticationManager AuthenticationManager;

	// create the user
	public User signup(RegisterUserDto input) {

		User user = new User()
				.setFirstName(input.getFirstName())
				.setLastName(input.getLastName())
				.setEmail(input.getEmail()).setPassword(passwordEncoder.encode(input.getPassword()));
		return userRepository.save(user);
	}

	// check if the use exists
	public User authenticate(LoginUserDto input) {
		AuthenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(input.getEmail(), input.getPassword())
				);
		return userRepository.findByEmail(input.getEmail()).orElseThrow();
	}
	
	/*public List<User> allUsers() {
        List<User> users = new ArrayList<>();

        userRepository.findAll().forEach(users::add);

        return users;
    }*/
}