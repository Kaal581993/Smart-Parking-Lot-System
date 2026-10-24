package com.smart.parking.lot.smart.parking.lot.service;


import com.smart.parking.lot.smart.parking.lot.entity.VehicleOwner;
import com.smart.parking.lot.smart.parking.lot.entity.owner.Address;
import com.smart.parking.lot.smart.parking.lot.entity.owner.LicenseNo;
import com.smart.parking.lot.smart.parking.lot.entity.owner.OwnerAadhaar;
import com.smart.parking.lot.smart.parking.lot.entity.owner.OwnerIdentityId;
import com.smart.parking.lot.smart.parking.lot.repository.VehicleOwnerRepository;
import com.smart.parking.lot.smart.parking.lot.repository.owner.AadharRepository;
import com.smart.parking.lot.smart.parking.lot.repository.owner.AddressRepository;
import com.smart.parking.lot.smart.parking.lot.repository.owner.LicenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class VehicleOwnerService {

    @Autowired
    private VehicleOwnerRepository vehicleOwnerRepository;

    @Autowired
    private AadharRepository aadharRepository;

    @Autowired
    private LicenseRepository licenseRepository;

    @Autowired
    private AddressRepository addressRepository;

    public VehicleOwner createVehicleOwner(VehicleOwner vehicleOwner) {
        if (vehicleOwnerRepository.existsByAadhaarNumber(vehicleOwner.getAadhaar().getAadhaarNo())) {
            throw new IllegalStateException("Aadhaar already registered to another owner");
        }
        if (vehicleOwnerRepository.existsByLicenseNumber(vehicleOwner.getLicenseNo().getLicenseId())) {
            throw new IllegalStateException("License already registered to another owner");
        }
        vehicleOwner = resolveReferences(vehicleOwner);
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
        VehicleOwner existing = getVehicleOwnerByAadhaarNumber(vehicleOwner);
        if (existing != null && existing.getOwnerIdentityId() != null) {
            vehicleOwnerRepository.deleteById(existing.getOwnerIdentityId());
        }
    }

    public VehicleOwner updateVehicleOwner(VehicleOwner vehicleOwner) {
        return vehicleOwnerRepository.save(resolveReferences(vehicleOwner));
    }

    public VehicleOwner getVehicleOwnerByAadhaarNumber(String aadhaarNumber) {
        return vehicleOwnerRepository.findByAadhaarNumber(aadhaarNumber);
    }

    public VehicleOwner getVehicleOwnerByLicenseNumber(String licenseNumber) {
        return vehicleOwnerRepository.findByLicenseNumber(licenseNumber);
    }

    public VehicleOwner updateVehicleOwnerByAadhaarNumber(String aadhaarNumber, VehicleOwner vehicleOwner) {
        VehicleOwner existing = getVehicleOwnerByAadhaarNumber(aadhaarNumber);
        if (existing != null && vehicleOwner.getAadhaar() == null) vehicleOwner.setAadhaar(existing.getAadhaar());
        if (existing != null && vehicleOwner.getLicenseNo() == null) vehicleOwner.setLicenseNo(existing.getLicenseNo());
        return vehicleOwnerRepository.save(resolveReferences(vehicleOwner));
    }

    public VehicleOwner updateVehicleOwnerByDrvLicenseNo(String licenseNumber, VehicleOwner vehicleOwner) {
        return vehicleOwnerRepository.save(resolveReferences(vehicleOwner));
    }

    private VehicleOwner resolveReferences(VehicleOwner vehicleOwner) {
        if (vehicleOwner.getAadhaar() != null && vehicleOwner.getAadhaar().getAadhaarNo() != null) {
            OwnerAadhaar aadhaar = aadharRepository.findById(vehicleOwner.getAadhaar().getAadhaarNo())
                    .orElseThrow(() -> new IllegalArgumentException("Aadhaar not found"));
            vehicleOwner.setAadhaar(aadhaar);
        }
        if (vehicleOwner.getLicenseNo() != null && vehicleOwner.getLicenseNo().getLicenseId() != null) {
            LicenseNo license = licenseRepository.findById(vehicleOwner.getLicenseNo().getLicenseId())
                    .orElseThrow(() -> new IllegalArgumentException("Driving license not found"));
            vehicleOwner.setLicenseNo(license);
        }
        if (vehicleOwner.getVehicleOwnerAddress() != null && vehicleOwner.getVehicleOwnerAddress().getAddressId() != null) {
            Address address = addressRepository.findById(vehicleOwner.getVehicleOwnerAddress().getAddressId())
                    .orElseThrow(() -> new IllegalArgumentException("Address not found"));
            vehicleOwner.setVehicleOwnerAddress(address);
        }
        if (vehicleOwner.getAadhaar() != null && vehicleOwner.getLicenseNo() != null) {
            vehicleOwner.setOwnerIdentityId(new OwnerIdentityId(
                    vehicleOwner.getAadhaar().getAadhaarNo(),
                    vehicleOwner.getLicenseNo().getLicenseId()));
        }
        return vehicleOwner;
    }
}
