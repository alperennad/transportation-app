package com.transport.model.payment;

public abstract class PaymentMethod {
    public abstract boolean processPayment(double amount);
} 