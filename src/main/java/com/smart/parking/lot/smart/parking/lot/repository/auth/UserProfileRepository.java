package com.smart.parking.lot.smart.parking.lot.repository.auth;

import com.smart.parking.lot.smart.parking.lot.entity.auth.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}
