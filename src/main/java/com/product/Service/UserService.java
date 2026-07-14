package com.product.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.DTO.LoginRequest;
import com.product.Entity.User;
import com.product.Repository.UserRepo;
import com.product.Security.JwtUtil;

@Service
public class UserService {

    @Autowired
    private UserRepo repo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authManager;

    // Register
    public String register(User user) {

        // Default Role
        if (user.getRole() == null) {
            user.setRole("USER");
        }

        user.setPassword(
                encoder.encode(user.getPassword()));

        repo.save(user);

        return "User Registered Successfully";
    }

    // Login
    public String login(LoginRequest request) {

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getMail(),
                        request.getPassword()));

        // Get user from DB
        User user = repo.findByMail(request.getMail())
                .orElseThrow(() ->
                        new RuntimeException("User Not Found"));

        // Generate JWT with Role
        return jwtUtil.generateToken(
                user.getMail(),
                user.getRole());
    }
}