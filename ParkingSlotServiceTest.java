package com.smart.parking.lot.smart.parking.lot.service;

import com.smart.parking.lot.smart.parking.lot.dto.CheckInRequest;
import com.smart.parking.lot.smart.parking.lot.entity.ParkingSlot;
import com.smart.parking.lot.smart.parking.lot.entity.ParkingTicket;
import com.smart.parking.lot.smart.parking.lot.repository.ParkingSlotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingSlotServiceTest {

    @Mock
    private ParkingSlotRepository parkingSlotRepository;

    @Mock
    private TicketService ticketService;

    @Mock
    private SlotService slotService;

    @InjectMocks
    private ParkingSlotService parkingSlotService;

    @Test
    void checkIn_throwsWhenSlotUnavailable() {
        ParkingSlot slot = new ParkingSlot();
        slot.setSlotId("S1");
        slot.setAvailable(false);
        when(slotService.getSlotById("S1")).thenReturn(slot);
        CheckInRequest req = new CheckInRequest("V1", "S1", null);
        assertThrows(IllegalStateException.class, () -> parkingSlotService.checkIn(req));
    }

    @Test
    void checkIn_throwsWhenSlotOccupied() {
        ParkingSlot slot = new ParkingSlot();
        slot.setSlotId("S1");
        slot.setAvailable(true);
        when(slotService.getSlotById("S1")).thenReturn(slot);
        when(ticketService.isSlotOccupied("S1")).thenReturn(true);
        CheckInRequest req = new CheckInRequest("V1", "S1", null);
        assertThrows(IllegalStateException.class, () -> parkingSlotService.checkIn(req));
    }

    @Test
    void checkIn_throwsWhenVehicleAlreadyParked() {
        ParkingSlot slot = new ParkingSlot();
        slot.setSlotId("S1");
        slot.setAvailable(true);
        when(slotService.getSlotById("S1")).thenReturn(slot);
        when(ticketService.isSlotOccupied("S1")).thenReturn(false);
        when(ticketService.isVehicleAlreadyParked("V1")).thenReturn(true);
        CheckInRequest req = new CheckInRequest("V1", "S1", null);
        assertThrows(IllegalStateException.class, () -> parkingSlotService.checkIn(req));
    }
}
