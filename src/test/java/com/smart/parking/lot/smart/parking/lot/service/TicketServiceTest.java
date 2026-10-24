package com.smart.parking.lot.smart.parking.lot.service;

import com.smart.parking.lot.smart.parking.lot.entity.CurrentTicketState;
import com.smart.parking.lot.smart.parking.lot.entity.ParkingTicket;
import com.smart.parking.lot.smart.parking.lot.entity.PaymentStatus;
import com.smart.parking.lot.smart.parking.lot.repository.ParkingTicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private ParkingTicketRepository parkingTicketRepository;

    @InjectMocks
    private TicketService ticketService;

    @Test
    void processPayment_marksPaidWhenFullyPaid() {
        ParkingTicket ticket = new ParkingTicket();
        ticket.setTicketId("T1");
        ticket.setPendingDues(50.0);
        when(parkingTicketRepository.findById("T1")).thenReturn(Optional.of(ticket));
        when(parkingTicketRepository.save(ticket)).thenReturn(ticket);

        ParkingTicket result = ticketService.processPayment("T1", 50.0);

        assertEquals(PaymentStatus.PAID, result.getPaidStatus());
        assertEquals(0.0, result.getPendingDues());
    }

    @Test
    void processPayment_marksPartiallyPaidWhenPartial() {
        ParkingTicket ticket = new ParkingTicket();
        ticket.setTicketId("T2");
        ticket.setPendingDues(100.0);
        when(parkingTicketRepository.findById("T2")).thenReturn(Optional.of(ticket));
        when(parkingTicketRepository.save(ticket)).thenReturn(ticket);

        ParkingTicket result = ticketService.processPayment("T2", 30.0);

        assertEquals(PaymentStatus.PARTIALLY_PAID, result.getPaidStatus());
        assertEquals(70.0, result.getPendingDues());
    }
}
