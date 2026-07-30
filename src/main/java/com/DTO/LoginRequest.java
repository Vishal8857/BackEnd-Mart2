package com.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {

	@NotBlank(message="Email can not be Blank")
	@Email(message = "Enter a valid email")
	private String mail;
	
	@NotBlank(message="Password can not be Blank")
	@Size(min = 3, message = "Password must be at least 8 characters")
	private String password;
	
	@NotBlank(message="Role can not be Blank")
	private String role; 
	
	
	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	
}