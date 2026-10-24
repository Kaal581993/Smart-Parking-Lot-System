package com.smart.parking.lot.smart.parking.lot.service;

import com.smart.parking.lot.smart.parking.lot.entity.CurrentTicketState;
import com.smart.parking.lot.smart.parking.lot.entity.ParkingTicket;
import com.smart.parking.lot.smart.parking.lot.repository.ParkingTicketRepository;
import com.smart.parking.lot.smart.parking.lot.visitor.OverstayScanVisitor;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    public ParkingTicket saveTicket(ParkingTicket ticket) {
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

    public Page<ParkingTicket> getTickets(Pageable pageable) {
        return parkingTicketRepository.findAll(pageable);
    }

    @Transactional
    public ParkingTicket createManagedTicket(ParkingTicket ticket) {
        if (ticket.getCheckInTime() == null) ticket.setCheckInTime(new Date());
        if (ticket.getStatus() == null) ticket.setStatus(CurrentTicketState.ACTIVE);
        if (ticket.getPaidStatus() == null) ticket.setPaidStatus(com.smart.parking.lot.smart.parking.lot.entity.PaymentStatus.PENDING);
        ticket.setActive(true);
        ticket.setDeleted(false);
        return parkingTicketRepository.save(ticket);
    }

    @Transactional
    public ParkingTicket updateManagedTicket(String ticketId, ParkingTicket incoming) {
        ParkingTicket ticket = getTicketById(ticketId);
        if (incoming.getVehicleDetails() != null) ticket.setVehicleDetails(incoming.getVehicleDetails());
        if (incoming.getParkingSlot() != null) ticket.setParkingSlot(incoming.getParkingSlot());
        if (incoming.getVehicleOwner() != null) ticket.setVehicleOwner(incoming.getVehicleOwner());
        if (incoming.getVehicleType() != null) ticket.setVehicleType(incoming.getVehicleType());
        if (incoming.getCheckInTime() != null) ticket.setCheckInTime(incoming.getCheckInTime());
        ticket.setCheckOutTime(incoming.getCheckOutTime());
        ticket.setParkingDuration(incoming.getParkingDuration());
        ticket.setHourlyRate(incoming.getHourlyRate());
        ticket.setBaseFee(incoming.getBaseFee());
        ticket.setExcessFee(incoming.getExcessFee());
        ticket.setTax(incoming.getTax());
        ticket.setTotalFee(incoming.getTotalFee());
        ticket.setPendingDues(incoming.getPendingDues());
        if (incoming.getPaidStatus() != null) ticket.setPaidStatus(incoming.getPaidStatus());
        if (incoming.getStatus() != null) ticket.setStatus(incoming.getStatus());
        ticket.setActive(incoming.isActive());
        ticket.setDeleted(incoming.isDeleted());
        ticket.setCreatedBy(incoming.getCreatedBy());
        return parkingTicketRepository.save(ticket);
    }

    @Transactional
    public void deleteManagedTicket(String ticketId) {
        ParkingTicket ticket = getTicketById(ticketId);
        ticket.setDeleted(true);
        ticket.setActive(false);
        ticket.setStatus(CurrentTicketState.CANCELLED);
        parkingTicketRepository.save(ticket);
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

    @Transactional
    public ParkingTicket processPayment(String ticketId, double amount) {
        ParkingTicket ticket = getTicketById(ticketId);
        double remaining = ticket.getPendingDues() - amount;
        if (remaining <= 0) {
            ticket.setPaidStatus(com.smart.parking.lot.smart.parking.lot.entity.PaymentStatus.PAID);
            ticket.setPendingDues(0);
        } else {
            ticket.setPaidStatus(com.smart.parking.lot.smart.parking.lot.entity.PaymentStatus.PARTIALLY_PAID);
            ticket.setPendingDues(remaining);
        }
        return parkingTicketRepository.save(ticket);
    }
    public List<ParkingTicket> scanOverstayed() {
        OverstayScanVisitor visitor = new OverstayScanVisitor();
        for (ParkingTicket t : parkingTicketRepository.findAll()) {
            visitor.visit(t);
        }
        return visitor.getOverstayed();
    }
}
