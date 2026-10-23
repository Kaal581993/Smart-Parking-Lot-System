package com.smart.parking.lot.smart.parking.lot.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;
import java.util.Date;

@Entity
@Table(name = "parking_slot")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingSlot {

     @Id
     @GeneratedValue(strategy = GenerationType.AUTO)
     @NotNull
     private String slotId;

     @NotNull
     private String slotNumber;

     private double costPerHour;

     @JsonFormat(pattern = "dd/MM/yyyy")
     private Date checkInTime;

     @JsonFormat(pattern = "dd/MM/yyyy")
     private Date checkOutTime;

     private int floorLimit;
     private int floorNo;

     private int floorSlotCapacity;

     private boolean isAvailable;
     private boolean isDeleted;
}
