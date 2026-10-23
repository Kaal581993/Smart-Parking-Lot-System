package com.smart.parking.lot.smart.parking.lot.visitor;

import com.smart.parking.lot.smart.parking.lot.entity.ParkingSlot;
import com.smart.parking.lot.smart.parking.lot.entity.ParkingTicket;
import com.smart.parking.lot.smart.parking.lot.entity.CurrentTicketState;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OverstayScanVisitor implements SlotVisitor {
    private final List<ParkingTicket> overstayed = new ArrayList<>();
    private final Instant now = Instant.now();

    @Override
    public void visit(ParkingSlot slot) {
        // no-op for slot; ticket visits drive logic
    }

    @Override
    public void visit(ParkingTicket ticket) {
        if (ticket.getStatus() != CurrentTicketState.ACTIVE) return;
        if (ticket.getCheckInTime() == null) return;
        Duration allowed = ticket.getParkingSlot().getAllocationDuration();
        if (allowed == null) allowed = Duration.ofHours(2);
        Instant deadline = ticket.getCheckInTime().toInstant().plus(allowed);
        if (now.isAfter(deadline)) {
            overstayed.add(ticket);
        }
    }

    public List<ParkingTicket> getOverstayed() {
        return overstayed;
    }
}
