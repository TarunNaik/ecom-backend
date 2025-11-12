package com.example.ecommerce.controller;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.User;
import com.example.ecommerce.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    //Admin test endpoint: GET /api/admin/revenue
    @GetMapping("/revenue")
    public ResponseEntity<Double> getTotalRevenue(@RequestHeader("Authorization") String jwtToken) {
        try {
            Double totalRevenue = adminService.getTotalRevenue(jwtToken);
            return ResponseEntity.ok(totalRevenue);
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    //Admin endpoint: GET /api/admin/products
    @GetMapping("/products")
    public ResponseEntity<List<?>> getAllProducts(@RequestHeader("Authorization") String jwtToken) {
        try {
            return ResponseEntity.ok(adminService.getAllProducts(jwtToken));
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    // Admin endpoint: GET /api/admin/users
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(@RequestHeader("Authorization") String authHeader) {
        try {
            return ResponseEntity.ok(adminService.getAllUsers(authHeader));
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    // Admin endpoint: GET /api/admin/orders
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        try {
            return ResponseEntity.ok(adminService.getAllOrders());
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    // Admin endpoint: PUT /api/admin/users/{id}/role
    @PutMapping("/users/{id}/role")
    public ResponseEntity<User> updateUserRole(@PathVariable Long id, @RequestParam String newRole) {
        try {
            User updatedUser = adminService.updateUserRole(id, newRole);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    // Admin endpoint: PUT /api/admin/users/{id}/toggle-status
    @PutMapping("/users/{id}/toggle-status")
    public ResponseEntity<User> toggleUserStatus(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        try {
            User updatedUser = adminService.toggleUserStatus(id, authHeader);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

    }

}