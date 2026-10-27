package com.smart.parking.lot.smart.parking.lot.controller;

import com.smart.parking.lot.smart.parking.lot.dto.BatchVehicleRequest;
import com.smart.parking.lot.smart.parking.lot.dto.BatchVehicleResponse;
import com.smart.parking.lot.smart.parking.lot.entity.ParkingTicket;
import com.smart.parking.lot.smart.parking.lot.entity.VehicleDetails;
import com.smart.parking.lot.smart.parking.lot.service.VehicleDetailsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
public class VehicleDetailsController {

    @Autowired
    private VehicleDetailsService vehicleDetailsService;

    @PostMapping
    public ResponseEntity<VehicleDetails> addVehicleDetails(@Valid @RequestBody VehicleDetails vehicleDetails) {
        return new ResponseEntity<>(vehicleDetailsService.createVehicleDetails(vehicleDetails), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> getVehicleDetails(Pageable pageable, @RequestParam(required = false) String vehicleDetailsId) {
        if (vehicleDetailsId != null) {
            return new ResponseEntity<>(vehicleDetailsService.getVehicleDetailsById(vehicleDetailsId), HttpStatus.OK);
        }
        return new ResponseEntity<>(vehicleDetailsService.getVehicleDetailsList(pageable), HttpStatus.OK);
    }

    @GetMapping("/{vehicleId}")
    public ResponseEntity<VehicleDetails> getVehicleById(@PathVariable String vehicleId) {
        return new ResponseEntity<>(vehicleDetailsService.getVehicleDetailsById(vehicleId), HttpStatus.OK);
    }

    @GetMapping("/{vehicleId}/active-ticket")
    public ResponseEntity<ParkingTicket> getActiveTicket(@PathVariable String vehicleId) {
        ParkingTicket ticket = vehicleDetailsService.getActiveTicketByVehicle(vehicleId);
        if (ticket == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(ticket, HttpStatus.OK);
    }

    @PutMapping("/{vehicleId}")
    public ResponseEntity<VehicleDetails> updateVehicleDetails(@PathVariable String vehicleId, @Valid @RequestBody VehicleDetails vehicleDetails) {
        vehicleDetails.setVehicleId(vehicleId);
        return new ResponseEntity<>(vehicleDetailsService.updateVehicleDetails(vehicleDetails), HttpStatus.OK);
    }

    @PostMapping("/batch")
    public ResponseEntity<BatchVehicleResponse> batchCreate(@Valid @RequestBody BatchVehicleRequest request) {
        return new ResponseEntity<>(vehicleDetailsService.batchCreateVehicles(request), HttpStatus.CREATED);
    }

    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<Void> deleteVehicleDetails(@PathVariable String vehicleId) {
        vehicleDetailsService.deleteVehicleDetails(vehicleId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
