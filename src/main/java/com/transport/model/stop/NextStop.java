package com.transport.model.stop;

public class NextStop {
    private final String stopId;
    private final double distance;
    private final int duration;
    private final double fare;

    public NextStop(String stopId, double distance, int duration, double fare) {
        this.stopId = stopId;
        this.distance = distance;
        this.duration = duration;
        this.fare = fare;
    }

    public String getStopId() {
        return stopId;
    }

    public double getDistance() {
        return distance;
    }

    public int getDuration() {
        return duration;
    }

    public double getFare() {
        return fare;
    }
} 