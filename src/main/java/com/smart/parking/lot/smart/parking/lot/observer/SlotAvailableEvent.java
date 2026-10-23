package com.smart.parking.lot.smart.parking.lot.observer;

import org.springframework.context.ApplicationEvent;

public class SlotAvailableEvent extends ApplicationEvent {
    private final String slotId;
    private final String slotNumber;

    public SlotAvailableEvent(String slotId, String slotNumber) {
        super(slotId);
        this.slotId = slotId;
        this.slotNumber = slotNumber;
    }

    public String getSlotId() { return slotId; }
    public String getSlotNumber() { return slotNumber; }
}
