package com.transport.model.vehicle;

public abstract class Vehicle {
    private final String type;
    private final double baseFare;

    public Vehicle(String type, double baseFare) {
        this.type = type;
        this.baseFare = baseFare;
    }

    public String getType() {
        return type;
    }

    public double getBaseFare() {
        return baseFare;
    }

    public abstract double calculateFare(double distance);
    public abstract int calculateTime(double distance); // Returns time in minutes
} 