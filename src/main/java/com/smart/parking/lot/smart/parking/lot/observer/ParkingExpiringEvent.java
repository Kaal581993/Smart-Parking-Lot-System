package com.smart.parking.lot.smart.parking.lot.observer;

import org.springframework.context.ApplicationEvent;

public class ParkingExpiringEvent extends ApplicationEvent {
    private final String ticketId;
    private final String vehicleNumber;
    private final long remainingMinutes;

    public ParkingExpiringEvent(Object source, String ticketId, String vehicleNumber, long remainingMinutes) {
        super(source);
        this.ticketId = ticketId;
        this.vehicleNumber = vehicleNumber;
        this.remainingMinutes = remainingMinutes;
    }

    public String getTicketId() { return ticketId; }
    public String getVehicleNumber() { return vehicleNumber; }
    public long getRemainingMinutes() { return remainingMinutes; }
}
