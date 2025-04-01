package com.transport.model.route;

import com.transport.model.passenger.Passenger;
import com.transport.model.stop.Stop;
import com.transport.model.vehicle.Vehicle;
import com.transport.model.vehicle.Bus;
import com.transport.model.vehicle.Tram;
import com.transport.model.vehicle.Taxi;

import java.util.ArrayList;
import java.util.List;

public class Route {
    private List<RouteSegment> segments;
    private double totalDistance;
    private double totalDuration;
    private double totalFare;
    private Passenger passenger;

    public Route(Passenger passenger) {
        this.segments = new ArrayList<>();
        this.passenger = passenger;
    }

    public void addSegment(Stop from, Stop to, Vehicle vehicle, double distance, double duration) {
        segments.add(new RouteSegment(from, to, vehicle, distance, duration));
        totalDistance += distance;
        totalDuration += duration;
        calculateTotalFare();
    }

    public void calculateTotalFare() {
        double calculatedFare = 0.0;
        Vehicle lastVehicle = null;
        Stop lastStop = null;

        for (RouteSegment segment : segments) {
            // Yürüme segmentlerini atla
            if (segment.getVehicle() == null) {
                continue;
            }

            // Yeni bir araç başlangıcı veya transfer durumu
            if (lastVehicle == null || 
                !lastVehicle.getClass().equals(segment.getVehicle().getClass()) || 
                (lastStop != null && !lastStop.equals(segment.getFrom()))) {
                
                // Araç tipine göre ücret hesapla ve indirim uygula
                double segmentFare = 0.0;
                if (segment.getVehicle() instanceof Bus || segment.getVehicle() instanceof Tram) {
                    segmentFare = passenger.calculateDiscount(27.0); // Her biniş için indirimli ücret
                } else if (segment.getVehicle() instanceof Taxi) {
                    // Taksi için mesafeye bağlı ücret (örnek: km başına 5 TL)
                    segmentFare = passenger.calculateDiscount(segment.getDistance() * 5.0);
                }
                calculatedFare += segmentFare;
            }

            lastVehicle = segment.getVehicle();
            lastStop = segment.getFrom();
        }

        this.totalFare = calculatedFare;
    }

    public List<RouteSegment> getSegments() {
        return segments;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public double getTotalDuration() {
        return totalDuration;
    }

    public double getTotalFare() {
        return totalFare;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════\n");
        sb.append("           🚏 ROTA BİLGİLERİ\n");
        sb.append("═══════════════════════════════════════════════\n\n");

        // Yolcu bilgisi
        sb.append("👤 Yolcu Tipi: ").append(passenger.getClass().getSimpleName()
            .replace("Passenger", "")
            .replace("Regular", "Normal")
            .replace("Student", "Öğrenci")
            .replace("Elderly", "Yaşlı"))
            .append("\n\n");
        
        // Başlangıç ve varış
        sb.append("📍 BAŞLANGIÇ - VARIŞ\n");
        sb.append("──────────────────────\n");
        if (segments.get(0).getFrom() != null) {
            sb.append("▶️ Başlangıç: ").append(segments.get(0).getFrom().getName()).append("\n");
        } else {
            sb.append("▶️ Başlangıç: Verilen Konum\n");
        }
        
        if (segments.get(segments.size() - 1).getTo() != null) {
            sb.append("⭐ Varış: ").append(segments.get(segments.size() - 1).getTo().getName()).append("\n");
        } else {
            sb.append("⭐ Varış: Hedef Konum\n");
        }
        
        // Detaylı güzergah
        sb.append("\n📝 DETAYLI GÜZERGAH\n");
        sb.append("──────────────────────\n");
        String currentVehicleType = null;
        int segmentCount = 1;
        double currentFare = 0.0;
        
        for (RouteSegment segment : segments) {
            Vehicle vehicle = segment.getVehicle();
            String vehicleEmoji;
            String vehicleType;
            String vehicleName;
            
            if (vehicle == null) {
                vehicleEmoji = "🚶";
                vehicleType = "walking";
                vehicleName = "Yürüyüş";
            } else {
                vehicleType = vehicle.getClass().getSimpleName().toLowerCase();
                vehicleName = switch (vehicleType) {
                    case "bus" -> "Otobüs";
                    case "tram" -> "Tramvay";
                    case "taxi" -> "Taksi";
                    default -> "Araç";
                };
                vehicleEmoji = switch (vehicleType) {
                    case "bus" -> "🚌";
                    case "tram" -> "🚊";
                    case "taxi" -> "🚕";
                    default -> "🚗";
                };
            }
            
            if (currentVehicleType == null || !currentVehicleType.equals(vehicleType)) {
                if (currentVehicleType != null && !currentVehicleType.equals(vehicleType)) {
                    // Aktarma bilgisini göster
                    sb.append("   ↳ Aktarma: ").append(String.format("%.2f", currentFare)).append(" TL\n");
                    currentFare = 0.0;
                }
                
                sb.append("\n").append(segmentCount++).append(". ").append(vehicleEmoji).append(" ").append(vehicleName).append("\n");
                if (segment.getFrom() != null) {
                    sb.append("   ↳ Başlangıç: ").append(segment.getFrom().getName()).append("\n");
                } else {
                    sb.append("   ↳ Başlangıç: Verilen Konum\n");
                }
                currentVehicleType = vehicleType;
            }
            
            if (segment.getTo() != null) {
                sb.append("   ↳ Sonraki Durak: ").append(segment.getTo().getName()).append("\n");
            } else {
                sb.append("   ↳ Sonraki Durak: Hedef Konum\n");
            }
            
            // Mesafe ve süre bilgilerini göster
            if (segment.getDistance() > 0 || segment.getDuration() > 0) {
                sb.append("      • Mesafe: ").append(String.format("%.2f", segment.getDistance())).append(" km\n");
                sb.append("      • Süre: ").append(String.format("%.0f", segment.getDuration())).append(" dakika\n");
            }
            
            // Ücret bilgisini güncelle
            if (vehicle != null) {
                if (vehicleType.equals("bus") || vehicleType.equals("tram")) {
                    currentFare = 27.0;
                    if (passenger != null) {
                        currentFare = passenger.calculateDiscount(currentFare);
                    }
                } else if (vehicleType.equals("taxi")) {
                    currentFare = vehicle.calculateFare(segment.getDistance());
                    if (passenger != null) {
                        currentFare = passenger.calculateDiscount(currentFare);
                    }
                }
            }
        }
        
        // Son segmentin ücretini göster
        if (currentFare > 0) {
            sb.append("   ↳ Ücret: ").append(String.format("%.2f", currentFare)).append(" TL\n");
        }
        
        // Özet bilgiler
        sb.append("\n📊 ÖZET BİLGİLER\n");
        sb.append("──────────────────────\n");
        sb.append("📏 Toplam Mesafe: ").append(String.format("%.2f", totalDistance)).append(" km\n");
        sb.append("⏱️ Toplam Süre: ").append(String.format("%.0f", totalDuration)).append(" dakika\n");
        sb.append("💰 Toplam Ücret: ").append(String.format("%.2f", totalFare)).append(" TL\n");
        
        sb.append("\n═══════════════════════════════════════════════\n");
        
        return sb.toString();
    }
} 