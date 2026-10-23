package com.smart.parking.lot.smart.parking.lot.facade;

import com.smart.parking.lot.smart.parking.lot.dto.CheckInRequest;
import com.smart.parking.lot.smart.parking.lot.dto.CheckOutRequest;
import com.smart.parking.lot.smart.parking.lot.entity.CurrentTicketState;
import com.smart.parking.lot.smart.parking.lot.entity.ParkingSlot;
import com.smart.parking.lot.smart.parking.lot.entity.ParkingTicket;
import com.smart.parking.lot.smart.parking.lot.entity.VehicleDetails;
import com.smart.parking.lot.smart.parking.lot.service.FeeService;
import com.smart.parking.lot.smart.parking.lot.service.SlotService;
import com.smart.parking.lot.smart.parking.lot.service.TicketService;
import com.smart.parking.lot.smart.parking.lot.service.VehicleDetailsService;
import com.smart.parking.lot.smart.parking.lot.template.CheckInCheckOutTemplate;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Component
public class ParkingFacade extends CheckInCheckOutTemplate {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private SlotService slotService;

    @Autowired
    private FeeService feeService;

    @Autowired
    private VehicleDetailsService vehicleDetailsService;

    @Override
    protected void validateRequest(CheckInRequest request) {
        if (request.vehicleId() == null || request.slotId() == null) {
            throw new IllegalArgumentException("Vehicle ID and Slot ID are required");
        }
    }

    @Override
    protected ParkingSlot allocateSlot(CheckInRequest request) {
        ParkingSlot slot = slotService.getSlotById(request.slotId());
        if (!slot.isAvailable()) {
            throw new IllegalStateException("Slot is not available: " + request.slotId());
        }
        if (ticketService.isSlotOccupied(request.slotId())) {
            throw new IllegalStateException("Slot already has an active ticket");
        }
        return slot;
    }

    @Override
    protected VehicleDetails validateVehicle(CheckInRequest request) {
        VehicleDetails vehicle = vehicleDetailsService.getVehicleDetailsById(request.vehicleId());
        if (vehicle == null) {
            throw new EntityNotFoundException("Vehicle not found: " + request.vehicleId());
        }
        if (ticketService.isVehicleAlreadyParked(request.vehicleId())) {
            throw new IllegalStateException("Vehicle already has an active ticket");
        }
        return vehicle;
    }

    @Override
    protected ParkingTicket createTicket(ParkingSlot slot, VehicleDetails vehicle, CheckInRequest request) {
        ParkingTicket ticket = new ParkingTicket();
        ticket.setParkingSlot(slot);
        ticket.setVehicleDetails(vehicle);
        ticket.setVehicleType(vehicle.getVehicleType());
        ticket.setVehicleOwner(vehicle.getOwner());
        ticket.setStatus(CurrentTicketState.ACTIVE);
        ticket.setActive(true);
        ticket.setCheckInTime(new Date());
        return ticket;
    }

    @Override
    @Transactional
    protected void persistTicket(ParkingTicket ticket) {
        ticketService.createTicket(ticket);
        slotService.markSlotOccupied(ticket.getParkingSlot().getSlotId());
    }

    @Override
    protected void notifyObservers(ParkingSlot slot, ParkingTicket ticket) {
        // Observer notifications are handled via facade methods
    }

    @Override
    protected ParkingTicket findTicket(String ticketId) {
        return ticketService.getTicketById(ticketId);
    }

    @Override
    protected void validateTicketForCheckout(ParkingTicket ticket) {
        if (ticket.getStatus() != CurrentTicketState.ACTIVE) {
            throw new IllegalStateException("Ticket is not ACTIVE. Cannot check out.");
        }
    }

    @Override
    protected void calculateFare(ParkingTicket ticket) {
        feeService.calculateFee(ticket);
    }

    @Override
    protected void releaseSlot(ParkingTicket ticket) {
        slotService.markSlotAvailable(ticket.getParkingSlot().getSlotId());
    }
}
