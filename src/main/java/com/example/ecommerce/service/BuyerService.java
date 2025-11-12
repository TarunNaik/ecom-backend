package com.example.ecommerce.service;

import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.*;
import com.example.ecommerce.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class BuyerService {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    CartItemRepository cartItemRepository;

    // Buyer-related business logic would go here
    public List<Product> getAvailableProducts(String jwtToken) {
        // Implementation to fetch available products for buyers
        checkBuyerAccess(jwtToken);
        return productRepository.findAll(); // Placeholder return
    }

    private void checkBuyerAccess(String jwtToken) throws SecurityException {
        // Implementation to check if the JWT token belongs to a buyer
        if(jwtToken == null || !jwtToken.startsWith("Bearer ")) {
            throw new SecurityException("Missing or invalid Authorization header.");
        }
        String token = jwtToken.replace("Bearer ", "");
        // Extract claims from the token and verify the role
        // If the role is not "ROLE_BUYER", throw SecurityException
        Claims claims = jwtUtil.extractClaims(token);
        String role = claims.get("role", String.class);
        // Throw SecurityException if access is denied
        if (role == null || !role.equalsIgnoreCase("ROLE_BUYER")) {
            throw new SecurityException("Access Denied: Only Buyer can perform this action.");
        }


    }

    public List<Order> getBuyerOrders(String jwtToken) throws SecurityException {
        checkBuyerAccess(jwtToken);
        String token = jwtToken.replace("Bearer ", "");
        Claims claims = jwtUtil.extractClaims(token);
        String email = claims.getSubject();
        User buyer = userRepository.findByEmail(email);
        if (buyer == null) {
            throw new IllegalArgumentException("Buyer not found.");
        }
        return orderRepository.findByBuyerId(buyer.getId());
    }

    public void addItemToCart(Long productId, int quantity, String jwtToken) throws SecurityException {
        checkBuyerAccess(jwtToken);
        // Implementation to add a product to the buyer's cart
        // This is a placeholder implementation
        String token = jwtToken.replace("Bearer ", "");
        Claims claims = jwtUtil.extractClaims(token);
        String email = claims.getSubject();
        User buyer = userRepository.findByEmail(email);
        if (buyer == null) {
            throw new IllegalArgumentException("Buyer not found.");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found."));
        // Logic to add the product to the buyer's cart would go here
        // 2. Find or create cart
        Cart cart = cartRepository.findByBuyerId(buyer.getId());
        if (cart == null) {
            cart = Cart.builder().buyer(buyer).build();
            cart = cartRepository.save(cart);
        }

        // 3. Check if product is already in cart
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);
        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
        }
        cartItemRepository.save(cartItem);
    }

    public List<CartItem> getCartItems(String jwtToken) throws SecurityException {
        checkBuyerAccess(jwtToken);
        String token = jwtToken.replace("Bearer ", "");
        Claims claims = jwtUtil.extractClaims(token);
        String email = claims.getSubject();
        User buyer = userRepository.findByEmail(email);
        if (buyer == null) {
            throw new IllegalArgumentException("Buyer not found.");
        }
        Cart cart = cartRepository.findByBuyerId(buyer.getId());
        if (cart == null) {
            throw new IllegalArgumentException("Cart not found.");
        }
        return cartItemRepository.findByCartId(cart.getId());
    }
}
