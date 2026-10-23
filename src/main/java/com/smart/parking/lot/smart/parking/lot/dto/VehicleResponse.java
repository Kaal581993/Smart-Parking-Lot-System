package com.smart.parking.lot.smart.parking.lot.dto;

public record VehicleResponse(
        String vehicleId,
        String numberPlate,
        String vehicleType,
        String vehicleBrand,
        String vehicleModel,
        String ownerAadhaarNo
) {}
