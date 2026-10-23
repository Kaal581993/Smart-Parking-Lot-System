package com.smart.parking.lot.smart.parking.lot.controller;

import com.smart.parking.lot.smart.parking.lot.entity.VehicleDetails;
import com.smart.parking.lot.smart.parking.lot.service.VehicleDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/vehicle_details")

public class VehicleDetailsController {

    @Autowired
    private VehicleDetailsService vehicleDetailsService;


    @PostMapping("/add_vehicle_details")
    public ResponseEntity<VehicleDetails> addVehicleDetails(@Valid @RequestBody VehicleDetails vehicleDetails) {
        return new ResponseEntity<>(
                vehicleDetailsService.createVehicleDetails(vehicleDetails),
                org.springframework.http.HttpStatus.CREATED);
    }

    @GetMapping("/get_vehicle_details")
    public ResponseEntity<?> getVehicleDetails(@RequestParam(required = false) String vehicleDetailsId) {
        if (vehicleDetailsId != null) {
            return new ResponseEntity<>(vehicleDetailsService.getVehicleDetailsById(vehicleDetailsId), org.springframework.http.HttpStatus.OK);
        }
        return new ResponseEntity<>(vehicleDetailsService.getVehicleDetailsList(), org.springframework.http.HttpStatus.OK);
    }

    @PutMapping("/update_vehicle_details/{vehicleDetailsId}")
    public ResponseEntity<VehicleDetails> updateVehicleDetails(@PathVariable String vehicleDetailsId, @Valid @RequestBody VehicleDetails vehicleDetails) {
        return new ResponseEntity<>(vehicleDetailsService.updateVehicleDetails(vehicleDetails), org.springframework.http.HttpStatus.OK);
    }

    @DeleteMapping("/delete_vehicle_details/{vehicleDetailsId}")
    public ResponseEntity<Void> deleteVehicleDetails(@PathVariable String vehicleDetailsId) {
        vehicleDetailsService.deleteVehicleDetails(vehicleDetailsId);
        return new ResponseEntity<>(org.springframework.http.HttpStatus.NO_CONTENT);
    }
}
