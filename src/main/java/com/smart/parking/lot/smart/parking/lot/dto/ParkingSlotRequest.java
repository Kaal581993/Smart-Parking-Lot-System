package com.smart.parking.lot.smart.parking.lot.dto;

import jakarta.validation.constraints.NotNull;

public record ParkingSlotRequest(
        @NotNull String slotNumber,
        @NotNull Integer floorNo,
        @NotNull Integer floorLimit,
        @NotNull Integer floorSlotCapacity,
        @NotNull Double costPerHour,
        String vehicleTypeAllowed
) {}
