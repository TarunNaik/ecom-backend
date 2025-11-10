package com.example.ecommerce.payload;

import java.util.List;

public class CheckoutRequest {
    private List<CartItemRequest> items;
    private String paymentToken;

    // Getters and setters

    public List<CartItemRequest> getItems() {
        return items;
    }

    public void setItems(List<CartItemRequest> items) {
        this.items = items;
    }

    public String getPaymentToken() {
        return paymentToken;
    }

    public void setPaymentToken(String paymentToken) {
        this.paymentToken = paymentToken;
    }
}