package com.example.ecommerce.service;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.User;
import com.example.ecommerce.payload.CartItemRequest;
import com.example.ecommerce.payload.CheckoutRequest;
import com.example.ecommerce.repository.OrderItemRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentService paymentService; // Inject PaymentService

    // Helper method to get the currently authenticated user
    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }
        return userRepository.findByEmail(email);
    }

    @Transactional
    public Order placeOrder(CheckoutRequest checkoutRequest) {
        User buyer = getCurrentUser();
        if (buyer == null || !buyer.getRole().equalsIgnoreCase("BUYER")) {
            throw new SecurityException("Only authenticated buyers can place orders.");
        }

        // 1. Validate items and calculate total
        List<OrderItem> orderItems = new ArrayList<>();
        double totalAmount = 0.0;

        for (CartItemRequest itemRequest : checkoutRequest.getItems()) {
            Optional<Product> productOpt = productRepository.findById(itemRequest.getProductId());
            if (!productOpt.isPresent()) {
                throw new IllegalArgumentException("Product not found with ID: " + itemRequest.getProductId());
            }

            Product product = productOpt.get();
            if (itemRequest.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be positive for product ID: " + product.getId());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItems.add(orderItem);

            totalAmount += product.getPrice() * itemRequest.getQuantity();
        }

        // 2. Process Payment (Mock implementation)
        // 2. Process Payment (Mock implementation)
        boolean paymentSuccess = paymentService.processPayment(totalAmount, checkoutRequest.getPaymentToken());
        
        if (!paymentSuccess) {
            throw new IllegalStateException("Payment failed. Order not placed.");
        }

        // 3. Create and save the Order
        Order order = new Order();
        order.setBuyer(buyer);
        order.setTotalAmount(totalAmount);
        order = orderRepository.save(order);

        // 4. Link OrderItems to the Order and save them
        for (OrderItem item : orderItems) {
            item.setOrder(order);
        }
        order.setItems(orderItems);

        orderItemRepository.saveAll(orderItems);
        return orderRepository.save(order);
    }

    public List<Order> getOrdersByBuyer() {
        User buyer = getCurrentUser();
        if (buyer == null) {
            throw new SecurityException("User must be authenticated.");
        }
        // Fetch orders directly by buyer ID for efficiency
        return orderRepository.findByBuyerId(buyer.getId());
    }
}