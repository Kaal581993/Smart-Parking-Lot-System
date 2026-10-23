package com.smart.parking.lot.smart.parking.lot.service;

import com.smart.parking.lot.smart.parking.lot.entity.ParkingSlot;
import com.smart.parking.lot.smart.parking.lot.entity.VehicleType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SlotAllocationServiceTest {

    @Mock
    private SlotService slotService;

    @InjectMocks
    private SlotAllocationService slotAllocationService;

    @Test
    void allocate_returnsSlotFromStrategy() {
        VehicleType vt = new VehicleType();
        vt.setTypeName("Car");
        ParkingSlot slot = new ParkingSlot();
        slot.setSlotId("S1");
        slot.setAvailable(true);
        slot.setActive(true);
        when(slotService.getAvailableSlots()).thenReturn(List.of(slot));
        ParkingSlot result = slotAllocationService.allocate(vt, "NEAREST");
        assertEquals("S1", result.getSlotId());
    }
}
