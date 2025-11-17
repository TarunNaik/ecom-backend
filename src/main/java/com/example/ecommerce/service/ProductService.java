package com.example.ecommerce.service;

import com.example.ecommerce.exception.ProductNotFoundException;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;


    /**
     * Create a new product (only for VENDOR role)
     * @param product Product to create
     * @param jwtToken authorization token
     * @return Product
     * @throws SecurityException if the user is not a VENDOR
     */
    public Product createProduct(Product product, String jwtToken) throws SecurityException {
        User vendor = checkVendorAccess(jwtToken);

        product.setVendor(vendor);
        return productRepository.save(product);
    }

    // Get all products (public access)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Get product by ID (public access)
     * @param id product ID
     * @return Optional<Product>
     */
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    /**
     * Update a product (only for the owning VENDOR)
     * @param jwtToken authorization token
     * @param id Product ID
     * @param updatedProduct Product with updated details
     * @return updated Product
     * @throws ProductNotFoundException if product not found
     * @throws SecurityException if the user is not authorized to update the product
     */
    public Product updateProduct(String jwtToken, Long id, Product updatedProduct)throws ProductNotFoundException, SecurityException {
        User vendor = checkVendorAccess(jwtToken);
        Optional<Product> existingProductOpt = productRepository.findById(id);
        if (existingProductOpt.isEmpty()) {
            throw new ProductNotFoundException("Product not found.");
        }
        Product existingProduct = existingProductOpt.get();
        if (vendor == null || !existingProduct.getVendor().getId().equals(vendor.getId())) {
            throw new SecurityException("You are not authorized to update this product.");
        }

        updateProduct(updatedProduct, existingProduct);
        // Note: Vendor cannot be changed

        return productRepository.save(existingProduct);
    }

    /**
     * Delete a product (only for the owning VENDOR)
     * @param id Product ID
     * @param jwtToken authorization token
     * @throws SecurityException if the user is not authorized to delete the product
     */
    public void deleteProduct(Long id, String jwtToken) throws SecurityException {
        User vendor = checkVendorAccess(jwtToken);
        Optional<Product> existingProductOpt = productRepository.findById(id);
        if (existingProductOpt.isEmpty()) {
            throw new IllegalArgumentException("Product not found.");
        }

        Product existingProduct = existingProductOpt.get();


        if (vendor == null || !existingProduct.getVendor().getId().equals(vendor.getId())) {
            throw new SecurityException("You are not authorized to delete this product.");
        }

        productRepository.deleteById(id);
    }

    /**
     * @param jwtToken authorization token
     * @return User with VENDOR role
     * Helper method to check if the user has VENDOR role
     */
    private User checkVendorAccess(String jwtToken) throws SecurityException {
        if(jwtToken == null || !jwtToken.startsWith("Bearer ")) {
            throw new SecurityException("Missing or invalid Authorization header.");
        }
        String token = jwtToken.replace("Bearer ", "");
        Claims claims = jwtUtil.extractClaims(token);
        String role = claims.get("role", String.class);
        if (role == null || !role.equalsIgnoreCase("ROLE_VENDOR")) {
            throw new SecurityException("Access Denied: Only Vendor can perform this action.");
        }
        String email = claims.getSubject();
        User vendor = userRepository.findByEmail(email);
        if (vendor == null) {
            logger.info("Looks like Buyer not not on boarded yet. Check with Admin.");
        }
        return vendor;
    }

    /**
     * Helper method to update existing product with non-null fields from updated product
     * @param updated product with updated details
     * @param existing product to be updated
     */
    private void updateProduct(Product updated, Product existing) {
        Optional.ofNullable(updated.getCategory())
                .filter(s -> !s.isEmpty())
                .ifPresent(existing::setCategory);

        Optional.ofNullable(updated.getName())
                .filter(s -> !s.isEmpty())
                .ifPresent(existing::setName);

        Optional.ofNullable(updated.getDescription())
                .filter(s -> !s.isEmpty())
                .ifPresent(existing::setDescription);

        Optional.ofNullable(updated.getPrice())
                .ifPresent(existing::setPrice);

        Optional.ofNullable(updated.getStock())
                .ifPresent(existing::setStock);
    }

}