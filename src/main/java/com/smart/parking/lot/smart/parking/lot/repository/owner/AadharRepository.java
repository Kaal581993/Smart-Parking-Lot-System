package com.smart.parking.lot.smart.parking.lot.repository.owner;

import com.smart.parking.lot.smart.parking.lot.entity.owner.OwnerAadhaar;
import org.springframework.data.jpa.repository.JpaRepository;


import org.springframework.stereotype.Repository;


@Repository
public interface AadharRepository extends JpaRepository<OwnerAadhaar,String> {
}
