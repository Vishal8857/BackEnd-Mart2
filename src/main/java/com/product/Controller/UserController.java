package com.product.Controller;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import com.DTO.LoginRequest;
import com.product.Entity.User;
import com.product.Repository.UserRepo;
import com.product.Service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService service;
	
	@Autowired
	private UserRepo userRepo;

	@GetMapping("/allUsers")
	public List<User> userList(){
		List<User>userData=userRepo.findAll();
		return userData;
	}
	
	@PostMapping("/register")
	public String register(@RequestBody User user) {

		return service.register(user);
	}

	@PostMapping("/login")
	public String login(@Valid @RequestBody LoginRequest request) {

		return service.login(request);
	}

	@GetMapping("/profile")
	public ResponseEntity<String> profile(Authentication authentication) {

	    String username = authentication.getName();
	    Collection<? extends GrantedAuthority> role=authentication.getAuthorities();
	    return ResponseEntity.ok(username+" "+ role);
	}
	
	@GetMapping("/profile1")
	public String profile(@AuthenticationPrincipal UserDetails userDetails) {
		
		return userDetails.getUsername();
	}
}