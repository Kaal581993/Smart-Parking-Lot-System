package com.smart.parking.lot.smart.parking.lot.repository;

import com.smart.parking.lot.smart.parking.lot.entity.VehicleOwner;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;


@Repository
public interface VehicleOwnerRepository extends JpaRepository<VehicleOwner, Long> {
    VehicleOwner findByAadhaarNumber(String aadhaarNumber);

    VehicleOwner findByLicenseNumber(String licenseNumber);
}
