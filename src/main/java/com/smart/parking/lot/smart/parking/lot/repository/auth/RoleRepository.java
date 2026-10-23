package com.smart.parking.lot.smart.parking.lot.repository.auth;

import com.smart.parking.lot.smart.parking.lot.entity.auth.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
