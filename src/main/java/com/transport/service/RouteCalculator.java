package com.transport.service;

import com.transport.model.location.Location;
import com.transport.model.passenger.Passenger;
import com.transport.model.route.Route;
import com.transport.model.route.RouteSegment;
import com.transport.model.stop.Stop;
import com.transport.model.vehicle.Bus;
import com.transport.model.vehicle.Taxi;
import com.transport.model.vehicle.Tram;
import com.transport.model.vehicle.Vehicle;

import java.util.*;
import java.util.stream.Collectors;

public class RouteCalculator {
    private static final double WALKING_THRESHOLD = 3.0; // km
    private final List<Stop> stops;
    private final Map<String, Stop> stopsMap;
    private static final double WALKING_SPEED = 5.0; // km/h
    private static final double BUS_SPEED = 35.0; // km/h
    private static final double TRAM_SPEED = 30.0; // km/h
    private static final double TAXI_SPEED = 50.0; // km/h

    public RouteCalculator(List<Stop> stops) {
        this.stops = stops;
        this.stopsMap = new HashMap<>();
        for (Stop stop : stops) {
            stopsMap.put(stop.getId(), stop);
        }
    }

    public Route calculateRoute(Location start, Location end, Passenger passenger) {
        Route route = new Route(passenger);
        
        // En yakın durakları bul
        Stop nearestStartStop = findNearestStop(start);
        Stop nearestEndStop = findNearestStop(end);
        
        if (nearestStartStop == null || nearestEndStop == null) {
            // Yakında durak yoksa direkt taksi kullan
            double directDistance = calculateDistance(start, end);
            Taxi taxi = new Taxi();
            double duration = (directDistance / TAXI_SPEED) * 60;
            route.addSegment(null, null, taxi, directDistance, duration);
            return route;
        }

        // Başlangıç noktasından ilk durağa yürüme veya taksi
        double startDistance = calculateDistance(start, nearestStartStop.getLocation());
        if (startDistance > WALKING_THRESHOLD) {
            // Taksi kullan
            Taxi taxi = new Taxi();
            double duration = (startDistance / TAXI_SPEED) * 60; // Dakikaya çevir
            route.addSegment(null, nearestStartStop, taxi, startDistance, duration);
        } else {
            // Yürü
            double duration = (startDistance / WALKING_SPEED) * 60; // Dakikaya çevir
            route.addSegment(null, nearestStartStop, null, startDistance, duration);
        }

        // Duraklar arası rota hesapla
        List<Stop> path = findPath(nearestStartStop, nearestEndStop);
        if (path == null || path.size() < 2) {
            // Rota bulunamadıysa baştan sona taksi kullan
            double directDistance = calculateDistance(start, end);
            Taxi taxi = new Taxi();
            double duration = (directDistance / TAXI_SPEED) * 60;
            route = new Route(passenger); // Yeni rota oluştur
            route.addSegment(null, null, taxi, directDistance, duration);
            return route;
        }

        // Duraklar arası seyahat
        for (int i = 0; i < path.size() - 1; i++) {
            Stop currentStop = path.get(i);
            Stop nextStop = path.get(i + 1);
            
            // Araç tipini belirle
            Vehicle vehicle = determineVehicle(currentStop, nextStop);
            
            // Mesafe ve süre hesapla
            double distance = calculateDistance(currentStop.getLocation(), nextStop.getLocation());
            double duration = calculateDuration(vehicle, distance);
            
            route.addSegment(currentStop, nextStop, vehicle, distance, duration);
        }

        // Son duraktan varış noktasına yürüme veya taksi
        double endDistance = calculateDistance(nearestEndStop.getLocation(), end);
        if (endDistance > WALKING_THRESHOLD) {
            // Taksi kullan
            Taxi taxi = new Taxi();
            double duration = (endDistance / TAXI_SPEED) * 60; // Dakikaya çevir
            route.addSegment(nearestEndStop, null, taxi, endDistance, duration);
        } else {
            // Yürü
            double duration = (endDistance / WALKING_SPEED) * 60; // Dakikaya çevir
            route.addSegment(nearestEndStop, null, null, endDistance, duration);
        }

        return route;
    }

