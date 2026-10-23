package com.smart.parking.lot.smart.parking.lot.facade;

import com.smart.parking.lot.smart.parking.lot.dto.CheckInBatchRequest;
import com.smart.parking.lot.smart.parking.lot.dto.CheckInBatchResponse;
import com.smart.parking.lot.smart.parking.lot.dto.CheckInRequest;
import com.smart.parking.lot.smart.parking.lot.dto.CheckInBatchResponse;
import com.smart.parking.lot.smart.parking.lot.dto.CheckOutRequest;
import com.smart.parking.lot.smart.parking.lot.entity.CurrentTicketState;
import com.smart.parking.lot.smart.parking.lot.entity.ParkingSlot;
import com.smart.parking.lot.smart.parking.lot.entity.ParkingTicket;
import com.smart.parking.lot.smart.parking.lot.entity.VehicleDetails;
import com.smart.parking.lot.smart.parking.lot.service.FeeService;
import com.smart.parking.lot.smart.parking.lot.service.SlotAllocationService;
import com.smart.parking.lot.smart.parking.lot.service.SlotService;
import com.smart.parking.lot.smart.parking.lot.service.TicketService;
import com.smart.parking.lot.smart.parking.lot.observer.SlotAvailabilityObserver;
import com.smart.parking.lot.smart.parking.lot.service.VehicleDetailsService;
import com.smart.parking.lot.smart.parking.lot.state.TicketStateContext;
import com.smart.parking.lot.smart.parking.lot.template.CheckInCheckOutTemplate;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

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

    @Autowired
    private SlotAvailabilityObserver slotAvailabilityObserver;

    @Autowired
    private SlotAllocationService slotAllocationService;

    private final com.smart.parking.lot.smart.parking.lot.memento.ParkingMementoCaretaker mementoCaretaker =
            new com.smart.parking.lot.smart.parking.lot.memento.ParkingMementoCaretaker();

    @Override
    protected void validateRequest(CheckInRequest request) {
        if (request.vehicleId() == null) {
            throw new IllegalArgumentException("Vehicle ID is required");
        }
    }

    @Override
    protected ParkingSlot allocateSlot(CheckInRequest request) {
        VehicleDetails vehicle = validateVehicle(request);
        if (request.slotId() != null) {
            ParkingSlot slot = slotService.getSlotById(request.slotId());
            if (!slot.isAvailable()) {
                throw new IllegalStateException("Slot is not available: " + request.slotId());
            }
            if (ticketService.isSlotOccupied(request.slotId())) {
                throw new IllegalStateException("Slot already has an active ticket");
            }
            return slot;
        }
        ParkingSlot allocated = slotAllocationService.allocate(vehicle.getVehicleType(), request.allocationStrategy());
        if (allocated == null) {
            throw new IllegalStateException("No available slot for vehicle type: " + vehicle.getVehicleType().getTypeName());
        }
        return allocated;
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
        ticketService.saveTicket(ticket);
        if (ticket.getCheckOutTime() == null) {
            slotService.markSlotOccupied(ticket.getParkingSlot().getSlotId());
            mementoCaretaker.push(new com.smart.parking.lot.smart.parking.lot.memento.ParkingTicketMemento(
                    ticket.getTicketId(), ticket.getParkingSlot().getSlotId(), false));
        }
    }

    @Override
    protected void notifyObservers(ParkingSlot slot, ParkingTicket ticket) {
        if (ticket.getCheckOutTime() == null) {
            slotAvailabilityObserver.onSlotOccupied(slot, ticket);
        } else {
            slotAvailabilityObserver.onSlotAvailable(slot);
            slotAvailabilityObserver.onPaymentCompleted(ticket);
        }
    }

    @Override
    protected ParkingTicket findTicket(String ticketId) {
        return ticketService.getTicketById(ticketId);
    }

    @Override
    protected void validateTicketForCheckout(ParkingTicket ticket) {
        TicketStateContext context = new TicketStateContext(ticket.getStatus());
        if (!context.canCheckOut()) {
            throw new IllegalStateException("Ticket is not ACTIVE. Cannot check out.");
        }
        context.checkOut(ticket);
    }

    @Override
    protected void calculateFare(ParkingTicket ticket) {
        feeService.calculateFee(ticket);
    }

    @Override
    protected void releaseSlot(ParkingTicket ticket) {
        slotService.markSlotAvailable(ticket.getParkingSlot().getSlotId());
    }

    @Transactional
    public void undoLastCheckIn() {
        com.smart.parking.lot.smart.parking.lot.memento.ParkingTicketMemento memento = mementoCaretaker.pop();
        if (memento == null) {
            throw new IllegalStateException("No check-in to undo");
        }
        ParkingTicket ticket = ticketService.getTicketById(memento.getTicketId());
        ticket.setStatus(CurrentTicketState.CANCELLED);
        ticket.setActive(false);
        ticketService.saveTicket(ticket);
        slotService.markSlotAvailable(memento.getSlotId());
    }

    @Transactional
    public CheckInBatchResponse batchCheckIn(CheckInBatchRequest request) {
        List<String> added = new java.util.ArrayList<>();
        List<String> alreadyParked = new java.util.ArrayList<>();
        List<String> notFound = new java.util.ArrayList<>();
        List<String> unavailable = new java.util.ArrayList<>();

        for (String vehicleId : new java.util.LinkedHashSet<>(request.vehicleIds())) {
            VehicleDetails vehicle = vehicleDetailsService.getVehicleDetailsById(vehicleId);
            if (vehicle == null) {
                notFound.add(vehicleId);
                continue;
            }
            if (ticketService.isVehicleAlreadyParked(vehicleId)) {
                alreadyParked.add(vehicleId);
                continue;
            }
            ParkingSlot slot = slotAllocationService.allocate(vehicle.getVehicleType(), request.slotId() == null ? null : "BEST_FIT");
            // if a specific slotId was requested, use it
            if (request.slotId() != null) {
                try {
                    slot = slotService.getSlotById(request.slotId());
                } catch (Exception e) {
                    unavailable.add(request.slotId());
                    continue;
                }
                if (!slot.isAvailable() || ticketService.isSlotOccupied(request.slotId())) {
                    unavailable.add(request.slotId());
                    continue;
                }
            }
            if (slot == null) {
                unavailable.add(vehicleId);
                continue;
            }
            ParkingTicket ticket = new ParkingTicket();
            ticket.setParkingSlot(slot);
            ticket.setVehicleDetails(vehicle);
            ticket.setVehicleType(vehicle.getVehicleType());
            ticket.setVehicleOwner(vehicle.getOwner());
            ticket.setStatus(CurrentTicketState.ACTIVE);
            ticket.setActive(true);
            ticket.setCheckInTime(new Date());
            ticketService.saveTicket(ticket);
            slotService.markSlotOccupied(slot.getSlotId());
            added.add(vehicleId);
        }
        return new CheckInBatchResponse(added, alreadyParked, notFound, unavailable);
    }
}
