package com.smart.parking.lot.smart.parking.lot.service;

import com.smart.parking.lot.smart.parking.lot.entity.ParkingTicket;
import com.smart.parking.lot.smart.parking.lot.entity.VehicleType;
import com.smart.parking.lot.smart.parking.lot.strategy.FareStrategy;
import com.smart.parking.lot.smart.parking.lot.strategy.FareStrategyRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeeServiceTest {

    @Mock
    private FareStrategyRegistry fareStrategyRegistry;

    @InjectMocks
    private FeeService feeService;

    @Test
    void calculateFee_usesStrategy() {
        VehicleType vt = new VehicleType();
        vt.setTypeName("Car");
        vt.setHourlyRate(20.0);
        ParkingTicket ticket = new ParkingTicket();
        ticket.setVehicleType(vt);
        ticket.setCheckInTime(new Date(System.currentTimeMillis() - 3600_000));
        ticket.setCheckOutTime(new Date());
        FareStrategy strategy = mock(FareStrategy.class);
        when(fareStrategyRegistry.get("Car")).thenReturn(strategy);
        when(strategy.calculate(any(Duration.class), any(VehicleType.class))).thenReturn(40.0);
        double fare = feeService.calculateFee(ticket);
        assertEquals(40.0, fare);
        assertEquals(40.0, ticket.getTotalFee());
    }
}
