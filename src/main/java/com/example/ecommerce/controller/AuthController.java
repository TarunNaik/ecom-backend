package com.example.ecommerce.controller;

import com.example.ecommerce.model.User;
import com.example.ecommerce.payload.LoginRequest;
import com.example.ecommerce.payload.RegisterRequest;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()) != null) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        // Create new user's account
        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        
        // Default role to BUYER if not specified or invalid
        String role = registerRequest.getRole() != null ? registerRequest.getRole().toUpperCase() : "BUYER";
        if (!role.equals("BUYER") && !role.equals("VENDOR") && !role.equals("ADMIN")) {
            role = "BUYER";
        }
        user.setRole(role);

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully! Role: " + user.getRole());
    }

    @PostMapping("/login")
    public ResponseEntity<String> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
            List<String> roles = new ArrayList<>();
            userDetails.getAuthorities().forEach(authority -> {
                roles.add(authority.getAuthority());
            });
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // In a real application, you would generate a JWT here.
            return new ResponseEntity<>(String.join(", ", roles), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: Invalid email or password", HttpStatus.UNAUTHORIZED);
        }
    }
}