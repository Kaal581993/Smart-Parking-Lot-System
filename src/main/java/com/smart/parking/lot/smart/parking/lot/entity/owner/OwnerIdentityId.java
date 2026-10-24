package com.smart.parking.lot.smart.parking.lot.entity.owner;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OwnerIdentityId implements Serializable {
    private String aadharNo;  // Just the ID, not the entity
    private String licenseNo; // Just the ID, not the entity

    // Must implement equals() and hashCode()

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OwnerIdentityId that = (OwnerIdentityId) o;
        return Objects.equals(aadharNo, that.aadharNo) &&
                Objects.equals(licenseNo, that.licenseNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aadharNo, licenseNo);
    }
}
