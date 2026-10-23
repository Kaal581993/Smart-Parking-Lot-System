package com.smart.parking.lot.smart.parking.lot.factory;

import com.smart.parking.lot.smart.parking.lot.entity.VehicleType;
import org.springframework.stereotype.Component;

@Component
public class VehicleTypeFactory {

    public VehicleType createTwoWheeler() {
        VehicleType vt = new VehicleType();
        vt.setTypeName("Two Wheeler");
        vt.setHourlyRate(10.0);
        vt.setDailyRate(50.0);
        return vt;
    }

    public VehicleType createCar() {
        VehicleType vt = new VehicleType();
        vt.setTypeName("Car");
        vt.setHourlyRate(20.0);
        vt.setDailyRate(100.0);
        return vt;
    }

    public VehicleType createBus() {
        VehicleType vt = new VehicleType();
        vt.setTypeName("Bus");
        vt.setHourlyRate(50.0);
        vt.setDailyRate(250.0);
        return vt;
    }

    public VehicleType create(String typeName, double hourlyRate, double dailyRate) {
        VehicleType vt = new VehicleType();
        vt.setTypeName(typeName);
        vt.setHourlyRate(hourlyRate);
        vt.setDailyRate(dailyRate);
        return vt;
    }
}
