package com.ExpenseTracker.Models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.mapping.DBRef;
//import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

//@Data

@Entity
@Table(name = "users")
//@Document(collection = "users")
public class User implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;
	@NotBlank(message = "First Name field required")
	public String firstName;
	@NotBlank(message = "Last Name field required")
	public String lastName;
	@NotBlank(message = "Email field required")
	public String email;
	@NotBlank(message = "password field required")
	public String password;

	// one to many relations between user and reservation
	/*
	 * @OneToMany( mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval =
	 * true ) private List<Expense> expenses = new ArrayList<>();
	 */

	//@JsonManagedReference
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Expense> expenses = new ArrayList<Expense>();

	/*
	 * public List<Expense> getExpenses() { return expenses; }
	 * 
	 * public void setExpenses(List<Expense> expenses) { this.expenses = expenses; }
	 */

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String firstName() {
		return firstName;
	}

	public User setFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public String lastName() {
		return lastName;
	}

	public User setLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public User setEmail(String email) {
		this.email = email;
		return this;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return password;
	}

	public User setPassword(String password) {
		this.password = password;
		return this;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return List.of();
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return email;
	}

}