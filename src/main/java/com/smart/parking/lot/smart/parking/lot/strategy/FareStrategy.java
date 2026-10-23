package com.smart.parking.lot.smart.parking.lot.strategy;

import com.smart.parking.lot.smart.parking.lot.entity.VehicleType;

import java.time.Duration;

public interface FareStrategy {
    double calculate(Duration duration, VehicleType vehicleType);
}
