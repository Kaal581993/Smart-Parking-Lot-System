package com.smart.parking.lot.smart.parking.lot.controller;


import com.smart.parking.lot.smart.parking.lot.dto.CheckInRequest;
import com.smart.parking.lot.smart.parking.lot.dto.CheckOutRequest;
import com.smart.parking.lot.smart.parking.lot.entity.ParkingSlot;
import com.smart.parking.lot.smart.parking.lot.entity.ParkingTicket;
import com.smart.parking.lot.smart.parking.lot.facade.ParkingFacade;
import com.smart.parking.lot.smart.parking.lot.service.ParkingSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/parking_lot")
public class ParkingSlotController {

    @Autowired
    private ParkingSlotService parkingSlotService;

    @Autowired
    private ParkingFacade parkingFacade;

    @RequestMapping("/add_parking_slot_details")
    public ResponseEntity<ParkingSlot> addParkingSlot(@Valid @RequestBody ParkingSlot parkingSlot) {
        return new ResponseEntity<>(parkingSlotService.createParkingSlot(parkingSlot), org.springframework.http.HttpStatus.CREATED);
    }

    @PostMapping("/check_in")
    public ResponseEntity<ParkingTicket> checkIn(@Valid @RequestBody CheckInRequest request) {
        ParkingTicket ticket = parkingFacade.checkIn(request);
        return new ResponseEntity<>(ticket, org.springframework.http.HttpStatus.CREATED);
    }

    @PostMapping("/check_out")
    public ResponseEntity<ParkingTicket> checkOut(@Valid @RequestBody CheckOutRequest request) {
        ParkingTicket ticket = parkingFacade.checkOut(request);
        return new ResponseEntity<>(ticket, org.springframework.http.HttpStatus.OK);
    }

    @GetMapping("/get_parking_slot")
    public ResponseEntity<?> getParkingSlots(@RequestParam(required = false) String parkingSlotId) {
        if (parkingSlotId != null) {
            return new ResponseEntity<>(parkingSlotService.getParkingSlotById(parkingSlotId), org.springframework.http.HttpStatus.OK);
        }
        return new ResponseEntity<>(parkingSlotService.getAllParkingSlots(), org.springframework.http.HttpStatus.OK);
    }

    @DeleteMapping("/delete_parking_slot/{parkingSlotId}")
    public ResponseEntity<Void> deleteParkingSlot(@PathVariable String parkingSlotId) {
        parkingSlotService.deleteParkingSlot(parkingSlotId);
        return new ResponseEntity<>(org.springframework.http.HttpStatus.NO_CONTENT);
    }

}
