package com.smart.parking.lot.smart.parking.lot.controller;


import com.smart.parking.lot.smart.parking.lot.entity.owner.LicenseNo;
import com.smart.parking.lot.smart.parking.lot.service.LicenseManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/driving_license")
public class LicenseController {
    @Autowired
    LicenseManagementService licenseManagementService;

    @PostMapping("/add_license")
    public ResponseEntity<LicenseNo> addLicense(@Valid @RequestBody LicenseNo license) {
        return new ResponseEntity<>(
                licenseManagementService.createLicense(license),
                org.springframework.http.HttpStatus.CREATED);
    }

    @GetMapping("/get_license")
    public ResponseEntity<?> getLicenses(@RequestParam(required = false) String licenseId) {
        if (licenseId != null) {
            return new ResponseEntity<>(licenseManagementService.getLicenseById(licenseId), org.springframework.http.HttpStatus.OK);
        }
        return new ResponseEntity<>(licenseManagementService.getLicenseList(), org.springframework.http.HttpStatus.OK);
    }

    @PutMapping("/update_license")
    public ResponseEntity<LicenseNo> updateLicense(@Valid @RequestBody LicenseNo license) {
        return new ResponseEntity<>(licenseManagementService.updateLicense(license), org.springframework.http.HttpStatus.OK);
    }

    @DeleteMapping("/delete_license/{licenseId}")
    public ResponseEntity<Void> deleteLicense(@PathVariable String licenseId) {
        licenseManagementService.deleteLicense(licenseId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
