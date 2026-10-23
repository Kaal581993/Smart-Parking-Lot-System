package com.smart.parking.lot.smart.parking.lot.state;

import com.smart.parking.lot.smart.parking.lot.entity.ParkingTicket;
import com.smart.parking.lot.smart.parking.lot.entity.CurrentTicketState;

public class CompletedTicketState implements TicketState {

    @Override
    public void checkIn(ParkingTicket ticket) {
        throw new IllegalStateException("Ticket is COMPLETED. Cannot check in on a completed ticket.");
    }

    @Override
    public void checkOut(ParkingTicket ticket) {
        throw new IllegalStateException("Ticket is already COMPLETED. Cannot check out again.");
    }

    @Override
    public void cancel(ParkingTicket ticket) {
        throw new IllegalStateException("Cannot cancel a COMPLETED ticket.");
    }

    @Override
    public boolean canCheckOut() {
        return false;
    }

    @Override
    public boolean canCheckIn() {
        return false;
    }

    @Override
    public CurrentTicketState getState() {
        return CurrentTicketState.COMPLETED;
    }
}
