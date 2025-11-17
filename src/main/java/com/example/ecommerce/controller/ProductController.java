package com.example.ecommerce.controller;

import com.example.ecommerce.exception.ProductNotFoundException;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.service.ProductService;
import com.example.ecommerce.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/products")
@RestController
public class ProductController {



    @Autowired
    private ProductService productService;
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/api/auth/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Controller is working");
    }

    // Public endpoint: GET /api/products
    @GetMapping("/list")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    // Public endpoint: GET /api/products/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Vendor endpoint: POST /api/vendors/products
    @PostMapping("/add")
    public ResponseEntity<Product> createProduct(@RequestBody Product product, @RequestHeader("Authorization") String jwtToken) {
        try {
            Product createdProduct = productService.createProduct(product, jwtToken);
            return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    // Vendor endpoint: PUT /api/vendors/products/{id}
    @PutMapping("/update/{id}")
    public ResponseEntity<Product> updateProduct(@RequestHeader("Authorization") String jwtToken,  @PathVariable Long id, @RequestBody Product product) {
        try {
            Product updatedProduct = productService.updateProduct(jwtToken, id, product);
            return ResponseEntity.ok(updatedProduct);
        } catch (ProductNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    // Vendor endpoint: DELETE /api/vendors/products/{id}
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProduct(@RequestHeader("Authorization") String jwtToken, @PathVariable Long id) {
        try {
            productService.deleteProduct(id, jwtToken);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}