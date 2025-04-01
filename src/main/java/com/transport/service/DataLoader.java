package com.transport.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transport.model.location.Location;
import com.transport.model.stop.Stop;
import com.transport.model.vehicle.Bus;
import com.transport.model.vehicle.Tram;
import com.transport.model.vehicle.Vehicle;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DataLoader {
    private final String filePath;
    private List<Stop> stops;
    private Map<String, Stop> stopsMap;
    private double taxiOpeningFee;
    private double taxiCostPerKm;

    public DataLoader(String filePath) {
        this.filePath = filePath;
        this.stops = new ArrayList<>();
        this.stopsMap = new HashMap<>();
    }

    public void loadData() throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("Veri dosyası bulunamadı: " + filePath);
        }

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> data = mapper.readValue(file, Map.class);
        
        if (data == null) {
            throw new IOException("Veri dosyası boş veya geçersiz format");
        }

        // Taksi ücretlerini yükle
        loadTaxiFares(data);

        // Detaylı durak verilerini yükle
        loadDetailedStops(data);

        // Basit durak verilerini yükle
        loadSimpleStops(data);
    }

    @SuppressWarnings("unchecked")
    private void loadTaxiFares(Map<String, Object> data) {
        Map<String, Double> taxiData = (Map<String, Double>) data.get("taxi");
        if (taxiData != null) {
            this.taxiOpeningFee = taxiData.get("openingFee");
            this.taxiCostPerKm = taxiData.get("costPerKm");
        }
    }

    @SuppressWarnings("unchecked")
    private void loadDetailedStops(Map<String, Object> data) throws IOException {
        List<Map<String, Object>> duraklar = (List<Map<String, Object>>) data.get("duraklar");
        if (duraklar != null) {
            for (Map<String, Object> durak : duraklar) {
                String id = (String) durak.get("id");
                String name = (String) durak.get("name");
                String type = (String) durak.get("type");
                Double lat = ((Number) durak.get("lat")).doubleValue();
                Double lon = ((Number) durak.get("lon")).doubleValue();
                Boolean sonDurak = (Boolean) durak.get("sonDurak");

                Stop stop = new Stop(id, name, new Location(lat, lon));
                stop.setType(type);
                stop.setSonDurak(sonDurak);

                // Sonraki durakları ekle
                List<Map<String, Object>> nextStops = (List<Map<String, Object>>) durak.get("nextStops");
                if (nextStops != null) {
                    for (Map<String, Object> nextStop : nextStops) {
                        String nextStopId = (String) nextStop.get("stopId");
                        double mesafe = ((Number) nextStop.get("mesafe")).doubleValue();
                        int sure = ((Number) nextStop.get("sure")).intValue();
                        double ucret = ((Number) nextStop.get("ucret")).doubleValue();
                        
                        stop.addNextStop(nextStopId, mesafe, sure, ucret);
                    }
                }

                // Transfer bilgilerini ekle
                Map<String, Object> transfer = (Map<String, Object>) durak.get("transfer");
                if (transfer != null) {
                    String transferStopId = (String) transfer.get("transferStopId");
                    int transferSure = ((Number) transfer.get("transferSure")).intValue();
                    double transferUcret = ((Number) transfer.get("transferUcret")).doubleValue();
                    
                    stop.setTransfer(transferStopId, transferSure, transferUcret);
                }

                stops.add(stop);
                stopsMap.put(id, stop);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void loadSimpleStops(Map<String, Object> data) throws IOException {
        List<Map<String, Object>> stopsData = (List<Map<String, Object>>) data.get("stops");
        if (stopsData != null) {
            for (Map<String, Object> stopData : stopsData) {
                String id = (String) stopData.get("id");
                
                // Eğer bu durak zaten yüklendiyse atla
                if (stopsMap.containsKey(id)) {
                    continue;
                }

                String name = (String) stopData.get("name");
                Map<String, Double> location = (Map<String, Double>) stopData.get("location");
                
                if (id == null || name == null || location == null) {
                    throw new IOException("Durak verisi eksik veya geçersiz");
                }
                
                Double lat = location.get("lat");
                Double lon = location.get("lon");
                
                if (lat == null || lon == null) {
                    throw new IOException("Durak koordinatları eksik veya geçersiz");
                }
                
                Stop stop = new Stop(id, name, new Location(lat, lon));
                
                List<String> lines = (List<String>) stopData.get("lines");
                if (lines != null) {
                    for (String line : lines) {
                        stop.addLine(line);
                    }
                }
                
                stops.add(stop);
                stopsMap.put(id, stop);
            }
        }
    }

    public List<Stop> getStops() {
        return stops;
    }

    public Stop getStop(String id) {
        return stopsMap.get(id);
    }

    public double getTaxiOpeningFee() {
        return taxiOpeningFee;
    }

    public double getTaxiCostPerKm() {
        return taxiCostPerKm;
    }
} 