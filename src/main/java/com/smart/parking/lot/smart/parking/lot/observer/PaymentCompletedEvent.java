package com.smart.parking.lot.smart.parking.lot.observer;

import org.springframework.context.ApplicationEvent;

public class PaymentCompletedEvent extends ApplicationEvent {
    private final String ticketId;
    private final double totalFee;

    public PaymentCompletedEvent(String ticketId, double totalFee) {
        super(ticketId);
        this.ticketId = ticketId;
        this.totalFee = totalFee;
    }

    public String getTicketId() { return ticketId; }
    public double getTotalFee() { return totalFee; }
}
