package com.smart.parking.lot.smart.parking.lot.controller;

import com.smart.parking.lot.smart.parking.lot.dto.CheckInRequest;
import com.smart.parking.lot.smart.parking.lot.dto.CheckOutRequest;
import com.smart.parking.lot.smart.parking.lot.entity.ParkingTicket;
import com.smart.parking.lot.smart.parking.lot.facade.ParkingFacade;
import com.smart.parking.lot.smart.parking.lot.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/check-in")
    public ResponseEntity<ParkingTicket> checkIn(@Valid @RequestBody CheckInRequest request) {
        return new ResponseEntity<>(parkingFacade.checkIn(request), HttpStatus.CREATED);
    }

    @PostMapping("/{ticketId}/check-out")
    public ResponseEntity<ParkingTicket> checkOut(@PathVariable String ticketId) {
        CheckOutRequest request = new CheckOutRequest(ticketId);
        return new ResponseEntity<>(parkingFacade.checkOut(request), HttpStatus.OK);
    }

    @PostMapping("/undo-last-checkin")
    public ResponseEntity<Void> undoLastCheckIn() {
        parkingFacade.undoLastCheckIn();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
