package com.smart.parking.lot.smart.parking.lot.observer;

import org.springframework.context.ApplicationEvent;

public class SlotOccupiedEvent extends ApplicationEvent {
    private final String slotId;
    private final String slotNumber;
    private final String ticketId;

    public SlotOccupiedEvent(String slotId, String slotNumber, String ticketId) {
        super(slotId);
        this.slotId = slotId;
        this.slotNumber = slotNumber;
        this.ticketId = ticketId;
    }

    public String getSlotId() { return slotId; }
    public String getSlotNumber() { return slotNumber; }
    public String getTicketId() { return ticketId; }
}
