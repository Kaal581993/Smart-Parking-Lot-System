package com.smart.parking.lot.smart.parking.lot.observer;

import com.smart.parking.lot.smart.parking.lot.entity.ParkingSlot;
import com.smart.parking.lot.smart.parking.lot.entity.ParkingTicket;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class SlotAvailabilityObserver implements ParkingObserver {

    private final ApplicationEventPublisher publisher;

    public SlotAvailabilityObserver(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void onSlotAvailable(ParkingSlot slot) {
        publisher.publishEvent(new SlotAvailableEvent(slot.getSlotId(), slot.getSlotNumber()));
    }

    @Override
    public void onSlotOccupied(ParkingSlot slot, ParkingTicket ticket) {
        publisher.publishEvent(new SlotOccupiedEvent(slot.getSlotId(), slot.getSlotNumber(), ticket.getTicketId()));
    }

    @Override
    public void onPaymentCompleted(ParkingTicket ticket) {
        publisher.publishEvent(new PaymentCompletedEvent(ticket.getTicketId(), ticket.getTotalFee()));
    }
}
