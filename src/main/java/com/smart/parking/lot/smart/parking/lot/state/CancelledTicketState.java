package com.smart.parking.lot.smart.parking.lot.state;

import com.smart.parking.lot.smart.parking.lot.entity.ParkingTicket;
import com.smart.parking.lot.smart.parking.lot.entity.CurrentTicketState;

public class CancelledTicketState implements TicketState {

    @Override
    public void checkIn(ParkingTicket ticket) {
        throw new IllegalStateException("Ticket is CANCELLED. Cannot check in on a cancelled ticket.");
    }

    @Override
    public void checkOut(ParkingTicket ticket) {
        throw new IllegalStateException("Cannot check out a CANCELLED ticket.");
    }

    @Override
    public void cancel(ParkingTicket ticket) {
        throw new IllegalStateException("Ticket is already CANCELLED.");
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
        return CurrentTicketState.CANCELLED;
    }
}
