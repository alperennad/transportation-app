package com.transport.model.stop;

public class  Transfer {
    private final String transferStopId;
    private final int transferDuration;
    private final double transferFare;

    public Transfer(String transferStopId, int transferDuration, double transferFare) {
        this.transferStopId = transferStopId;
        this.transferDuration = transferDuration;
        this.transferFare = transferFare;
    }

    public String getTransferStopId() {
        return transferStopId;
    }

    public int getTransferDuration() {
        return transferDuration;
    }

    public double getTransferFare() {
        return transferFare;
    }
} 