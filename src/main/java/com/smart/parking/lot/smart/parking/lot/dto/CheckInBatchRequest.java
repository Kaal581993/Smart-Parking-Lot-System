package com.smart.parking.lot.smart.parking.lot.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CheckInBatchRequest(
        @NotNull List<String> vehicleIds,
        @NotNull String slotId
) {}
