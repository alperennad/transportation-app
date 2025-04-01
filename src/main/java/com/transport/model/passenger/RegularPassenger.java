package com.transport.model.passenger;

public class RegularPassenger extends Passenger {
    public RegularPassenger() {
        super("regular");
    }

    @Override
    public double calculateDiscount(double fare) {
        // Eğer ödeme yöntemi Kentkart değilse tam ücret al
        if (!getPaymentMethod().equals("Kentkart")) {
            return fare;
        }
        return fare; // Normal yolcu için Kentkart ile de tam ücret
    }
} 