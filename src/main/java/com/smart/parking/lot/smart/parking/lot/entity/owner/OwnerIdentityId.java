package com.smart.parking.lot.smart.parking.lot.entity.owner;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
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
