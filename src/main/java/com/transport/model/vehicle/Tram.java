package com.transport.model.vehicle;

public class Tram extends Vehicle {
    public Tram() {
        super("tram", 27.0); // Tramvay temel ücreti 27 TL
    }

    @Override
    public double calculateFare(double distance) {
        return getBaseFare(); // Sadece temel ücret
    }

    @Override
    public int calculateTime(double distance) {
        // Ortalama hız 35 km/saat
        return (int) ((distance / 35.0) * 60);
    }
} 