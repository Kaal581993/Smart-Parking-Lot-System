package com.smart.parking.lot.smart.parking.lot.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "parking_slot")
@Data
@NoArgsConstructor(onConstructor_ = @JsonCreator)
@AllArgsConstructor
@Builder
public class ParkingSlot {

     @Id
     @GeneratedValue(strategy = GenerationType.AUTO)
     @NotNull
     private String slotId;

     @Version
     private Long version;

     @NotNull
     private String slotNumber;

     private java.time.Duration allocationDuration;

     @ManyToOne
     @JoinColumn(name = "vehicle_type_allowed_id")
     private VehicleType vehicleTypeAllowed;

     private double costPerHour;

     @JsonFormat(pattern = "dd/MM/yyyy")
     private Date checkInTime;

     @JsonFormat(pattern = "dd/MM/yyyy")
     private Date checkOutTime;

     @NotNull
     private int floorNo;

     @NotNull
     private int floorLimit;

     @NotNull
     private int floorSlotCapacity;

     private boolean isAvailable = true;
     private boolean isActive = true;
     private boolean isDeleted = false;

     @JsonFormat(pattern = "dd/MM/yyyy")
     private Date deletedAt;

     @OneToMany(mappedBy = "parkingSlot", cascade = CascadeType.ALL, orphanRemoval = false)
     @JsonIgnore
     private List<ParkingTicket> tickets;
}
