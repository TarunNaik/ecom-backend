package com.example.ecommerce.controller;

import com.example.ecommerce.exception.CartEmptyException;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.service.BuyerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buyer")
public class BuyerController {

    Logger logger = LoggerFactory.getLogger(BuyerController.class);

    @Autowired
    private BuyerService buyerService;
    // Buyer endpoint: GET /api/buyer/wishlist
    @GetMapping("/wishlist")
    public ResponseEntity<List<Product>> getWishlist(@RequestHeader("Authorization") String jwtToken) {
        try {
            List<Product> wishlist = buyerService.getWishlist(jwtToken);
            return ResponseEntity.ok(wishlist);
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    // Buyer endpoint: GET /api/buyer/cart
    @GetMapping("/cart")
    public ResponseEntity<List<CartItem>> getCartItems(@RequestHeader("Authorization") String jwtToken) {
        try {
            List<CartItem> cartItems = buyerService.getCartItems(jwtToken);
            return ResponseEntity.ok(cartItems);
        } catch (CartEmptyException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
    //Buyer endpoint: Remove from Cart /api/buyer/cart/remove/{productId}
    @DeleteMapping("/cart/remove/{productId}")
    public ResponseEntity<String> removeFromCart(@PathVariable Long productId, @RequestHeader("Authorization") String jwtToken) {
        try {
            buyerService.removeItemFromCart(productId, jwtToken);
            return ResponseEntity.ok("Product removed from cart");
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    // Buyer endpoint: Add Wishlist /api/buyer/wishlist/add/{productId}
    @PostMapping("/wishlist/add/{productId}")
    public ResponseEntity<String> addToWishlist(@PathVariable Long productId, @RequestHeader("Authorization") String jwtToken) {
        try {
            buyerService.addItemToWishlist(productId, jwtToken);
            return ResponseEntity.ok("Product added to wishlist");
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
    // Buyer endpoints: Removefrom Wishlist /api/buyer/wishlist/remove/{productId}
    @DeleteMapping("/wishlist/remove/{productId}")
    public ResponseEntity<String> removeFromWishlist(@PathVariable Long productId, @RequestHeader("Authorization") String jwtToken) {
        try {
            buyerService.removeItemFromWishlist(productId, jwtToken);
            return ResponseEntity.ok("Product removed from wishlist");
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    //Buyer endpoint: Update cart item quantity /api/buyer/cart/update/{productId}/{count}
    @PutMapping("/cart/update/{productId}/{count}")
    public ResponseEntity<String> updateCartItemQuantity(@PathVariable Long productId, @PathVariable int count, @RequestHeader("Authorization") String jwtToken) {
        try {
            buyerService.updateCartItemQuantity(productId, count, jwtToken);
            return ResponseEntity.ok("Cart item quantity updated");
        } catch (CartEmptyException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
    // Additional buyer-specific endpoints can be added here - clear wishlist
    @DeleteMapping("/wishlist/clear")
    public ResponseEntity<String> clearWishlist(@RequestHeader("Authorization") String jwtToken) {
        try {
            buyerService.clearWishlist(jwtToken);
            return ResponseEntity.ok("Wishlist cleared");
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
    // Additional buyer-specific endpoints can be added here - clear cart
    @DeleteMapping("/cart/clear")
    public ResponseEntity<String> clearCart(@RequestHeader("Authorization") String jwtToken) {
        try {
            buyerService.clearCart(jwtToken);
            return ResponseEntity.ok("Cart cleared");
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
