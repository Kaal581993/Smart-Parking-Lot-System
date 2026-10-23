package com.smart.parking.lot.smart.parking.lot.repository;

import com.smart.parking.lot.smart.parking.lot.entity.VehicleDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface VehicleDetailsRepository extends JpaRepository<VehicleDetails, String> {
    boolean existsByNumberPlate(String numberPlate);

    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END FROM VehicleDetails v WHERE v.numberPlate = :numberPlate AND v.owner.ownerIdentityId <> :ownerId")
    boolean existsByNumberPlateAndOwnerNot(@Param("numberPlate") String numberPlate, @Param("ownerId") String ownerId);
}
