package com.smart.parking.lot.smart.parking.lot.repository.auth;

import com.smart.parking.lot.smart.parking.lot.entity.auth.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
}
