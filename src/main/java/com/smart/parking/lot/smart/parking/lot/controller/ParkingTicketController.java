package com.smart.parking.lot.smart.parking.lot.controller;

import com.smart.parking.lot.smart.parking.lot.dto.CheckInBatchRequest;
import com.smart.parking.lot.smart.parking.lot.dto.CheckInBatchResponse;
import com.smart.parking.lot.smart.parking.lot.dto.CheckInRequest;
import com.smart.parking.lot.smart.parking.lot.dto.CheckOutRequest;
import com.smart.parking.lot.smart.parking.lot.dto.PaymentRequest;
import com.smart.parking.lot.smart.parking.lot.entity.ParkingTicket;
import com.smart.parking.lot.smart.parking.lot.entity.VehicleDetails;
import com.smart.parking.lot.smart.parking.lot.facade.ParkingFacade;
import com.smart.parking.lot.smart.parking.lot.service.TicketService;
import com.smart.parking.lot.smart.parking.lot.service.ParkingSlotService;
import com.smart.parking.lot.smart.parking.lot.service.VehicleDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/parking_tickets")
public class ParkingTicketController {

    @Autowired
    private ParkingFacade parkingFacade;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private VehicleDetailsService vehicleDetailsService;

    @Autowired
    private ParkingSlotService parkingSlotService;

    @GetMapping
    public ResponseEntity<Page<ParkingTicket>> getTickets(Pageable pageable) {
        return new ResponseEntity<>(ticketService.getTickets(pageable), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ParkingTicket> createManagedTicket(@RequestBody ParkingTicket ticket) {
        resolveReferences(ticket);
        return new ResponseEntity<>(ticketService.createManagedTicket(ticket), HttpStatus.CREATED);
    }

    @PostMapping("/check-in")
    public ResponseEntity<ParkingTicket> checkIn(@Valid @RequestBody CheckInRequest request) {
        return new ResponseEntity<>(parkingFacade.checkIn(request), HttpStatus.CREATED);
    }

    @PostMapping("/{ticketId}/check-out")
    public ResponseEntity<ParkingTicket> checkOut(@PathVariable String ticketId) {
        CheckOutRequest request = new CheckOutRequest(ticketId);
        return new ResponseEntity<>(parkingFacade.checkOut(request), HttpStatus.OK);
    }

    @PostMapping("/{ticketId}/pay")
    public ResponseEntity<ParkingTicket> payTicket(@PathVariable String ticketId,
                                                    @Valid @RequestBody PaymentRequest request) {
        return new ResponseEntity<>(ticketService.processPayment(ticketId, request.amount()), HttpStatus.OK);
    }

    @PostMapping("/batch-check-in")
    public ResponseEntity<CheckInBatchResponse> batchCheckIn(@Valid @RequestBody CheckInBatchRequest request) {
        return new ResponseEntity<>(parkingFacade.batchCheckIn(request), HttpStatus.CREATED);
    }

    @PostMapping("/undo-last-checkin")
    public ResponseEntity<Void> undoLastCheckIn() {
        parkingFacade.undoLastCheckIn();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<ParkingTicket> getTicket(@PathVariable String ticketId) {
        return new ResponseEntity<>(ticketService.getTicketById(ticketId), HttpStatus.OK);
    }

    @PutMapping("/{ticketId}")
    public ResponseEntity<ParkingTicket> updateManagedTicket(@PathVariable String ticketId,
                                                              @RequestBody ParkingTicket ticket) {
        resolveReferences(ticket);
        return new ResponseEntity<>(ticketService.updateManagedTicket(ticketId, ticket), HttpStatus.OK);
    }

    @DeleteMapping("/{ticketId}")
    public ResponseEntity<Void> deleteManagedTicket(@PathVariable String ticketId) {
        ticketService.deleteManagedTicket(ticketId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private void resolveReferences(ParkingTicket ticket) {
        if (ticket.getVehicleDetails() != null && ticket.getVehicleDetails().getVehicleId() != null) {
            VehicleDetails vehicle = vehicleDetailsService.getVehicleDetailsById(ticket.getVehicleDetails().getVehicleId());
            ticket.setVehicleDetails(vehicle);
            ticket.setVehicleOwner(vehicle.getOwner());
            ticket.setVehicleType(vehicle.getVehicleType());
        }
        if (ticket.getParkingSlot() != null && ticket.getParkingSlot().getSlotId() != null) {
            ticket.setParkingSlot(parkingSlotService.getParkingSlotById(ticket.getParkingSlot().getSlotId()));
        }
    }
}
