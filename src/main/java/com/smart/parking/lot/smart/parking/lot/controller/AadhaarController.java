package com.smart.parking.lot.smart.parking.lot.controller;


import com.smart.parking.lot.smart.parking.lot.entity.owner.OwnerAadhaar;
import com.smart.parking.lot.smart.parking.lot.service.AadhaarManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<OwnerAadhaar> addAadhaar(@RequestBody OwnerAadhaar ownerAadhaar){
        return new ResponseEntity<>(
                aadhaarManagementService.createOwnerAadhaar(ownerAadhaar),
                org.springframework.http.HttpStatus.CREATED);
    }


    @GetMapping("/get_owner_aadhaar/{ownerAadhaarId}")
    public ResponseEntity<OwnerAadhaar> getOwnerAadhaar(@PathVariable String ownerAadhaarId) {
        return new ResponseEntity<>(
                aadhaarManagementService.getOwnerAadhaarID(ownerAadhaarId),
                org.springframework.http.HttpStatus.OK);
    }

    @GetMapping("/get_all_aadhaar")
    public ResponseEntity<List<OwnerAadhaar>> getAllAadhaars() {
        return new ResponseEntity<>(
                aadhaarManagementService.getOwnerAadhaarList(),
                org.springframework.http.HttpStatus.OK);
    }

    @DeleteMapping("/delete_aadhaar/{ownerAadhaarId}")
    public ResponseEntity<Void> deleteAadhaar(@PathVariable String ownerAadhaarId) {
        aadhaarManagementService.deleteOwnerAadhaar(ownerAadhaarId);
        return new ResponseEntity<>(org.springframework.http.HttpStatus.NO_CONTENT);
    }
}
