package com.transport.model.vehicle;

public class Taxi extends Vehicle {
    private static final double OPENING_FEE = 25.0; // Açılış ücreti
    private static final double COST_PER_KM = 35.0; // Kilometre başına ücret

    public Taxi() {
        super("taxi", OPENING_FEE);
    }

    @Override
    public double calculateFare(double distance) {
        return getBaseFare() + (distance * COST_PER_KM);
    }

    @Override
    public int calculateTime(double distance) {
        // Ortalama hız 50 km/saat
        return (int) ((distance / 50.0) * 60);
    }
} 