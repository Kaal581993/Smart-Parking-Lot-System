package com.smart.parking.lot.smart.parking.lot.listener;

import com.smart.parking.lot.smart.parking.lot.observer.PaymentCompletedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    public PaymentEventListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        messagingTemplate.convertAndSend("/topic/payments", event);
    }
}
