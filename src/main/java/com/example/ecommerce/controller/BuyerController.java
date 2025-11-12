package com.example.ecommerce.controller;

import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.service.BuyerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buyer")
public class BuyerController {

    @Autowired
    private BuyerService buyerService;
    // Buyer endpoint: GET /api/buyer/cart
    @GetMapping("/cart")
    public ResponseEntity<List<CartItem>> getCartItems(@RequestHeader("Authorization") String jwtToken) {
        try {
            List<CartItem> cartItems = buyerService.getCartItems(jwtToken);
            return ResponseEntity.ok(cartItems);
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAvailableProducts(@RequestHeader("Authorization") String jwtToken) {
        try {
            List<Product> products = buyerService.getAvailableProducts(jwtToken);
            return ResponseEntity.ok(products);
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
    // Buyer endpoint: GET /api/buyer/orders
    @GetMapping("/orders")
    public ResponseEntity<List<?>> getBuyerOrders(@RequestHeader("Authorization") String jwtToken) {
        try {
            List<?> orders = buyerService.getBuyerOrders(jwtToken);
            return ResponseEntity.ok(orders);
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
    // Additional buyer-specific endpoints can be added here add to Cart
    @PostMapping("/cart/add/{productId}/{count}" )
    public ResponseEntity<String> addToCart(@PathVariable Long productId, @PathVariable int count, @RequestHeader("Authorization") String jwtToken) {
        try {
            buyerService.addItemToCart(productId, count, jwtToken);
            return ResponseEntity.ok("Product added to cart");
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
