package com.transport.model.passenger;

public class ElderlyPassenger extends Passenger {
    public ElderlyPassenger() {
        super("elderly");
    }

    @Override
    public double calculateDiscount(double fare) {
        // Eğer ödeme yöntemi Kentkart değilse tam ücret al
        if (!getPaymentMethod().equals("Kentkart")) {
            return fare;
        }
        return 0.0; // Yaşlı yolcu Kentkart ücreti (ücretsiz)
    }
} 