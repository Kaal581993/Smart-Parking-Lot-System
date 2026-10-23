package com.smart.parking.lot.smart.parking.lot.controller;


import com.smart.parking.lot.smart.parking.lot.entity.owner.OwnerAadhaar;
import com.smart.parking.lot.smart.parking.lot.service.AadhaarManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/aadhaar")
public class AadhaarController {

    @Autowired
    AadhaarManagementService aadhaarManagementService;
//
//    @Autowired
//    public AadhaarController(AadhaarManagementService aadhaarManagementService) {
//        this.aadhaarManagementService = aadhaarManagementService;
//    }

    @PostMapping("/add_aadhaar")
    public ResponseEntity<OwnerAadhaar> addAadhaar(@Valid @RequestBody OwnerAadhaar ownerAadhaar){
        return new ResponseEntity<>(
                aadhaarManagementService.createOwnerAadhaar(ownerAadhaar),
                org.springframework.http.HttpStatus.CREATED);
    }


    @GetMapping("/get_aadhaar")
    public ResponseEntity<?> getAadhaars(@RequestParam(required = false) String ownerAadhaarId) {
        if (ownerAadhaarId != null) {
            return new ResponseEntity<>(aadhaarManagementService.getOwnerAadhaarID(ownerAadhaarId), org.springframework.http.HttpStatus.OK);
        }
        return new ResponseEntity<>(aadhaarManagementService.getOwnerAadhaarList(), org.springframework.http.HttpStatus.OK);
    }

    @PutMapping("/update_aadhaar")
    public ResponseEntity<OwnerAadhaar> updateAadhaar(@Valid @RequestBody OwnerAadhaar ownerAadhaar) {
        return new ResponseEntity<>(aadhaarManagementService.updateOwnerAadhaar(ownerAadhaar), org.springframework.http.HttpStatus.OK);
    }

    @DeleteMapping("/delete_aadhaar/{ownerAadhaarId}")
    public ResponseEntity<Void> deleteAadhaar(@PathVariable String ownerAadhaarId) {
        aadhaarManagementService.deleteOwnerAadhaar(ownerAadhaarId);
        return new ResponseEntity<>(org.springframework.http.HttpStatus.NO_CONTENT);
    }
}
