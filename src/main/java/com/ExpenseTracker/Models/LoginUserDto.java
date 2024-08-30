package com.ExpenseTracker.Models;



import jakarta.validation.constraints.NotBlank;

public class LoginUserDto {
	@NotBlank(message = "First Name field required")
    private String email;
	@NotBlank(message = "First Name field required")
    private String password;

    public String getEmail() {
        return email;
    }

    public LoginUserDto setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public LoginUserDto setPassword(String password) {
        this.password = password;
        return this;
    }

    @Override
    public String toString() {
        return "LoginUserDto{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
