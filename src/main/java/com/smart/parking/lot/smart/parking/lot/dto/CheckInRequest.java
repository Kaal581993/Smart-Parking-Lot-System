package com.smart.parking.lot.smart.parking.lot.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

public record CheckInRequest(
        @NotNull String vehicleId,
        String slotId,
        String allocationStrategy
) {}
