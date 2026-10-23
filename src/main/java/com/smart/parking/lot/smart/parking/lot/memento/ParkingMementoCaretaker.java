package com.smart.parking.lot.smart.parking.lot.memento;

import java.util.ArrayDeque;
import java.util.Deque;

public class ParkingMementoCaretaker {
    private final Deque<ParkingTicketMemento> stack = new ArrayDeque<>();

    public void push(ParkingTicketMemento memento) {
        stack.push(memento);
    }

    public ParkingTicketMemento pop() {
        return stack.isEmpty() ? null : stack.pop();
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }
}
