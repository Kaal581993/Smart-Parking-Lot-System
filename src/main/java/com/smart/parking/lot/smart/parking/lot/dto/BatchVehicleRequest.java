package com.smart.parking.lot.smart.parking.lot.dto;

import com.smart.parking.lot.smart.parking.lot.entity.VehicleDetails;

import java.util.List;

public record BatchVehicleRequest(
        List<VehicleDetails> vehicles
) {}
