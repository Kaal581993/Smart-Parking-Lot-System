package com.smart.parking.lot.smart.parking.lot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordChangeRequest(@NotBlank String currentPassword,
                                    @NotBlank @Size(min = 8, max = 72) String newPassword,
                                    @NotBlank String confirmPassword) {
}
