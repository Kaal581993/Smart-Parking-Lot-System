package com.smart.parking.lot.smart.parking.lot.entity;


import com.smart.parking.lot.smart.parking.lot.entity.util.IdGenerator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vehicle_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleType {

    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    private String typeId;
    private String typeName;
    private double hourlyRate;
    private double dailyRate;

    @PrePersist
    public void generateTypeId() {
        if (typeId == null) {
            typeId = IdGenerator.INSTANCE.generateId("VT", 4);
        }
    }

    public double calculateCharge(int hours) {
        if (hours <= 0) {
            return 0;
        }
        if (hours < 24) {
            return hourlyRate * hours;
        }
        int days = hours / 24;
        int remainingHours = hours % 24;
        return (dailyRate * days) + (hourlyRate * remainingHours);
    }

    // Static constants for predefined vehicle types
    public static VehicleType twoWheeler() {
        return VehicleType.builder()
                .typeName("Two Wheeler")
                .hourlyRate(10.0)
                .dailyRate(50.0)
                .build();
    }

    public static VehicleType threeWheeler() {
        return VehicleType.builder()
                .typeName("Three Wheeler")
                .hourlyRate(15.0)
                .dailyRate(75.0)
                .build();
    }

    public static VehicleType carHatchback() {
        return VehicleType.builder()
                .typeName("Car Hatchback")
                .hourlyRate(20.0)
                .dailyRate(100.0)
                .build();
    }

    public static VehicleType carSedan() {
        return VehicleType.builder()
                .typeName("Car Sedan")
                .hourlyRate(25.0)
                .dailyRate(120.0)
                .build();
    }

    public static VehicleType suv() {
        return VehicleType.builder()
                .typeName("SUV")
                .hourlyRate(30.0)
                .dailyRate(150.0)
                .build();
    }

    public static VehicleType miniBus() {
        return VehicleType.builder()
                .typeName("Mini Bus")
                .hourlyRate(40.0)
                .dailyRate(200.0)
                .build();
    }

    public static VehicleType bus() {
        return VehicleType.builder()
                .typeName("Bus")
                .hourlyRate(50.0)
                .dailyRate(250.0)
                .build();
    }

    public static VehicleType truck() {
        return VehicleType.builder()
                .typeName("Truck")
                .hourlyRate(60.0)
                .dailyRate(300.0)
                .build();
    }

    public static VehicleType electricTwoWheeler() {
        return VehicleType.builder()
                .typeName("Electric Two Wheeler")
                .hourlyRate(8.0)
                .dailyRate(40.0)
                .build();
    }

    public static VehicleType electricCar() {
        return VehicleType.builder()
                .typeName("Electric Car")
                .hourlyRate(18.0)
                .dailyRate(90.0)
                .build();
    }
}
