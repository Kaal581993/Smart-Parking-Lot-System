package com.smart.parking.lot.smart.parking.lot.scheduler;

import com.smart.parking.lot.smart.parking.lot.entity.CurrentTicketState;
import com.smart.parking.lot.smart.parking.lot.entity.ParkingTicket;
import com.smart.parking.lot.smart.parking.lot.observer.ParkingExpiringEvent;
import com.smart.parking.lot.smart.parking.lot.repository.ParkingTicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Component
public class ExpiryNotificationScheduler {

    @Autowired
    private ParkingTicketRepository parkingTicketRepository;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Scheduled(fixedRate = 60000)
    public void checkExpiringTickets() {
        List<ParkingTicket> active = parkingTicketRepository.findAll().stream()
                .filter(t -> t.getStatus() == CurrentTicketState.ACTIVE)
                .toList();
        Instant now = Instant.now();
        for (ParkingTicket t : active) {
            if (t.getCheckInTime() == null) continue;
            Duration allowed = t.getParkingSlot().getAllocationDuration();
            if (allowed == null) allowed = Duration.ofHours(2);
            Instant expiry = t.getCheckInTime().toInstant().plus(allowed);
            long remaining = Duration.between(now, expiry).toMinutes();
            if (remaining <= 15 && remaining >= 0) {
                String vehicleNumber = t.getVehicleDetails() != null ? t.getVehicleDetails().getNumberPlate() : "N/A";
                publisher.publishEvent(new ParkingExpiringEvent(this, t.getTicketId(), vehicleNumber, remaining));
            }
        }
    }
}
