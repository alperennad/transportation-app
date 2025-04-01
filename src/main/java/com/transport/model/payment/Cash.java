package com.transport.model.payment;

public class Cash extends PaymentMethod {
    @Override
    public boolean processPayment(double amount) {
        return true;
    }
} 