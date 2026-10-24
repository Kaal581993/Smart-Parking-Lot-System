package com.smart.parking.lot.smart.parking.lot.controller;


import com.smart.parking.lot.smart.parking.lot.dto.CheckInRequest;
import com.smart.parking.lot.smart.parking.lot.dto.CheckOutRequest;
import com.smart.parking.lot.smart.parking.lot.entity.ParkingTicket;
import com.smart.parking.lot.smart.parking.lot.entity.VehicleDetails;
import com.smart.parking.lot.smart.parking.lot.entity.VehicleOwner;
import com.smart.parking.lot.smart.parking.lot.service.VehicleDetailsService;
import com.smart.parking.lot.smart.parking.lot.service.VehicleOwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/vehicle_owners")
public class VehicleOwnerController {

    @Autowired
    private VehicleOwnerService vehicleOwnerService;

    @Autowired
    private VehicleDetailsService vehicleDetailsService;

    @PostMapping
    public ResponseEntity<VehicleOwner> createVehicleOwner(@Valid @RequestBody VehicleOwner vehicleOwner) {
        return new ResponseEntity<>(vehicleOwnerService.createVehicleOwner(vehicleOwner), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> getVehicleOwners(
            Pageable pageable,
            @RequestParam(required = false) String aadhaarNumber,
            @RequestParam(required = false) String licenseNumber) {
        if (aadhaarNumber != null) {
            return new ResponseEntity<>(vehicleOwnerService.getVehicleOwnerByAadhaarNumber(aadhaarNumber), HttpStatus.OK);
        }
        if (licenseNumber != null) {
            return new ResponseEntity<>(vehicleOwnerService.getVehicleOwnerByLicenseNumber(licenseNumber), HttpStatus.OK);
        }
        return new ResponseEntity<>(vehicleOwnerService.getVehicleOwnerList(pageable), HttpStatus.OK);
    }

    @GetMapping("/{aadhaarNumber}")
    public ResponseEntity<VehicleOwner> getVehicleOwnerByAadhaar(@PathVariable String aadhaarNumber) {
        return new ResponseEntity<>(vehicleOwnerService.getVehicleOwnerByAadhaarNumber(aadhaarNumber), HttpStatus.OK);
    }

    @GetMapping("/{aadhaarNumber}/vehicles")
    public ResponseEntity<List<VehicleDetails>> getOwnerVehicles(@PathVariable String aadhaarNumber) {
        return new ResponseEntity<>(vehicleDetailsService.getVehiclesByOwnerAadhaar(aadhaarNumber), HttpStatus.OK);
    }

    @PostMapping("/{aadhaarNumber}/vehicles")
    public ResponseEntity<VehicleDetails> createOwnerVehicle(@PathVariable String aadhaarNumber,
                                                              @Valid @RequestBody VehicleDetails vehicleDetails) {
        VehicleOwner owner = vehicleOwnerService.getVehicleOwnerByAadhaarNumber(aadhaarNumber);
        vehicleDetails.setOwner(owner);
        return new ResponseEntity<>(vehicleDetailsService.createVehicleDetails(vehicleDetails), HttpStatus.CREATED);
    }

    @PutMapping("/{aadhaarNumber}")
    public ResponseEntity<VehicleOwner> updateVehicleOwner(@PathVariable String aadhaarNumber,
                                                            @Valid @RequestBody VehicleOwner vehicleOwner) {
        return new ResponseEntity<>(vehicleOwnerService.updateVehicleOwnerByAadhaarNumber(aadhaarNumber, vehicleOwner), HttpStatus.OK);
    }

    @DeleteMapping("/{aadhaarNumber}")
    public ResponseEntity<Void> deleteVehicleOwner(@PathVariable String aadhaarNumber) {
        VehicleOwner owner = vehicleOwnerService.getVehicleOwnerByAadhaarNumber(aadhaarNumber);
        vehicleOwnerService.deleteVehicleOwner(owner != null ? owner.getAadhaar().getAadhaarNo() : aadhaarNumber);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
