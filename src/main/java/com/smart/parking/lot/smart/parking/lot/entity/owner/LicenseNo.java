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
    private StateCode stateCode;          // SS: 2 letters (e.g., MH, DL)
    private String rtoDistrictCode;    // RR: 2 digits (e.g., 01, 02)
    private String seriesCode;         // LL: 1-2 letters excluding I and O (e.g., AB, CD)
    private String serialNumber;       // NNNN: 4-digit number (0001-9999)

    public String getFullLicenseNumber() {
        return String.format("%s %s %s %s", stateCode, rtoDistrictCode, seriesCode, serialNumber);
    }
}
