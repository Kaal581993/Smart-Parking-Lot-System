package com.smart.parking.lot.smart.parking.lot.state;

import com.smart.parking.lot.smart.parking.lot.entity.ParkingTicket;
import com.smart.parking.lot.smart.parking.lot.entity.CurrentTicketState;

public class TicketStateContext {
    private TicketState state;

    public TicketStateContext(CurrentTicketState initialState) {
        setState(initialState);
    }

    public void setState(CurrentTicketState stateType) {
        switch (stateType) {
            case ACTIVE -> this.state = new ActiveTicketState();
            case COMPLETED -> this.state = new CompletedTicketState();
            case CANCELLED -> this.state = new CancelledTicketState();
            default -> throw new IllegalArgumentException("Unknown ticket state: " + stateType);
        }
    }

    public TicketState getState() {
        return state;
    }

    public void checkIn(ParkingTicket ticket) {
        state.checkIn(ticket);
    }

    public void checkOut(ParkingTicket ticket) {
        state.checkOut(ticket);
    }

    public void cancel(ParkingTicket ticket) {
        state.cancel(ticket);
    }

    public boolean canCheckOut() {
        return state.canCheckOut();
    }

    public boolean canCheckIn() {
        return state.canCheckIn();
    }
}
