package com.transport.model.route;

import com.transport.model.stop.Stop;
import com.transport.model.vehicle.Vehicle;

public class RouteSegment {
    private final Stop from;
    private final Stop to;
    private final Vehicle vehicle;
    private final double distance;
    private final double duration;

    public RouteSegment(Stop from, Stop to, Vehicle vehicle, double distance, double duration) {
        this.from = from;
        this.to = to;
        this.vehicle = vehicle;
        this.distance = distance;
        this.duration = duration;
    }

    public Stop getFrom() {
        return from;
    }

    public Stop getTo() {
        return to;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public double getDistance() {
        return distance;
    }

    public double getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        String vehicleEmoji;
        if (vehicle == null) {
            vehicleEmoji = "🚶";
        } else {
            vehicleEmoji = switch (vehicle.getClass().getSimpleName().toLowerCase()) {
                case "bus" -> "🚌";
                case "tram" -> "🚊";
                case "taxi" -> "🚕";
                default -> "🚗";
            };
        }

        String fromName = from != null ? from.getName() : "Verilen Konum";
        String toName = to != null ? to.getName() : "Hedef Konum";

        return String.format("%s %s -> %s (%.2f km, %.0f dk)", 
            vehicleEmoji, fromName, toName, distance, duration);
    }
} 