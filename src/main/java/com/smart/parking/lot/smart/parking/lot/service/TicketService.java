package com.smart.parking.lot.smart.parking.lot.service;

import com.smart.parking.lot.smart.parking.lot.entity.CurrentTicketState;
import com.smart.parking.lot.smart.parking.lot.entity.ParkingSlot;
import com.smart.parking.lot.smart.parking.lot.entity.ParkingTicket;
import com.smart.parking.lot.smart.parking.lot.repository.ParkingSlotRepository;
import com.smart.parking.lot.smart.parking.lot.repository.ParkingTicketRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class TicketService {

    @Autowired
    private ParkingTicketRepository parkingTicketRepository;

    @Transactional
    public ParkingTicket createTicket(ParkingTicket ticket) {
        ticket.setStatus(CurrentTicketState.ACTIVE);
        ticket.setActive(true);
        ticket.setCheckInTime(new Date());
        return parkingTicketRepository.save(ticket);
    }

    @Transactional
    public ParkingTicket completeTicket(String ticketId) {
        ParkingTicket ticket = parkingTicketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found: " + ticketId));
        ticket.setStatus(CurrentTicketState.COMPLETED);
        ticket.setActive(false);
        ticket.setCheckOutTime(new Date());
        return parkingTicketRepository.save(ticket);
    }

    @Transactional
    public ParkingTicket cancelTicket(String ticketId) {
        ParkingTicket ticket = parkingTicketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found: " + ticketId));
        ticket.setStatus(CurrentTicketState.CANCELLED);
        ticket.setActive(false);
        return parkingTicketRepository.save(ticket);
    }

    public ParkingTicket getTicketById(String ticketId) {
        return parkingTicketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found: " + ticketId));
    }

    public boolean isSlotOccupied(String slotId) {
        return parkingTicketRepository.existsByParkingSlot_SlotIdAndStatus(slotId, CurrentTicketState.ACTIVE);
    }

    public boolean isVehicleAlreadyParked(String vehicleId) {
        return parkingTicketRepository.existsByVehicleDetails_VehicleIdAndStatus(vehicleId, CurrentTicketState.ACTIVE);
    }

    public ParkingTicket getActiveTicketByVehicle(String vehicleId) {
        List<ParkingTicket> active = parkingTicketRepository.findByVehicleDetails_VehicleIdAndStatus(vehicleId, CurrentTicketState.ACTIVE);
        return active.isEmpty() ? null : active.get(0);
    }

    public List<ParkingTicket> getTicketsBySlot(String slotId) {
        return parkingTicketRepository.findByParkingSlot_SlotId(slotId);
    }
}
