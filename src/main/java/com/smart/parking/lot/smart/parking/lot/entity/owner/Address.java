package com.smart.parking.lot.smart.parking.lot.entity.owner;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String addressId;
    private String flatNoHouseNo;
    private String societyNameAptName;
    private String areaName;
    private String townOrCityName;

    @Enumerated(EnumType.STRING)
    private StateCode stateCode;
    private String countryName;

    private String pincode;

    public String getFullAddress() {
        return String.format("%s, %s, %s, %s, %s, %s - %s",
                flatNoHouseNo,
                societyNameAptName,
                areaName,
                townOrCityName,
                stateCode != null ? stateCode.getCode() : "",
                countryName,
                pincode);
    }
}
