package com.smart.parking.lot.smart.parking.lot.dto;

import java.time.Duration;

public record ParkingTicketResponse(
        String ticketId,
        String vehicleNumber,
        String slotNumber,
        String checkInTime,
        String checkOutTime,
        Duration parkingDuration,
        Double totalFee,
        String paidStatus,
        String status
) {}
