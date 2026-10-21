package com.smart.parking.lot.smart.parking.lot.controller;


import com.smart.parking.lot.smart.parking.lot.entity.VehicleOwner;
import com.smart.parking.lot.smart.parking.lot.service.VehicleOwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicle_owner")
public class VehicleOwnerController {

    @Autowired
    private VehicleOwnerService vehicleOwnerService;


    @PostMapping("/add_vehicle_owner")
    public ResponseEntity<VehicleOwner> addVehicleOwner(@RequestBody VehicleOwner vehicleOwner) {
        return new ResponseEntity<>(
                vehicleOwnerService.createVehicleOwner(vehicleOwner),
                org.springframework.http.HttpStatus.CREATED);
    }


    @GetMapping("/get_vehicle_owner")
    public ResponseEntity<?> getVehicleOwners(
            @RequestParam(required = false) String vehicleOwnerId,
            @RequestParam(required = false) String aadhaarNumber,
            @RequestParam(required = false) String licenseNumber) {
        if (vehicleOwnerId != null) {
            return new ResponseEntity<>(vehicleOwnerService.getVehicleOwnerById(vehicleOwnerId), org.springframework.http.HttpStatus.OK);
        }
        if (aadhaarNumber != null) {
            return new ResponseEntity<>(vehicleOwnerService.getVehicleOwnerByAadhaarNumber(aadhaarNumber), org.springframework.http.HttpStatus.OK);
        }
        if (licenseNumber != null) {
            return new ResponseEntity<>(vehicleOwnerService.getVehicleOwnerByLicenseNumber(licenseNumber), org.springframework.http.HttpStatus.OK);
        }
        return new ResponseEntity<>(vehicleOwnerService.getVehicleOwnerList(), org.springframework.http.HttpStatus.OK);
    }

    @DeleteMapping("/delete_vehicle_owner/{vehicleOwnerId}")
    public ResponseEntity<Void> deleteVehicleOwner(@PathVariable String vehicleOwnerId) {
        vehicleOwnerService.deleteVehicleOwner(vehicleOwnerId);
        return new ResponseEntity<>(org.springframework.http.HttpStatus.NO_CONTENT);
    }

    @PutMapping("/update_vehicle_owner")
    public ResponseEntity<VehicleOwner> updateVehicleOwner(@PathVariable VehicleOwner vehicleOwner){
        return new ResponseEntity<>(
                vehicleOwnerService.updateVehicleOwner(vehicleOwner), HttpStatus.OK
        );
    }

    @PutMapping("/update_vehicle_owner_by_aadhaar_number/{aadhaarNumber}")
    public ResponseEntity<VehicleOwner> updateVehicleOwnerByAadhaarNumber(@PathVariable String aadhaarNumber, @RequestBody VehicleOwner vehicleOwner) {
        return new ResponseEntity<>(
                vehicleOwnerService.updateVehicleOwnerByAadhaarNumber(aadhaarNumber, vehicleOwner), HttpStatus.OK
        );
    }

    @PutMapping("/update_vehicle_owner_by_drv_licence_number/{licenseNumber}")
    public ResponseEntity<VehicleOwner> updateVehicleOwnerByDrvLicenseNo(@PathVariable String licenseNo, @RequestBody VehicleOwner vehicleOwner){
        return new ResponseEntity<>(
                vehicleOwnerService.updateVehicleOwnerByDrvLicenseNo(licenseNo, vehicleOwner), HttpStatus.OK
        );
    }
}
