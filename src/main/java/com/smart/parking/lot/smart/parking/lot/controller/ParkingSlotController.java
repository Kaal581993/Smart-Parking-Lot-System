package com.smart.parking.lot.smart.parking.lot.controller;


import com.smart.parking.lot.smart.parking.lot.entity.ParkingSlot;
import com.smart.parking.lot.smart.parking.lot.entity.owner.LicenseNo;
import com.smart.parking.lot.smart.parking.lot.service.LicenseManagementService;
import com.smart.parking.lot.smart.parking.lot.service.ParkingSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/parking_lot")
public class ParkingSlotController {

    @Autowired
    private ParkingSlotService parkingSlotService;

    @RequestMapping("/add_parking_slot_details")
    public ResponseEntity<ParkingSlot> addParkingSlot(@RequestBody ParkingSlot parkingSlot) {
        return new ResponseEntity<>(parkingSlotService.createParkingSlot(parkingSlot), org.springframework.http.HttpStatus.CREATED);

        //parkingSlotService.createParkingSlot(parkingSlot);
    }

    @GetMapping("/get_parking_slot_details/{parkingSlotId}")
    public ResponseEntity<ParkingSlot> getParkingSlot(@PathVariable String parkingSlotId) {
        return new ResponseEntity<>(parkingSlotService.getParkingSlotById(parkingSlotId), org.springframework.http.HttpStatus.OK);
    }

    @GetMapping("/get_all_parking_slots")
    public ResponseEntity<List<ParkingSlot>> getAllParkingSlots() {
        return new ResponseEntity<>(parkingSlotService.getAllParkingSlots(), org.springframework.http.HttpStatus.OK);
    }

    @DeleteMapping("/delete_parking_slot/{parkingSlotId}")
    public ResponseEntity<Void> deleteParkingSlot(@PathVariable String parkingSlotId) {
        parkingSlotService.deleteParkingSlot(parkingSlotId);
        return new ResponseEntity<>(org.springframework.http.HttpStatus.NO_CONTENT);
    }

}
