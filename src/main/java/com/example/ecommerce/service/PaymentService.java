package com.example.ecommerce.service;

public interface PaymentService {
    boolean processPayment(Double amount, String paymentToken);
}