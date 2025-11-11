package com.example.ecommerce.service;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    // Helper method to get the currently authenticated user
    private User getCurrentUser(String email) {
        if(StringUtils.hasText(email)) {
            return userRepository.findByEmail(email);
        }
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }
        return userRepository.findByEmail(email);
    }

    // Create a new product (only for VENDOR)
    public Product createProduct(Product product, String email) {
        User vendor = getCurrentUser(email);
        if (vendor == null || !vendor.getRole().equalsIgnoreCase("VENDOR")) {
            throw new SecurityException("Only vendors can create products.");
        }
        product.setVendor(vendor);
        return productRepository.save(product);
    }

    // Get all products (public access)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Get a single product by ID (public access)
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // Update an existing product (only for the owning VENDOR)
    public Product updateProduct(Long id, Product updatedProduct) {
        Optional<Product> existingProductOpt = productRepository.findById(id);
        if (!existingProductOpt.isPresent()) {
            throw new IllegalArgumentException("Product not found.");
        }

        Product existingProduct = existingProductOpt.get();
        User vendor = getCurrentUser(null);

        if (vendor == null || !existingProduct.getVendor().getId().equals(vendor.getId())) {
            throw new SecurityException("You are not authorized to update this product.");
        }

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        // Note: Vendor cannot be changed

        return productRepository.save(existingProduct);
    }

    // Delete a product (only for the owning VENDOR)
    public void deleteProduct(Long id) {
        Optional<Product> existingProductOpt = productRepository.findById(id);
        if (!existingProductOpt.isPresent()) {
            throw new IllegalArgumentException("Product not found.");
        }

        Product existingProduct = existingProductOpt.get();
        User vendor = getCurrentUser(null);

        if (vendor == null || !existingProduct.getVendor().getId().equals(vendor.getId())) {
            throw new SecurityException("You are not authorized to delete this product.");
        }

        productRepository.deleteById(id);
    }
}