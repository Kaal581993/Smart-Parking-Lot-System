package com.smart.parking.lot.smart.parking.lot.controller;


import com.smart.parking.lot.smart.parking.lot.entity.VehicleType;
import com.smart.parking.lot.smart.parking.lot.service.VehicleTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/vehicle_type")
public class VehicleTypeController {

    @Autowired
    private VehicleTypeService vehicleTypeService;

    @PostMapping("/add_vehicle_type")
    public ResponseEntity<VehicleType> addVehicleType(@RequestBody VehicleType vehicleType) {
        return new ResponseEntity<>(
                vehicleTypeService.createVehicleType(vehicleType),
                org.springframework.http.HttpStatus.CREATED);
    }


    @PutMapping("/update_vehicle_type/{vehicleTypeId}")
    public ResponseEntity<VehicleType> updateVehicleType(@PathVariable String vehicleTypeId, @RequestBody VehicleType vehicleType) {
        return new ResponseEntity<>(
                vehicleTypeService.updateVehicleTypeById(vehicleTypeId, vehicleType),
                org.springframework.http.HttpStatus.OK);
    }

    @GetMapping("/get_vehicle_type")
    public ResponseEntity<?> getVehicleTypes(@RequestParam(required = false) String vehicleTypeId) {
        if (vehicleTypeId != null) {
            return new ResponseEntity<>(vehicleTypeService.getVehicleTypeById(vehicleTypeId), org.springframework.http.HttpStatus.OK);
        }
        return new ResponseEntity<>(vehicleTypeService.getVehicleTypeList(), org.springframework.http.HttpStatus.OK);
    }

    @DeleteMapping("/delete_vehicle_type/{vehicleTypeId}")
    public ResponseEntity<Void> deleteVehicleType(@PathVariable String vehicleTypeId) {
        vehicleTypeService.deleteVehicleType(vehicleTypeId);
        return new ResponseEntity<>(org.springframework.http.HttpStatus.NO_CONTENT);
    }


}
