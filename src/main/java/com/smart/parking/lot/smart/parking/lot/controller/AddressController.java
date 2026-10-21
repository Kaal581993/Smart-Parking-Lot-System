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

    @GetMapping("/get_address/{addressId}")
    public ResponseEntity<Address> getAddress(@PathVariable String addressId) {
        Address address = addressManagementService.getAddress(addressId);
        return new ResponseEntity<>(address, HttpStatus.OK);
    }

    @GetMapping("/get_all_address")
    public ResponseEntity<List<Address>> getAllAddresses() {
        List<Address> addresses = addressManagementService.getAllAddresses();
        return new ResponseEntity<>(addresses, HttpStatus.OK);
    }

    @DeleteMapping("/delete_address/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable String addressId) {
        addressManagementService.deleteAddress(addressId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
