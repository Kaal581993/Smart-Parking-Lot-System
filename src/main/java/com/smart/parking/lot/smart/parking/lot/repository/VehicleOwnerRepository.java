package com.smart.parking.lot.smart.parking.lot.repository;

import com.smart.parking.lot.smart.parking.lot.entity.VehicleOwner;
import com.smart.parking.lot.smart.parking.lot.entity.owner.OwnerIdentityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface VehicleOwnerRepository extends JpaRepository<VehicleOwner, OwnerIdentityId> {
    VehicleOwner findByAadhaarNumber(String aadhaarNumber);

    VehicleOwner findByLicenseNumber(String licenseNumber);

    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END FROM VehicleOwner v WHERE v.aadhaar.aadhaarNo = :aadhaarNumber")
    boolean existsByAadhaarNumber(@Param("aadhaarNumber") String aadhaarNumber);

    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END FROM VehicleOwner v WHERE v.licenseNo.licenseId = :licenseNumber")
    boolean existsByLicenseNumber(@Param("licenseNumber") String licenseNumber);
}
