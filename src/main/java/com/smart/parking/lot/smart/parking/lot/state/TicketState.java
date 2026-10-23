package com.smart.parking.lot.smart.parking.lot.state;

import com.smart.parking.lot.smart.parking.lot.entity.ParkingTicket;
import com.smart.parking.lot.smart.parking.lot.entity.CurrentTicketState;

public interface TicketState {
    void checkIn(ParkingTicket ticket);

    void checkOut(ParkingTicket ticket);

    void cancel(ParkingTicket ticket);

    boolean canCheckOut();

    boolean canCheckIn();

    CurrentTicketState getState();
}
