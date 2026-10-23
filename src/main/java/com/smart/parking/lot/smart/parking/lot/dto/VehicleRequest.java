package com.smart.parking.lot.smart.parking.lot.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record VehicleRequest(
        @NotNull String numberPlate,
        @NotNull String vehicleType,
        String vehicleBrand,
        String vehicleModel
) {}
