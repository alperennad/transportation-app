package com.transport.model.payment;

public class Card extends PaymentMethod {
    @Override
    public boolean processPayment(double amount) {
        return true;
    }
} 