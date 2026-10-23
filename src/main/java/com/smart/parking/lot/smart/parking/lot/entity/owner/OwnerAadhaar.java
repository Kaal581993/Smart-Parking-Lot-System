package com.smart.parking.lot.smart.parking.lot.entity.owner;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    private String aadhaarNo;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private String middleName;

    @NotNull
    private Gender gender;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @NotNull
    private Date dateOfBirth;

    @Column(unique = true)
    @NotNull
    private String email;

    @Column(unique = true)
    private String phone;

    @Transient
    @JsonProperty("addressId")
    @NotNull
    private String addressId;

    @JsonProperty("Address")
    @ManyToOne
    @JoinColumn(name = "addressId")
    @NotNull
    private Address address;

    @NotNull
    private String panNo;
}
