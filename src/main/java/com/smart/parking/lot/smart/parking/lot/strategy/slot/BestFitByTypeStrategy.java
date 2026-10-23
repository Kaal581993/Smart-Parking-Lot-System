package com.smart.parking.lot.smart.parking.lot.strategy.slot;

import com.smart.parking.lot.smart.parking.lot.entity.ParkingSlot;
import com.smart.parking.lot.smart.parking.lot.entity.VehicleType;

import java.util.List;

public class BestFitByTypeStrategy implements SlotAllocationStrategy {
    @Override
    public ParkingSlot allocate(VehicleType vehicleType, List<ParkingSlot> availableSlots) {
        return availableSlots.stream()
                .filter(ParkingSlot::isActive)
                .filter(ParkingSlot::isAvailable)
                .filter(s -> vehicleType.equals(s.getVehicleTypeAllowed()))
                .findFirst()
                .orElse(null);
    }
}
