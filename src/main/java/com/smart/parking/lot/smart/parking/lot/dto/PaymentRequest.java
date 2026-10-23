package com.smart.parking.lot.smart.parking.lot.dto;

import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
        @NotNull Double amount
) {}
