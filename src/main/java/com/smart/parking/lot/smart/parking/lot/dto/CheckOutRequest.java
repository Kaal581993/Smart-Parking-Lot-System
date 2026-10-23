package com.smart.parking.lot.smart.parking.lot.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

public record CheckOutRequest(
        @NotNull String ticketId
) {}
