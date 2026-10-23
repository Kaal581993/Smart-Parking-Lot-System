package com.smart.parking.lot.smart.parking.lot.service;

import com.smart.parking.lot.smart.parking.lot.entity.CurrentTicketState;
import com.smart.parking.lot.smart.parking.lot.entity.ParkingTicket;
import com.smart.parking.lot.smart.parking.lot.repository.ParkingTicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private ParkingTicketRepository parkingTicketRepository;

    @InjectMocks
    private TicketService ticketService;

    @Test
    void createTicket_setsActiveAndCheckIn() {
        ParkingTicket t = new ParkingTicket();
        when(parkingTicketRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        ParkingTicket saved = ticketService.createTicket(t);
        assertEquals(CurrentTicketState.ACTIVE, saved.getStatus());
        assertTrue(saved.isActive());
        assertNotNull(saved.getCheckInTime());
    }

    @Test
    void isSlotOccupied_delegatesToRepository() {
        when(parkingTicketRepository.existsByParkingSlot_SlotIdAndStatus("S1", CurrentTicketState.ACTIVE)).thenReturn(true);
        assertTrue(ticketService.isSlotOccupied("S1"));
    }

    @Test
    void isVehicleAlreadyParked_delegatesToRepository() {
        when(parkingTicketRepository.existsByVehicleDetails_VehicleIdAndStatus("V1", CurrentTicketState.ACTIVE)).thenReturn(false);
        assertFalse(ticketService.isVehicleAlreadyParked("V1"));
    }

    @Test
    void getTicketById_throwsWhenNotFound() {
        when(parkingTicketRepository.findById("X")).thenReturn(Optional.empty());
        assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> ticketService.getTicketById("X"));
    }

    @Test
    void scanOverstayed_returnsOverstayedTickets() {
        ParkingTicket t = new ParkingTicket();
        t.setCheckInTime(new java.util.Date(System.currentTimeMillis() - 10_000_000));
        t.setStatus(CurrentTicketState.ACTIVE);
        com.smart.parking.lot.smart.parking.lot.entity.ParkingSlot slot = new com.smart.parking.lot.smart.parking.lot.entity.ParkingSlot();
        slot.setAllocationDuration(java.time.Duration.ofMinutes(1));
        t.setParkingSlot(slot);
        when(parkingTicketRepository.findAll()).thenReturn(List.of(t));
        assertEquals(1, ticketService.scanOverstayed().size());
    }
}
