package com.transport.model.stop;

import com.transport.model.location.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Stop {
    private String id;
    private String name;
    private Location location;
    private List<String> lines;
    private String type;
    private boolean sonDurak;
    private Map<String, StopConnection> nextStops;
    private TransferInfo transfer;

    public Stop(String id, String name, Location location) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.lines = new ArrayList<>();
        this.nextStops = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public List<String> getLines() {
        return lines;
    }

    public void addLine(String line) {
        lines.add(line);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSonDurak() {
        return sonDurak;
    }

    public void setSonDurak(Boolean sonDurak) {
        this.sonDurak = sonDurak;
    }

    public Map<String, StopConnection> getNextStops() {
        return nextStops;
    }

    public void addNextStop(String stopId, double mesafe, int sure, double ucret) {
        nextStops.put(stopId, new StopConnection(stopId, mesafe, sure, ucret));
    }

    public TransferInfo getTransfer() {
        return transfer;
    }

    public void setTransfer(String transferStopId, int transferSure, double transferUcret) {
        this.transfer = new TransferInfo(transferStopId, transferSure, transferUcret);
    }

    public static class StopConnection {
        private String stopId;
        private double mesafe;
        private int sure;
        private double ucret;

        public StopConnection(String stopId, double mesafe, int sure, double ucret) {
            this.stopId = stopId;
            this.mesafe = mesafe;
            this.sure = sure;
            this.ucret = ucret;
        }

        public String getStopId() {
            return stopId;
        }

        public double getMesafe() {
            return mesafe;
        }

        public int getSure() {
            return sure;
        }

        public double getUcret() {
            return ucret;
        }
    }

    public static class TransferInfo {
        private String transferStopId;
        private int transferSure;
        private double transferUcret;

        public TransferInfo(String transferStopId, int transferSure, double transferUcret) {
            this.transferStopId = transferStopId;
            this.transferSure = transferSure;
            this.transferUcret = transferUcret;
        }

        public String getTransferStopId() {
            return transferStopId;
        }

        public int getTransferSure() {
            return transferSure;
        }

        public double getTransferUcret() {
            return transferUcret;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stop stop = (Stop) o;
        return id.equals(stop.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
} 