package com.smart.parking.lot.smart.parking.lot.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.smart.parking.lot.smart.parking.lot.entity.owner.Address;
import com.smart.parking.lot.smart.parking.lot.entity.owner.LicenseNo;
import com.smart.parking.lot.smart.parking.lot.entity.owner.OwnerAadhaar;
import com.smart.parking.lot.smart.parking.lot.entity.owner.OwnerIdentityId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "vehicle_owner")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleOwner {
     private String vehicleOwnerName;
     private int vehicleOwnerPhone;
     private String vehicleOwnerEmail;

     @EmbeddedId
     private OwnerIdentityId ownerIdentityId;

     @OneToOne
     @JoinColumn(name = "owner_aadhar_no")
     @MapsId("aadharNo")
     private OwnerAadhaar aadhaar;  // The relationship

     @OneToOne
     @JoinColumn(name = "owner_license_no")
     @MapsId("licenseNo")
     private LicenseNo licenseNo;  // The relationship

     private String vehicleOwnerPanNo;

     @JsonFormat(pattern = "dd/MM/yyyy")
     private Date vehicleOwnerDOB;


     @OneToOne
     @JoinColumn(name = "owner_address")
     private Address vehicleOwnerAddress;

     private Date licenseExpiryDate;

     private double vehicleOwnerLedgerBalance;
     private double vehicleOwnerDues;

}
