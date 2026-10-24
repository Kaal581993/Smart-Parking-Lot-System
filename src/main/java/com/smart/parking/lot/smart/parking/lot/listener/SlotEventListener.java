package com.smart.parking.lot.smart.parking.lot.listener;

import com.smart.parking.lot.smart.parking.lot.observer.SlotAvailableEvent;
import com.smart.parking.lot.smart.parking.lot.observer.SlotOccupiedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class SlotEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    public SlotEventListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleSlotAvailable(SlotAvailableEvent event) {
        messagingTemplate.convertAndSend("/topic/slots", event);
    }

    @EventListener
    public void handleSlotOccupied(SlotOccupiedEvent event) {
        messagingTemplate.convertAndSend("/topic/slots", event);
    }
}
