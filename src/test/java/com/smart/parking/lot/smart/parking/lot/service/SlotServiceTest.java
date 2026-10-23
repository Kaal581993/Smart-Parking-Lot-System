package com.smart.parking.lot.smart.parking.lot.service;

import com.smart.parking.lot.smart.parking.lot.entity.ParkingSlot;
import com.smart.parking.lot.smart.parking.lot.repository.ParkingSlotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SlotServiceTest {

    @Mock
    private ParkingSlotRepository parkingSlotRepository;

    @InjectMocks
    private SlotService slotService;

    @Test
    void createSlot_setsAvailableAndActive() {
        ParkingSlot slot = new ParkingSlot();
        slot.setSlotNumber("P1");
        slot.setFloorNo(1);
        slot.setFloorSlotCapacity(5);
        when(parkingSlotRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        ParkingSlot saved = slotService.createSlot(slot);
        assertTrue(saved.isAvailable());
        assertTrue(saved.isActive());
    }

    @Test
    void createSlot_throwsWhenFloorNoInvalid() {
        ParkingSlot slot = new ParkingSlot();
        slot.setSlotNumber("P1");
        slot.setFloorNo(0);
        slot.setFloorSlotCapacity(5);
        assertThrows(IllegalArgumentException.class, () -> slotService.createSlot(slot));
    }

    @Test
    void markSlotOccupied_setsUnavailable() {
        ParkingSlot slot = new ParkingSlot();
        slot.setSlotId("S1");
        slot.setAvailable(true);
        when(parkingSlotRepository.findByIdWithLock("S1")).thenReturn(Optional.of(slot));
        when(parkingSlotRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        ParkingSlot updated = slotService.markSlotOccupied("S1");
        assertFalse(updated.isAvailable());
    }

    @Test
    void getSlotById_throwsWhenNotFound() {
        when(parkingSlotRepository.findById("X")).thenReturn(Optional.empty());
        assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> slotService.getSlotById("X"));
    }
}
