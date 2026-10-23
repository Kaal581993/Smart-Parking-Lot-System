package com.smart.parking.lot.smart.parking.lot.dto;

import jakarta.validation.constraints.NotNull;

public record VehicleOwnerRequest(
        @NotNull String vehicleOwnerName,
        @NotNull Integer vehicleOwnerPhone,
        @NotNull String vehicleOwnerEmail,
        @NotNull String aadhaarNo,
        @NotNull String licenseId,
        @NotNull String vehicleOwnerPanNo,
        @NotNull String vehicleOwnerDOB,
        @NotNull String addressId,
        @NotNull String licenseExpiryDate,
        @NotNull Double vehicleOwnerLedgerBalance,
        @NotNull Double vehicleOwnerDues
) {}
