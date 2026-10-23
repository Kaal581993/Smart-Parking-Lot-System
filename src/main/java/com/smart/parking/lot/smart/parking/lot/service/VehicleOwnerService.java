package com.smart.parking.lot.smart.parking.lot.service;


import com.smart.parking.lot.smart.parking.lot.entity.VehicleOwner;
import com.smart.parking.lot.smart.parking.lot.repository.VehicleOwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
        VehicleOwner owner = vehicleOwnerRepository.findByAadhaarNumber(vehicleOwner);
        if (owner == null) {
            throw new jakarta.persistence.EntityNotFoundException("VehicleOwner not found: " + vehicleOwner);
        }
        return owner;
    }

    public org.springframework.data.domain.Page<VehicleOwner> getVehicleOwnerList(org.springframework.data.domain.Pageable pageable) {
        return vehicleOwnerRepository.findAll(pageable);
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
