package com.smart.parking.lot.smart.parking.lot.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.smart.parking.lot.smart.parking.lot.entity.util.IdGenerator;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "vehicle_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleDetails {

    @Id
    @NotNull
    private String vehicleId;

    @Column(unique = true)
    @jakarta.validation.constraints.Pattern(regexp = "^[A-Z]{2}[0-9]{2}[A-Z]{1,2}[0-9]{4}$", message = "Invalid Indian number plate format. Expected: SS00XX0000 (e.g., MH01AB1234)")
    private String numberPlate;

    @ManyToOne
    private VehicleType vehicleType;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "aadharNo"),
            @JoinColumn(name = "licenseNo")
    })
    private VehicleOwner owner;


    private String vehicleBrand;
    private String vehicleModel;

    private boolean isDeleted = false;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date deletedAt;

    @PrePersist
    public void generateVehicleId() {
        if (vehicleId == null) {
            vehicleId = IdGenerator.INSTANCE.generateVehicleId();
        }
    }
}
