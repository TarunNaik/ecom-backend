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
    @Autowired
    WishlistRepository wishlistRepository;
    @Autowired
    WishlistItemRepository wishlistItemRepository;

    /**
     * Check if the JWT token belongs to a buyer and return the User
     * @param jwtToken
     * @throws SecurityException
     */
    private User checkBuyerAccess(String jwtToken) throws SecurityException {
        if(jwtToken == null || !jwtToken.startsWith("Bearer ")) {
            throw new SecurityException("Missing or invalid Authorization header.");
        }
        String token = jwtToken.replace("Bearer ", "");
        Claims claims = jwtUtil.extractClaims(token);
        String role = claims.get("role", String.class);
        if (role == null || !role.equalsIgnoreCase("ROLE_BUYER")) {
            throw new SecurityException("Access Denied: Only Buyer can perform this action.");
        }
        String email = claims.getSubject();
        User buyer = userRepository.findByEmail(email);
        if (buyer == null) {
            throw new IllegalArgumentException("Buyer not found.");
        }
        return buyer;
    }

    // Buyer-related business logic would go here
    public List<Product> getAvailableProducts(String jwtToken) {
        checkBuyerAccess(jwtToken);
        return productRepository.findAll(); // Placeholder return
    }

    public List<Order> getBuyerOrders(String jwtToken) throws SecurityException {
        User buyer = checkBuyerAccess(jwtToken);
        return orderRepository.findByBuyerId(buyer.getId());
    }

    public void addItemToCart(Long productId, int quantity, String jwtToken) throws SecurityException {
        User buyer = checkBuyerAccess(jwtToken);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found."));
        Cart cart = cartRepository.findByBuyerId(buyer.getId());
        if (cart == null) {
            cart = Cart.builder().buyer(buyer).build();
            cart = cartRepository.save(cart);
        }
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
        User buyer = checkBuyerAccess(jwtToken);
        Cart cart = cartRepository.findByBuyerId(buyer.getId());
        if (cart == null) {
            throw new IllegalArgumentException("Cart not found.");
        }
        return cartItemRepository.findByCartId(cart.getId());
    }

    public void addItemToWishlist(Long productId, String jwtToken) {
        User buyer = checkBuyerAccess(jwtToken);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found."));
        Wishlist wishlist = wishlistRepository.findByBuyerId(buyer.getId());
        if (wishlist == null) {
            wishlist = Wishlist.builder().buyer(buyer).build();
            wishlist = wishlistRepository.save(wishlist);
        }
        WishlistItem wishlistItem = wishlistItemRepository.findByWishlistIdAndProductId(wishlist.getId(), productId);
        if (wishlistItem == null) {
            wishlistItem = new WishlistItem();
            wishlistItem.setWishlist(wishlist);
            wishlistItem.setProduct(product);
            wishlistItemRepository.save(wishlistItem);
        }
    }

    public List<Product> getWishlist(String jwtToken) throws SecurityException {
        User buyer = checkBuyerAccess(jwtToken);
        Wishlist wishlist = wishlistRepository.findByBuyerId(buyer.getId());
        if (wishlist == null) {
            throw new IllegalArgumentException("Wishlist not found.");
        }
        List<WishlistItem> wishlistItems = wishlistItemRepository.findByWishlistId(wishlist.getId());
        return wishlistItems.stream().map(WishlistItem::getProduct).toList();
    }

    public void removeItemFromWishlist(Long productId, String jwtToken) throws SecurityException {
        User buyer = checkBuyerAccess(jwtToken);
        Wishlist wishlist = wishlistRepository.findByBuyerId(buyer.getId());
        if (wishlist == null) {
            throw new IllegalArgumentException("Wishlist not found.");
        }
        WishlistItem wishlistItem = wishlistItemRepository.findByWishlistIdAndProductId(wishlist.getId(), productId);
        if (wishlistItem != null) {
            wishlistItemRepository.delete(wishlistItem);
        }
    }

    public void removeItemFromCart(Long productId, String jwtToken) throws SecurityException {
        User buyer = checkBuyerAccess(jwtToken);
        Cart cart = cartRepository.findByBuyerId(buyer.getId());
        if (cart == null) {
            throw new IllegalArgumentException("Cart not found.");
        }
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);
        if (cartItem != null) {
            cartItemRepository.delete(cartItem);
        }
    }
}
