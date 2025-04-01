package com.transport.model.payment;

public class KentCard extends PaymentMethod {
    @Override
    public boolean processPayment(double amount) {
        return true;
    }
} 