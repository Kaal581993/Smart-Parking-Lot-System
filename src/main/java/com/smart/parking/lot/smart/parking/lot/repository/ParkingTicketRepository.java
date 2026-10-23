package com.smart.parking.lot.smart.parking.lot.repository;

import com.smart.parking.lot.smart.parking.lot.entity.CurrentTicketState;
import com.smart.parking.lot.smart.parking.lot.entity.ParkingTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingTicketRepository extends JpaRepository<ParkingTicket, String> {

    List<ParkingTicket> findByParkingSlotIdAndStatus(String slotId, CurrentTicketState status);

    List<ParkingTicket> findByVehicleDetailsIdAndStatus(String vehicleId, CurrentTicketState status);

    boolean existsByParkingSlotIdAndStatus(String slotId, CurrentTicketState status);

    boolean existsByVehicleDetailsIdAndStatus(String vehicleId, CurrentTicketState status);
}
