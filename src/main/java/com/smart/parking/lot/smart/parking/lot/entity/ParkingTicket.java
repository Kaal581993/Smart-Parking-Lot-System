package com.smart.parking.lot.smart.parking.lot.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    private String ticketId;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "vehicle_id")
    private VehicleDetails vehicleDetails;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "parking_slot_id")
    private ParkingSlot parkingSlot;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "vehicle_owner_id")
    private VehicleOwner vehicleOwner;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "vehicle_type_id")
    private VehicleType vehicleType;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @NotNull
    private Date checkInTime;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date checkOutTime;

    private Duration parkingDuration;


    private double hourlyRate, baseFee, excessFee, tax, totalFee, pendingDues;
    private PaymentStatus paidStatus;
    private CurrentTicketState status;
    private boolean isActive;
    private boolean isDeleted;
    private Date deletedAt;

    @PrePersist
    public void onCreate() {
        if (ticketId == null) {
            ticketId = UUID.randomUUID().toString();
        }
        if (status == null) {
            status = CurrentTicketState.ACTIVE;
        }
        isDeleted = false;
    }

    @PreUpdate
    public void onUpdate() {
        if (checkInTime != null && checkOutTime != null) {
            parkingDuration = Duration.between(checkInTime.toInstant(), checkOutTime.toInstant());
        }
        if (status == CurrentTicketState.COMPLETED) {
            isActive = false;
        }
        if (isDeleted && deletedAt == null) {
            deletedAt = new Date();
        }
    }
}
