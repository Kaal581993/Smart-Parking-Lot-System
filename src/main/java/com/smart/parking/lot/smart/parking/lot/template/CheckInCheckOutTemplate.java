package com.smart.parking.lot.smart.parking.lot.template;

import com.smart.parking.lot.smart.parking.lot.dto.CheckInRequest;
import com.smart.parking.lot.smart.parking.lot.dto.CheckOutRequest;
import com.smart.parking.lot.smart.parking.lot.entity.ParkingSlot;
import com.smart.parking.lot.smart.parking.lot.entity.ParkingTicket;
import com.smart.parking.lot.smart.parking.lot.entity.VehicleDetails;

public abstract class CheckInCheckOutTemplate {

    /**
     * Template method - fixed algorithm for check-in flow.
     * Steps are executed in order; subclasses can override individual hooks.
     */
    public final ParkingTicket checkIn(CheckInRequest request) {
        validateRequest(request);
        ParkingSlot slot = allocateSlot(request);
        VehicleDetails vehicle = validateVehicle(request);
        ParkingTicket ticket = createTicket(slot, vehicle, request);
        persistTicket(ticket);
        notifyObservers(slot, ticket);
        return ticket;
    }

    /**
     * Template method - fixed algorithm for check-out flow.
     */
    public final ParkingTicket checkOut(CheckOutRequest request) {
        ParkingTicket ticket = findTicket(request.ticketId());
        validateTicketForCheckout(ticket);
        calculateFare(ticket);
        releaseSlot(ticket);
        persistTicket(ticket);
        notifyObservers(ticket.getParkingSlot(), ticket);
        return ticket;
    }

    // ---- Template hooks ----

    protected abstract void validateRequest(CheckInRequest request);

    protected abstract ParkingSlot allocateSlot(CheckInRequest request);

    protected abstract VehicleDetails validateVehicle(CheckInRequest request);

    protected abstract ParkingTicket createTicket(ParkingSlot slot, VehicleDetails vehicle, CheckInRequest request);

    protected abstract void persistTicket(ParkingTicket ticket);

    protected abstract void notifyObservers(ParkingSlot slot, ParkingTicket ticket);

    protected abstract ParkingTicket findTicket(String ticketId);

    protected abstract void validateTicketForCheckout(ParkingTicket ticket);

    protected abstract void calculateFare(ParkingTicket ticket);

    protected abstract void releaseSlot(ParkingTicket ticket);

    // ---- Helper classes used within template ----

    public record ValidationResult(boolean valid, String message) {}
}
