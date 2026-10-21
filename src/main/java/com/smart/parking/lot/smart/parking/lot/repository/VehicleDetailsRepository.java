package com.smart.parking.lot.smart.parking.lot.repository;

import com.smart.parking.lot.smart.parking.lot.entity.VehicleDetails;
import org.springframework.data.jpa.repository.JpaRepository;


import org.springframework.stereotype.Repository;


@Repository
public interface VehicleDetailsRepository extends JpaRepository<VehicleDetails, Long> {
}
