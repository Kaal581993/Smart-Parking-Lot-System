package com.smart.parking.lot.smart.parking.lot.entity.owner;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    private String addressId;
    @NotNull
    private String flatNoHouseNo;

    @NotNull
    private String societyNameAptName;

    @NotNull
    private String areaName;

    @NotNull
    private String townOrCityName;

    @Enumerated(EnumType.STRING)
    @NotNull
    private StateCode stateCode;

    @NotNull
    private String countryName;

    @NotNull
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
