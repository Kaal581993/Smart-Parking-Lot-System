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
@Table(name = "license_no")
public class LicenseNo {


    @Id
    private String licenseId;  // Store "MH01AB1234" as primary key


    @PrePersist
    public void generateLicenseId() {
        if (licenseId == null) {
            licenseId = getFullLicenseNumber().replace(" ", "");
        }
    }


    @Enumerated(EnumType.STRING)
    @NotNull
    private StateCode stateCode;// SS: 2 letters (e.g., MH, DL)

    @NotNull
    private String rtoDistrictCode;    // RR: 2 digits (e.g., 01, 02)

    @NotNull
    private String seriesCode;         // LL: 1-2 letters excluding I and O (e.g., AB, CD)

    @NotNull
    private String serialNumber;       // NNNN: 4-digit number (0001-9999)

    public String getFullLicenseNumber() {
        return String.format("%s %s %s %s",
                stateCode != null ? stateCode.getCode() : "",
                rtoDistrictCode,
                seriesCode,
                serialNumber);
    }
}
