package com.smart.parking.lot.smart.parking.lot.state;

import com.smart.parking.lot.smart.parking.lot.entity.ParkingTicket;
import com.smart.parking.lot.smart.parking.lot.entity.CurrentTicketState;

public class ActiveTicketState implements TicketState {

    @Override
    public void checkIn(ParkingTicket ticket) {
        throw new IllegalStateException("Ticket is already ACTIVE. Cannot check in again.");
    }

    @Override
    public void checkOut(ParkingTicket ticket) {
        ticket.setCheckOutTime(new java.util.Date());
        ticket.setStatus(CurrentTicketState.COMPLETED);
        ticket.setActive(false);
    }

    @Override
    public void cancel(ParkingTicket ticket) {
        ticket.setStatus(CurrentTicketState.CANCELLED);
        ticket.setActive(false);
    }

    @Override
    public boolean canCheckOut() {
        return true;
    }

    @Override
    public boolean canCheckIn() {
        return false;
    }

    @Override
    public CurrentTicketState getState() {
        return CurrentTicketState.ACTIVE;
    }
}
