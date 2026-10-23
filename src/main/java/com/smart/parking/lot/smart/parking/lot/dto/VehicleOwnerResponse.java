package com.smart.parking.lot.smart.parking.lot.dto;

public record VehicleOwnerResponse(
        String aadhaarNo,
        String licenseId,
        String vehicleOwnerName,
        Integer vehicleOwnerPhone,
        String vehicleOwnerEmail,
        String vehicleOwnerPanNo,
        String vehicleOwnerDOB,
        String addressId,
        String licenseExpiryDate,
        Double vehicleOwnerLedgerBalance,
        Double vehicleOwnerDues
) {}
