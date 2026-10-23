package com.smart.parking.lot.smart.parking.lot.service;


import com.smart.parking.lot.smart.parking.lot.dto.CheckInRequest;
import com.smart.parking.lot.smart.parking.lot.entity.CurrentTicketState;
import com.smart.parking.lot.smart.parking.lot.entity.ParkingSlot;
import com.smart.parking.lot.smart.parking.lot.entity.ParkingTicket;
import com.smart.parking.lot.smart.parking.lot.repository.ParkingSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class ParkingSlotService {

    @Autowired
    private ParkingSlotRepository parkingSlotRepository;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private SlotService slotService;

    public ParkingSlot createParkingSlot(ParkingSlot parkingSlot) {
        return slotService.createSlot(parkingSlot);
    }

    public List<ParkingSlot> getSlots(String floorNo, Boolean available, String vehicleType) {
        return parkingSlotRepository.findAll().stream()
                .filter(s -> floorNo == null || String.valueOf(s.getFloorNo()).equals(floorNo))
                .filter(s -> available == null || s.isAvailable() == available)
                .filter(s -> vehicleType == null || (s.getVehicleTypeAllowed() != null && s.getVehicleTypeAllowed().getTypeName().equalsIgnoreCase(vehicleType)))
                .toList();
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

    @Transactional
    public ParkingTicket checkIn(CheckInRequest req) {
        ParkingSlot slot = slotService.getSlotById(req.slotId());
        if (!slot.isAvailable()) {
            throw new IllegalStateException("Slot already occupied");
        }

        if (ticketService.isSlotOccupied(req.slotId())) {
            throw new IllegalStateException("Slot already has an active ticket");
        }

        if (ticketService.isVehicleAlreadyParked(req.vehicleId())) {
            throw new IllegalStateException("Vehicle already has an active ticket");
        }

        return ticketService.createTicket(buildTicket(req, slot));
    }

    private ParkingTicket buildTicket(CheckInRequest req, ParkingSlot slot) {
        ParkingTicket ticket = new ParkingTicket();
        ticket.setParkingSlot(slot);
        ticket.setStatus(CurrentTicketState.ACTIVE);
        ticket.setActive(true);
        ticket.setCheckInTime(new Date());
        return ticket;
    }
}
