package com.transport.model.passenger;

public abstract class Passenger {
    private String type;
    private String paymentMethod;

    public Passenger(String type) {
        this.type = type;
        this.paymentMethod = "Kentkart"; // Varsayılan ödeme yöntemi
    }

    public String getType() {
        return type;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public abstract double calculateDiscount(double fare);
} 