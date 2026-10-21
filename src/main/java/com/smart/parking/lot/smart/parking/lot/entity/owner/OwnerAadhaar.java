package com.smart.parking.lot.smart.parking.lot.entity.owner;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "owner_aadhaar")
public class OwnerAadhaar {
    @Id
    @JsonProperty("aadhaarNo")
    private String aadhaarNo;
    private String firstName;
    private String lastName;
    private String middleName;
    private Gender gender;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date dateOfBirth;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phone;

    @Transient
    @JsonProperty("addressId")
    private String addressId;

    @JsonProperty("Address")
    @ManyToOne
    @JoinColumn(name = "addressId")
    private Address address;
    private String panNo;
}
