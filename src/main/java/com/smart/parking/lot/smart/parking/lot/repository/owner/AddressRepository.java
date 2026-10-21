package com.smart.parking.lot.smart.parking.lot.repository.owner;

import com.smart.parking.lot.smart.parking.lot.entity.owner.Address;
import org.springframework.data.jpa.repository.JpaRepository;


import org.springframework.stereotype.Repository;


@Repository
public interface AddressRepository extends JpaRepository<Address,String> {
}
