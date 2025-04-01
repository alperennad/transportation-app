package com.transport.model.passenger;

public class StudentPassenger extends Passenger {
    public StudentPassenger() {
        super("student");
    }

    @Override
    public double calculateDiscount(double fare) {
        // Eğer ödeme yöntemi Kentkart değilse tam ücret al
        if (!getPaymentMethod().equals("Kentkart")) {
            return fare;
        }
        return 13.0; // Öğrenci Kentkart ücreti
    }
} 