package com.smart.parking.lot.smart.parking.lot.repository;

import com.smart.parking.lot.smart.parking.lot.entity.VehicleDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface VehicleDetailsRepository extends JpaRepository<VehicleDetails, String> {
    boolean existsByNumberPlate(String numberPlate);

    @Query("SELECT v FROM VehicleDetails v WHERE v.isDeleted = false")
    org.springframework.data.domain.Page<VehicleDetails> findByNotDeleted(org.springframework.data.domain.Pageable pageable);

    @Query("SELECT v FROM VehicleDetails v WHERE v.owner.aadhaar.aadhaarNo = :aadhaarNumber")
    java.util.List<VehicleDetails> findByOwnerAadhaarNumber(@Param("aadhaarNumber") String aadhaarNumber);
}
