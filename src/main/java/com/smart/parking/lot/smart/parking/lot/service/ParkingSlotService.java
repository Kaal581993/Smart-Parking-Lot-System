package com.smart.parking.lot.smart.parking.lot.service;


import com.smart.parking.lot.smart.parking.lot.entity.ParkingSlot;
import com.smart.parking.lot.smart.parking.lot.repository.ParkingSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParkingSlotService {

    @Autowired
    private ParkingSlotRepository parkingSlotRepository;

    public ParkingSlot createParkingSlot(ParkingSlot parkingSlot) {
        return parkingSlotRepository.save(parkingSlot);
    }

    public List<ParkingSlot> getAllParkingSlots() {
        return parkingSlotRepository.findAll();
    }

    public ParkingSlot getParkingSlotById(String parkingSlotId) {
        return parkingSlotRepository.findById(parkingSlotId).orElse(null);
    }

    public void deleteParkingSlot(String parkingSlotId) {
        parkingSlotRepository.deleteById(parkingSlotId);
    }

    public ParkingSlot updateParkingSlot(ParkingSlot parkingSlot) {
        return parkingSlotRepository.save(parkingSlot);
    }

    public List<ParkingSlot> getAvailableParkingSlots() {
        return parkingSlotRepository.findAll();
    }

}
