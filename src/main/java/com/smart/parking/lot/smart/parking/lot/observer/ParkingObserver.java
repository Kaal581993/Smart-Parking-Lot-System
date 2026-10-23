package com.smart.parking.lot.smart.parking.lot.observer;

import com.smart.parking.lot.smart.parking.lot.entity.ParkingSlot;
import com.smart.parking.lot.smart.parking.lot.entity.ParkingTicket;

public interface ParkingObserver {
    void onSlotAvailable(ParkingSlot slot);

    void onSlotOccupied(ParkingSlot slot, ParkingTicket ticket);

    void onPaymentCompleted(ParkingTicket ticket);
}
