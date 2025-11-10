package com.example.ecommerce.service;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    // Helper method to check if the current user is an ADMIN
    private void checkAdminAccess() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }
        User admin = userRepository.findByEmail(email);
        if (admin == null || !admin.getRole().equalsIgnoreCase("ADMIN")) {
            throw new SecurityException("Access Denied: Only administrators can perform this action.");
        }
    }

    public List<User> getAllUsers() {
        checkAdminAccess();
        return userRepository.findAll();
    }

    public List<Order> getAllOrders() {
        checkAdminAccess();
        return orderRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        checkAdminAccess();
        return userRepository.findById(id);
    }
    
    // Simple role update for admin dashboard management
    public User updateUserRole(Long userId, String newRole) {
        checkAdminAccess();
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("User not found.");
        }
        User user = userOpt.get();
        String normalizedRole = newRole.toUpperCase();
        if (normalizedRole.equals("BUYER") || normalizedRole.equals("VENDOR") || normalizedRole.equals("ADMIN")) {
            user.setRole(normalizedRole);
            return userRepository.save(user);
        } else {
            throw new IllegalArgumentException("Invalid role specified.");
        }
    }
}