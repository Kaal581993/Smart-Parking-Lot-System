package com.smart.parking.lot.smart.parking.lot.strategy;

import com.smart.parking.lot.smart.parking.lot.entity.VehicleType;

import java.time.Duration;

public class TwoWheelerFareStrategy implements FareStrategy {
    @Override
    public double calculate(Duration duration, VehicleType vehicleType) {
        long hours = Math.max(1, duration.toHours());
        return hours * vehicleType.getHourlyRate();
    }
}
