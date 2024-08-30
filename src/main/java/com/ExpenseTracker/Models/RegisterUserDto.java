package com.ExpenseTracker.Models;

import jakarta.validation.constraints.NotBlank;

public class RegisterUserDto {
	@NotBlank(message = "First field required")
	private String firstName;
	@NotBlank(message = "Last field required")
	private String lastName;
	@NotBlank(message = "Emailfield required")
    private String email;
	@NotBlank(message = "First field required")
    private String password;


    public String getEmail() {
        return email;
    }

    public RegisterUserDto setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public RegisterUserDto setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public RegisterUserDto setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }
    
    public String getLastName() {
        return lastName;
    }

    public RegisterUserDto setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    @Override
    public String toString() {
        return "RegisterUserDto{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", fullName='" + firstName + '\'' +
                '}';
    }
}