    public List<Route> calculateAlternativeRoutes(Location start, Location end, Passenger passenger) {
        List<Route> alternativeRoutes = new ArrayList<>();
        
        // 1. Sadece Taksi Rota
        Route taxiRoute = calculateTaxiOnlyRoute(start, end, passenger);
        if (taxiRoute != null) {
            alternativeRoutes.add(taxiRoute);
        }

        // 2. Sadece Otobüs Rota
        Route busRoute = calculateBusOnlyRoute(start, end, passenger);
        if (busRoute != null) {
            alternativeRoutes.add(busRoute);
        }

        // 3. Tramvay Öncelikli Rota
        Route tramRoute = calculateTramPriorityRoute(start, end, passenger);
        if (tramRoute != null) {
            alternativeRoutes.add(tramRoute);
        }

        // 4. En Az Aktarmalı Rota
        Route minTransferRoute = calculateMinTransferRoute(start, end, passenger);
        if (minTransferRoute != null) {
            alternativeRoutes.add(minTransferRoute);
        }

        return alternativeRoutes;
    }

    private Route calculateTaxiOnlyRoute(Location start, Location end, Passenger passenger) {
        Route route = new Route(passenger);
        double distance = calculateDistance(start, end);
        double duration = (distance / TAXI_SPEED) * 60;
        route.addSegment(null, null, new Taxi(), distance, duration);
        return route;
    }

    private Route calculateBusOnlyRoute(Location start, Location end, Passenger passenger) {
        Stop startStop = findNearestStop(start);
        Stop endStop = findNearestStop(end);
        
        if (startStop == null || endStop == null) return null;

        Route route = new Route(passenger);
        
        // Başlangıç noktasından ilk durağa yürüme
        double walkDistance = calculateDistance(start, startStop.getLocation());
        double walkDuration = (walkDistance / WALKING_SPEED) * 60;
        route.addSegment(null, startStop, null, walkDistance, walkDuration);

        // Otobüs ile seyahat
        double busDistance = calculateDistance(startStop.getLocation(), endStop.getLocation());
        double busDuration = (busDistance / BUS_SPEED) * 60;
        route.addSegment(startStop, endStop, new Bus(), busDistance, busDuration);

        // Son duraktan varış noktasına yürüme
        walkDistance = calculateDistance(endStop.getLocation(), end);
        walkDuration = (walkDistance / WALKING_SPEED) * 60;
        route.addSegment(endStop, null, null, walkDistance, walkDuration);

        return route;
    }

    private Route calculateTramPriorityRoute(Location start, Location end, Passenger passenger) {
        Stop startStop = findNearestStop(start);
        Stop endStop = findNearestStop(end);
        
        if (startStop == null || endStop == null) return null;

        Route route = new Route(passenger);
        
        // Başlangıç noktasından ilk durağa yürüme
        double walkDistance = calculateDistance(start, startStop.getLocation());
        double walkDuration = (walkDistance / WALKING_SPEED) * 60;
        route.addSegment(null, startStop, null, walkDistance, walkDuration);

        // Tramvay ile seyahat
        double tramDistance = calculateDistance(startStop.getLocation(), endStop.getLocation());
        double tramDuration = (tramDistance / TRAM_SPEED) * 60;
        route.addSegment(startStop, endStop, new Tram(), tramDistance, tramDuration);

        // Son duraktan varış noktasına yürüme
        walkDistance = calculateDistance(endStop.getLocation(), end);
        walkDuration = (walkDistance / WALKING_SPEED) * 60;
        route.addSegment(endStop, null, null, walkDistance, walkDuration);

        return route;
    }

    private Route calculateMinTransferRoute(Location start, Location end, Passenger passenger) {
        Stop startStop = findNearestStop(start);
        Stop endStop = findNearestStop(end);
        
        if (startStop == null || endStop == null) return null;

        Route route = new Route(passenger);
        
        // Başlangıç noktasından ilk durağa yürüme
        double walkDistance = calculateDistance(start, startStop.getLocation());
        double walkDuration = (walkDistance / WALKING_SPEED) * 60;
        route.addSegment(null, startStop, null, walkDistance, walkDuration);

        // En az aktarmalı rotayı bul
        List<Stop> path = findPath(startStop, endStop);
        if (path != null && path.size() >= 2) {
            // Duraklar arası seyahat
            for (int i = 0; i < path.size() - 1; i++) {
                Stop currentStop = path.get(i);
                Stop nextStop = path.get(i + 1);
                
                // Araç tipini belirle
                Vehicle vehicle = determineVehicle(currentStop, nextStop);
                
                // Mesafe ve süre hesapla
                double distance = calculateDistance(currentStop.getLocation(), nextStop.getLocation());
                double duration = calculateDuration(vehicle, distance);
                
                route.addSegment(currentStop, nextStop, vehicle, distance, duration);
            }
        }

        // Son duraktan varış noktasına yürüme
        walkDistance = calculateDistance(endStop.getLocation(), end);
        walkDuration = (walkDistance / WALKING_SPEED) * 60;
        route.addSegment(endStop, null, null, walkDistance, walkDuration);

        return route;
    }

