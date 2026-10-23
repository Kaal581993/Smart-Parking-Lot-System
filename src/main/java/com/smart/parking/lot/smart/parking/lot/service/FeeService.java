package com.smart.parking.lot.smart.parking.lot.service;

import com.smart.parking.lot.smart.parking.lot.entity.ParkingTicket;
import com.smart.parking.lot.smart.parking.lot.entity.VehicleType;
import com.smart.parking.lot.smart.parking.lot.strategy.FareStrategy;
import com.smart.parking.lot.smart.parking.lot.strategy.FareStrategyRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class FeeService {

    @Autowired
    private FareStrategyRegistry fareStrategyRegistry;

    public double calculateFee(ParkingTicket ticket) {
        if (ticket.getCheckInTime() == null || ticket.getCheckOutTime() == null) {
            throw new IllegalStateException("Check-in and check-out times are required to calculate fee");
        }
        Duration duration = Duration.between(ticket.getCheckInTime().toInstant(), ticket.getCheckOutTime().toInstant());
        FareStrategy strategy = fareStrategyRegistry.get(ticket.getVehicleType().getTypeName());
        double fare = strategy.calculate(duration, ticket.getVehicleType());
        ticket.setHourlyRate(ticket.getVehicleType().getHourlyRate());
        ticket.setBaseFee(fare);
        ticket.setTotalFee(fare);
        return fare;
    }
}
