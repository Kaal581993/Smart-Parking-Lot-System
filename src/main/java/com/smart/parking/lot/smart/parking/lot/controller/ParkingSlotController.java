package com.smart.parking.lot.smart.parking.lot.controller;

import com.smart.parking.lot.smart.parking.lot.entity.ParkingSlot;
import com.smart.parking.lot.smart.parking.lot.entity.ParkingTicket;
import com.smart.parking.lot.smart.parking.lot.service.ParkingSlotService;
import com.smart.parking.lot.smart.parking.lot.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/parking_slots")
public class ParkingSlotController {

    @Autowired
    private ParkingSlotService parkingSlotService;

    @Autowired
    private TicketService ticketService;

    @PostMapping
    public ResponseEntity<ParkingSlot> addParkingSlot(@Valid @RequestBody ParkingSlot parkingSlot) {
        return new ResponseEntity<>(parkingSlotService.createParkingSlot(parkingSlot), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ParkingSlot>> getParkingSlots(
            @RequestParam(required = false) Integer floorNo,
            @RequestParam(required = false) Boolean available,
            @RequestParam(required = false) String vehicleType) {
        return new ResponseEntity<>(parkingSlotService.getSlots(
                floorNo == null ? null : String.valueOf(floorNo), available, vehicleType), HttpStatus.OK);
    }

    @GetMapping("/{slotId}")
    public ResponseEntity<ParkingSlot> getParkingSlotById(@PathVariable String slotId) {
        return new ResponseEntity<>(parkingSlotService.getParkingSlotById(slotId), HttpStatus.OK);
    }

    @GetMapping("/{slotId}/tickets")
    public ResponseEntity<List<ParkingTicket>> getSlotTickets(@PathVariable String slotId) {
        return new ResponseEntity<>(ticketService.getTicketsBySlot(slotId), HttpStatus.OK);
    }

    @DeleteMapping("/{slotId}")
    public ResponseEntity<Void> deleteParkingSlot(@PathVariable String slotId) {
        parkingSlotService.deleteParkingSlot(slotId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
