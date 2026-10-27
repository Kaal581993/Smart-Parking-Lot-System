package com.smart.parking.lot.smart.parking.lot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfileUpdateRequest(@NotBlank @Size(max = 80) String displayName) {
}
