package com.smart.parking.lot.smart.parking.lot.dto;

import java.util.List;

public record CheckInBatchResponse(
        List<String> added,
        List<String> alreadyParked,
        List<String> notFoundVehicles,
        List<String> unavailableSlots
) {}
