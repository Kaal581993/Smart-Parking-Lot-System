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
import jakarta.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "vehicle_owner")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleOwner {

     @NotNull
     private String vehicleOwnerName;
     @NotNull
     private int vehicleOwnerPhone;
     @NotNull
     private String vehicleOwnerEmail;

     @EmbeddedId
     private OwnerIdentityId ownerIdentityId;

     @OneToOne
     @JoinColumn(name = "owner_aadhar_no")
     @MapsId("aadharNo")
     @NotNull
     private OwnerAadhaar aadhaar;  // The relationship

     @OneToOne
     @JoinColumn(name = "owner_license_no")
     @MapsId("licenseNo")
     @NotNull
     private LicenseNo licenseNo;  // The relationship

     @NotNull
     private String vehicleOwnerPanNo;

     @JsonFormat(pattern = "dd/MM/yyyy")
     @NotNull
     private Date vehicleOwnerDOB;


     @OneToOne
     @JoinColumn(name = "owner_address")
     @NotNull
     private Address vehicleOwnerAddress;

     @NotNull
     private Date licenseExpiryDate;

     @NotNull
     private double vehicleOwnerLedgerBalance;

     @NotNull
     private double vehicleOwnerDues;

}
