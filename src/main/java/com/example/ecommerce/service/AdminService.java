package com.example.ecommerce.service;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderStatus;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
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

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ProductService productService;
    // Helper method to check if the current user is an ADMIN
    private void checkAdminAccess(String jwtToken) throws SecurityException{
        if (jwtToken == null || !jwtToken.startsWith("Bearer ")) {
            throw new SecurityException("Missing or invalid Authorization header.");
        }
        String token = jwtToken.replace("Bearer ", "");
        Claims claims = jwtUtil.extractClaims(token);
        String role = claims.get("role", String.class);
        if (role == null || !role.equalsIgnoreCase("ROLE_ADMIN")) {
            throw new SecurityException("Access Denied: Only administrators can perform this action.");
        }
    }

    public List<User> getAllUsers(String jwtToken) {
        checkAdminAccess(jwtToken);
        return userRepository.findAll();
    }

    public List<Order> getAllOrders() {
        checkAdminAccess(null);
        return orderRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        checkAdminAccess(null);
        return userRepository.findById(id);
    }
    
    // Simple role update for admin dashboard management
    public User updateUserRole(Long userId, String newRole) {
        checkAdminAccess(null);
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

    public User toggleUserStatus(Long userId, String jwtToken) throws SecurityException{
        checkAdminAccess(jwtToken);
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("User not found.");
        }
        User user = userOpt.get();
        user.setIsActive(!user.getIsActive());
        return userRepository.save(user);
    }

    public List<Product> getAllProducts(String jwtToken) throws SecurityException{
        checkAdminAccess(jwtToken);

        // This method would typically call a ProductRepository to fetch products.
        // For brevity, returning null or an empty list here.
        return productService.getAllProducts(); // Replace with actual product fetching logic
    }

    public Double getTotalRevenue(String jwtToken) {
        checkAdminAccess(jwtToken);
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .filter(order -> order.getOrderStatus().equals(OrderStatus.DELIVERED))
                .mapToDouble(Order::getTotalAmount)
                .sum();
    }
}