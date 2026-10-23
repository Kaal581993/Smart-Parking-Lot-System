package com.smart.parking.lot.smart.parking.lot.service;


import com.smart.parking.lot.smart.parking.lot.entity.VehicleOwner;
import com.smart.parking.lot.smart.parking.lot.repository.VehicleOwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleOwnerService {

    @Autowired
    private VehicleOwnerRepository vehicleOwnerRepository;

    public VehicleOwner createVehicleOwner(VehicleOwner vehicleOwner) {
        if (vehicleOwnerRepository.existsByAadhaarNumber(vehicleOwner.getAadhaar().getAadhaarNo())) {
            throw new IllegalStateException("Aadhaar already registered to another owner");
        }
        if (vehicleOwnerRepository.existsByLicenseNumber(vehicleOwner.getLicenseNo().getLicenseId())) {
            throw new IllegalStateException("License already registered to another owner");
        }
        return vehicleOwnerRepository.save(vehicleOwner);
    }

    public VehicleOwner getVehicleOwnerById(String vehicleOwner) {
        return null;
    }

    public List<VehicleOwner> getVehicleOwnerList() {
        return vehicleOwnerRepository.findAll();
    }

    public void deleteVehicleOwner(String vehicleOwner) {
        // no-op: composite key requires full identity; handle via repository if needed
    }

    public VehicleOwner updateVehicleOwner(VehicleOwner vehicleOwner) {
        return vehicleOwnerRepository.save(vehicleOwner);
    }

    public VehicleOwner getVehicleOwnerByAadhaarNumber(String aadhaarNumber) {
        return vehicleOwnerRepository.findByAadhaarNumber(aadhaarNumber);
    }

    public VehicleOwner getVehicleOwnerByLicenseNumber(String licenseNumber) {
        return vehicleOwnerRepository.findByLicenseNumber(licenseNumber);
    }

    public VehicleOwner updateVehicleOwnerByAadhaarNumber(String aadhaarNumber, VehicleOwner vehicleOwner) {
        return vehicleOwnerRepository.save(vehicleOwner);
    }

    public VehicleOwner updateVehicleOwnerByDrvLicenseNo(String licenseNumber, VehicleOwner vehicleOwner) {
        return vehicleOwnerRepository.save(vehicleOwner);
    }
}
