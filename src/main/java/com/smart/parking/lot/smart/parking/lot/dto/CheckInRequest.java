package com.smart.parking.lot.smart.parking.lot.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

public record CheckInRequest(
        @NotNull String vehicleId,
        @NotNull String slotId,
        @NotNull String vehicleType
) {}
