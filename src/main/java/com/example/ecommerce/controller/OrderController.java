package com.example.ecommerce.controller;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.payload.CheckoutRequest;
import com.example.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // Buyer endpoint: POST /api/orders/checkout
    @PostMapping("/checkout")
    public ResponseEntity<Order> placeOrder(@RequestBody CheckoutRequest checkoutRequest) {
        try {
            Order order = orderService.placeOrder(checkoutRequest);
            return new ResponseEntity<>(order, HttpStatus.CREATED);
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Buyer endpoint: GET /api/orders/history
    @GetMapping("/history")
    public ResponseEntity<List<Order>> getOrderHistory() {
        try {
            List<Order> orders = orderService.getOrdersByBuyer();
            return ResponseEntity.ok(orders);
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}