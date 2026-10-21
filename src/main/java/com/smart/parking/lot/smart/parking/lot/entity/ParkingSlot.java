package com.smart.parking.lot.smart.parking.lot.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
     private String slotId;
     private double allocationDuration;
     private double costPerHour;
     private Date checkInTime;
     private Date checkOutTime;
     private int floorLimit;
     private int floorNo;
     private int floorSlotCapacity;
}
