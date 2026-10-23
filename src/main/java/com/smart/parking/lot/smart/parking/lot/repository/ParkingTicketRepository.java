package com.smart.parking.lot.smart.parking.lot.repository;

import com.smart.parking.lot.smart.parking.lot.entity.CurrentTicketState;
import com.smart.parking.lot.smart.parking.lot.entity.ParkingTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingTicketRepository extends JpaRepository<ParkingTicket, String> {

    List<ParkingTicket> findByParkingSlot_SlotIdAndStatus(String slotId, CurrentTicketState status);

    List<ParkingTicket> findByParkingSlot_SlotId(String slotId);

    List<ParkingTicket> findByVehicleDetails_VehicleIdAndStatus(String vehicleId, CurrentTicketState status);

    boolean existsByParkingSlot_SlotIdAndStatus(String slotId, CurrentTicketState status);

    boolean existsByVehicleDetails_VehicleIdAndStatus(String vehicleId, CurrentTicketState status);
}
