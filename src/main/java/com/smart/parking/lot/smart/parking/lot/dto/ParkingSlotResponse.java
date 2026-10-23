package com.smart.parking.lot.smart.parking.lot.dto;

public record ParkingSlotResponse(
        String slotId,
        String slotNumber,
        Integer floorNo,
        Integer floorLimit,
        Integer floorSlotCapacity,
        Double costPerHour,
        String vehicleTypeAllowed,
        Boolean isAvailable,
        Boolean isActive
) {}
