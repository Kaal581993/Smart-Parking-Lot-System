package com.smart.parking.lot.smart.parking.lot.controller;


import com.smart.parking.lot.smart.parking.lot.entity.owner.LicenseNo;
import com.smart.parking.lot.smart.parking.lot.service.LicenseManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/driving_license")
public class LicenseController {
    @Autowired
    LicenseManagementService licenseManagementService;

    @PostMapping("/add_license")
    public ResponseEntity<LicenseNo> addLicense(@RequestBody LicenseNo license) {
        return new ResponseEntity<>(
                licenseManagementService.createLicense(license),
                org.springframework.http.HttpStatus.CREATED);
    }

    @GetMapping("/get_license/{licenseId}")
    public ResponseEntity<LicenseNo> getLicenseById(@PathVariable String licenseId) {
        return new ResponseEntity<>(licenseManagementService.getLicenseById(licenseId),
                org.springframework.http.HttpStatus.OK);
    }

    @GetMapping("/get_all_license")
    public ResponseEntity<List<LicenseNo>> getAllLicenses() {
        return new ResponseEntity<>(licenseManagementService.getLicenseList(),org.springframework.http.HttpStatus.OK);
    }

    @DeleteMapping("/delete_license/{licenseId}")
    public void deleteLicense(@PathVariable String licenseId) {
        licenseManagementService.deleteLicense(licenseId);
    }
}