    private Stop findNearestStop(Location location) {
        Stop nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Stop stop : stops) {
            double distance = calculateDistance(location, stop.getLocation());
            if (distance < minDistance) {
                minDistance = distance;
                nearest = stop;
            }
        }

        return nearest;
    }

    private List<Stop> findPath(Stop start, Stop end) {
        // Dijkstra algoritması ile en kısa yolu bul
        Map<String, Double> distances = new HashMap<>();
        Map<String, Stop> previous = new HashMap<>();
        PriorityQueue<Stop> queue = new PriorityQueue<>((a, b) -> 
            Double.compare(distances.getOrDefault(a.getId(), Double.MAX_VALUE),
                         distances.getOrDefault(b.getId(), Double.MAX_VALUE)));
        Set<String> visited = new HashSet<>();
        
        // Başlangıç değerlerini ayarla
        for (Stop stop : stops) {
            distances.put(stop.getId(), Double.MAX_VALUE);
        }
        distances.put(start.getId(), 0.0);
        queue.add(start);
        
        // Dijkstra algoritması
        while (!queue.isEmpty()) {
            Stop current = queue.poll();
            
            if (current.equals(end)) {
                break;
            }
            
            if (visited.contains(current.getId())) {
                continue;
            }
            
            visited.add(current.getId());
            
            // Komşu durakları kontrol et
            for (Map.Entry<String, Stop.StopConnection> entry : current.getNextStops().entrySet()) {
                Stop neighbor = stopsMap.get(entry.getKey());
                if (neighbor == null || visited.contains(neighbor.getId())) continue;
                
                double distance = calculateDistance(current.getLocation(), neighbor.getLocation());
                double newDist = distances.get(current.getId()) + distance;
                
                if (newDist < distances.getOrDefault(neighbor.getId(), Double.MAX_VALUE)) {
                    distances.put(neighbor.getId(), newDist);
                    previous.put(neighbor.getId(), current);
                    queue.add(neighbor);
                }
            }
            
            // Transfer noktalarını kontrol et
            if (current.getTransfer() != null) {
                Stop transferStop = stopsMap.get(current.getTransfer().getTransferStopId());
                if (transferStop != null && !visited.contains(transferStop.getId())) {
                    double distance = calculateDistance(current.getLocation(), transferStop.getLocation());
                    double newDist = distances.get(current.getId()) + distance;
                    
                    if (newDist < distances.getOrDefault(transferStop.getId(), Double.MAX_VALUE)) {
                        distances.put(transferStop.getId(), newDist);
                        previous.put(transferStop.getId(), current);
                        queue.add(transferStop);
                    }
                }
            }
        }
        
        // Yolu oluştur
        if (!previous.containsKey(end.getId())) {
            return null; // Yol bulunamadı
        }
        
        List<Stop> path = new ArrayList<>();
        Stop current = end;
        while (current != null) {
            path.add(0, current);
            current = previous.get(current.getId());
        }
        
        return path;
    }

    private Vehicle determineVehicle(Stop from, Stop to) {
        // Durak tiplerine göre araç belirle
        if (from.getType() != null && to.getType() != null) {
            if (from.getType().equals("bus") || to.getType().equals("bus")) {
                return new Bus();
            } else if (from.getType().equals("tram") || to.getType().equals("tram")) {
                return new Tram();
            }
        }
        
        // Varsayılan olarak otobüs
        return new Bus();
    }

    private double calculateDistance(Location loc1, Location loc2) {
        if (loc1 == null || loc2 == null) return 0;
        
        double lat1 = loc1.getLatitude();
        double lon1 = loc1.getLongitude();
        double lat2 = loc2.getLatitude();
        double lon2 = loc2.getLongitude();
        
        // Haversine formülü ile mesafe hesaplama
        double R = 6371; // Dünya yarıçapı (km)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    private double calculateDuration(Vehicle vehicle, double distance) {
        if (vehicle == null) {
            // Yürüme hızı: 5 km/saat
            return (distance / WALKING_SPEED) * 60; // Dakikaya çevir
        }
        
        // Araç tipine göre süre hesapla (km/saat -> dakika)
        return switch (vehicle.getClass().getSimpleName().toLowerCase()) {
            case "bus" -> (distance / BUS_SPEED) * 60;
            case "tram" -> (distance / TRAM_SPEED) * 60;
            case "taxi" -> (distance / TAXI_SPEED) * 60;
            default -> (distance / WALKING_SPEED) * 60;
        };
    }
} 