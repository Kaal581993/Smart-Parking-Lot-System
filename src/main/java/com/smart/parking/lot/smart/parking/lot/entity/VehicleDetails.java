package com.smart.parking.lot.smart.parking.lot.entity;


import com.smart.parking.lot.smart.parking.lot.entity.util.IdGenerator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "vehicle_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleDetails {

    @Id
    private String vehicleId;
    private String numberPlate;

    @ManyToOne
    private VehicleType vehicleType;

    private String vehicleBrand;
    private String vehicleModel;

    @PrePersist
    public void generateVehicleId() {
        if (vehicleId == null) {
            vehicleId = IdGenerator.INSTANCE.generateVehicleId();
        }
    }
}
