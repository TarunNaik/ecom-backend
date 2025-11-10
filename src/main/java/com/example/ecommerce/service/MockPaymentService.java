package com.example.ecommerce.service;

import org.springframework.stereotype.Service;

@Service
public class MockPaymentService implements PaymentService {

    @Override
    public boolean processPayment(Double amount, String paymentToken) {
        System.out.println("--- MOCK PAYMENT GATEWAY ---");
        System.out.println("Processing payment of $" + String.format("%.2f", amount));
        System.out.println("Using token: " + paymentToken);
        
        // Simulate successful payment for any non-empty token
        if (paymentToken != null && !paymentToken.isEmpty()) {
            System.out.println("Payment successful.");
            return true;
        } else {
            System.out.println("Payment failed: Invalid token.");
            return false;
        }
    }
}