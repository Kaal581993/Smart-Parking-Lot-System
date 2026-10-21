package com.smart.parking.lot.smart.parking.lot.controller;


import com.smart.parking.lot.smart.parking.lot.entity.owner.Address;
import com.smart.parking.lot.smart.parking.lot.service.AddressManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private AddressManagementService addressManagementService;

    @PostMapping("/add_address")
    public ResponseEntity<Address> createAddress(@RequestBody Address address) {
        addressManagementService.createAddress(address);
        return new ResponseEntity<>(address, HttpStatus.CREATED);
    }

    @GetMapping("/get_address")
    public ResponseEntity<?> getAddresses(@RequestParam(required = false) String addressId) {
        if (addressId != null) {
            return new ResponseEntity<>(addressManagementService.getAddress(addressId), HttpStatus.OK);
        }
        return new ResponseEntity<>(addressManagementService.getAllAddresses(), HttpStatus.OK);
    }

    @DeleteMapping("/delete_address/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable String addressId) {
        addressManagementService.deleteAddress(addressId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
