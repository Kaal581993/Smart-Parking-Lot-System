package com.smart.parking.lot.smart.parking.lot.repository.auth;

import com.smart.parking.lot.smart.parking.lot.entity.auth.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
    Optional<Privilege> findByName(String name);
}
