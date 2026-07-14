package com.product.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
	public String profile() {

		return "Protected API Accessed";
	}
}