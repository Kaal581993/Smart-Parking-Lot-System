package com.smart.parking.lot.smart.parking.lot.visitor;

import com.smart.parking.lot.smart.parking.lot.entity.ParkingSlot;
import com.smart.parking.lot.smart.parking.lot.entity.ParkingTicket;

public interface SlotVisitor {
    void visit(ParkingSlot slot);

    void visit(ParkingTicket ticket);
}
