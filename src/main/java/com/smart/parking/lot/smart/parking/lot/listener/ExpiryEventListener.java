package com.smart.parking.lot.smart.parking.lot.listener;

import com.smart.parking.lot.smart.parking.lot.observer.ParkingExpiringEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class ExpiryEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    public ExpiryEventListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleParkingExpiring(ParkingExpiringEvent event) {
        messagingTemplate.convertAndSend("/topic/expiry", event);
    }
}
