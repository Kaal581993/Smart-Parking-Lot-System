package com.smart.parking.lot.smart.parking.lot.strategy.slot;

import com.smart.parking.lot.smart.parking.lot.entity.ParkingSlot;
import com.smart.parking.lot.smart.parking.lot.entity.VehicleType;

import java.util.Comparator;
import java.util.List;

public class NearestSlotStrategy implements SlotAllocationStrategy {
    @Override
    public ParkingSlot allocate(VehicleType vehicleType, List<ParkingSlot> availableSlots) {
        return availableSlots.stream()
                .filter(ParkingSlot::isActive)
                .filter(ParkingSlot::isAvailable)
                .filter(s -> s.getVehicleTypeAllowed() == null || s.getVehicleTypeAllowed().equals(vehicleType))
                .min(Comparator.comparingInt(ParkingSlot::getFloorNo).thenComparing(ParkingSlot::getSlotNumber))
                .orElse(null);
    }
}
