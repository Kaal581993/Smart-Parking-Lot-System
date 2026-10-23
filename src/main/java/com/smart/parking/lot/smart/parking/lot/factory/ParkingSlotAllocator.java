package com.smart.parking.lot.smart.parking.lot.factory;

import com.smart.parking.lot.smart.parking.lot.entity.ParkingSlot;
import org.springframework.stereotype.Component;

@Component
public class ParkingSlotAllocator {

    public ParkingSlot allocate(String slotNumber, int floorNo, double costPerHour) {
        ParkingSlot slot = new ParkingSlot();
        slot.setSlotNumber(slotNumber);
        slot.setFloorNo(floorNo);
        slot.setCostPerHour(costPerHour);
        slot.setAvailable(true);
        return slot;
    }
}
