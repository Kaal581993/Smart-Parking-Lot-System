package com.smart.parking.lot.smart.parking.lot.memento;

public class ParkingTicketMemento {
    private final String ticketId;
    private final String slotId;
    private final boolean slotWasAvailable;

    public ParkingTicketMemento(String ticketId, String slotId, boolean slotWasAvailable) {
        this.ticketId = ticketId;
        this.slotId = slotId;
        this.slotWasAvailable = slotWasAvailable;
    }

    public String getTicketId() { return ticketId; }
    public String getSlotId() { return slotId; }
    public boolean isSlotWasAvailable() { return slotWasAvailable; }
}
