package com.smart.parking.lot.smart.parking.lot.service;

import com.smart.parking.lot.smart.parking.lot.entity.ParkingSlot;
import com.smart.parking.lot.smart.parking.lot.repository.ParkingSlotRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SlotService {

    @Autowired
    private ParkingSlotRepository parkingSlotRepository;

    @Transactional
    public ParkingSlot createSlot(ParkingSlot slot) {
        if (slot.getFloorNo() <= 0) {
            throw new IllegalArgumentException("floorNo must be greater than 0");
        }
        if (slot.getFloorSlotCapacity() <= 0) {
            throw new IllegalArgumentException("floorSlotCapacity must be greater than 0");
        }
        if (slot.getSlotNumber() == null || slot.getSlotNumber().isBlank()) {
            throw new IllegalArgumentException("slotNumber is required");
        }
        slot.setAvailable(true);
        slot.setActive(true);
        return parkingSlotRepository.save(slot);
    }

    public ParkingSlot getSlotById(String slotId) {
        return parkingSlotRepository.findById(slotId)
                .orElseThrow(() -> new EntityNotFoundException("Slot not found: " + slotId));
    }

    public List<ParkingSlot> getAvailableSlots() {
        return parkingSlotRepository.findAll().stream()
                .filter(s -> s.isAvailable() && s.isActive())
                .toList();
    }

    @Transactional
    public ParkingSlot markSlotOccupied(String slotId) {
        ParkingSlot slot = getSlotById(slotId);
        slot.setAvailable(false);
        return parkingSlotRepository.save(slot);
    }

    @Transactional
    public ParkingSlot markSlotAvailable(String slotId) {
        ParkingSlot slot = getSlotById(slotId);
        slot.setAvailable(true);
        return parkingSlotRepository.save(slot);
    }

    public List<ParkingSlot> getAllSlots() {
        return parkingSlotRepository.findAll();
    }
}
