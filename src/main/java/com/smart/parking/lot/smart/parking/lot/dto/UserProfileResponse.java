package com.smart.parking.lot.smart.parking.lot.dto;

import java.time.Instant;

public record UserProfileResponse(Long id, String username, String displayName, String role,
                                  String ownerId, boolean hasProfileImage, Instant imageUpdatedAt) {
}
