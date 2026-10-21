package com.smart.parking.lot.smart.parking.lot.service;

import com.smart.parking.lot.smart.parking.lot.entity.owner.LicenseNo;
import com.smart.parking.lot.smart.parking.lot.repository.owner.LicenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class LicenseManagementService {



    @Autowired
    private LicenseRepository licenseRepository;

    public LicenseNo createLicense(LicenseNo license) {
        licenseRepository.save(license);
        return license;
    }

    public List<LicenseNo> getLicenseList() {
        return licenseRepository.findAll();
    }

    public LicenseNo getLicenseById(String licenseId) {
        return licenseRepository.findById(licenseId).orElse(null);
    }

    public void deleteLicense(String licenseId) {
        licenseRepository.deleteById(licenseId);
    }

}

