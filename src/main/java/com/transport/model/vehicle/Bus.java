package com.transport.model.vehicle;

public class Bus extends Vehicle {
    public Bus() {
        super("bus", 27.0); // Otobüs temel ücreti 27 TL
    }

    @Override
    public double calculateFare(double distance) {
        return getBaseFare(); // Sadece temel ücret
    }

    @Override
    public int calculateTime(double distance) {
        // Ortalama hız 40 km/saat
        return (int) ((distance / 40.0) * 60);
    }
} 