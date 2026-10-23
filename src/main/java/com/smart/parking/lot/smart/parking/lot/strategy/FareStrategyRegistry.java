package com.smart.parking.lot.smart.parking.lot.strategy;

import com.smart.parking.lot.smart.parking.lot.entity.VehicleType;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class FareStrategyRegistry {
    private final Map<String, FareStrategy> strategies = new ConcurrentHashMap<>();

    public void register(String vehicleTypeName, FareStrategy strategy) {
        strategies.put(vehicleTypeName.toUpperCase(), strategy);
    }

    public FareStrategy get(String vehicleTypeName) {
        FareStrategy strategy = strategies.get(vehicleTypeName.toUpperCase());
        if (strategy == null) {
            throw new IllegalArgumentException("No fare strategy registered for vehicle type: " + vehicleTypeName);
        }
        return strategy;
    }
}
