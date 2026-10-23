package com.smart.parking.lot.smart.parking.lot.strategy.slot;

import com.smart.parking.lot.smart.parking.lot.entity.ParkingSlot;
import com.smart.parking.lot.smart.parking.lot.entity.VehicleType;

import java.util.List;

public interface SlotAllocationStrategy {
    ParkingSlot allocate(VehicleType vehicleType, List<ParkingSlot> availableSlots);
}
