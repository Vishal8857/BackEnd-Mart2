package com.product.Controller;

import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import com.DTO.LoginRequest;
import com.product.CommanClasses.UserResponse;
import com.product.Entity.User;
import com.product.Repository.UserRepo;
import com.product.Service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {
	
	private static final Logger logger=LoggerFactory.getLogger(UserController.class);

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
	
	@DeleteMapping("/deteleUser/{userId}")
	public ResponseEntity<UserResponse> deleteUser(Long userId){
		
		boolean flag=service.deleteUser(userId);
		logger.info("ture");
		
		if(flag) {
		UserResponse response=new UserResponse("User deleted successfully....");
		logger.info("User delted successfully... with Id: "+userId);
		return ResponseEntity.status(HttpStatus.OK).body(response);
		}
		UserResponse response=new UserResponse("User not exist....");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}
}
