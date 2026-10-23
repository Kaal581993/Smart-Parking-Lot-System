package com.smart.parking.lot.smart.parking.lot.strategy;

import com.smart.parking.lot.smart.parking.lot.factory.VehicleTypeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class FareStrategyConfig {

    @Autowired
    private FareStrategyRegistry registry;

    @PostConstruct
    public void init() {
        registry.register("Two Wheeler", new TwoWheelerFareStrategy());
        registry.register("Car", new CarFareStrategy());
        registry.register("Bus", new BusFareStrategy());
        registry.register("Truck", new BusFareStrategy());
        registry.register("Mini Bus", new CarFareStrategy());
        registry.register("SUV", new CarFareStrategy());
        registry.register("Electric Two Wheeler", new TwoWheelerFareStrategy());
        registry.register("Electric Car", new CarFareStrategy());
    }
}
