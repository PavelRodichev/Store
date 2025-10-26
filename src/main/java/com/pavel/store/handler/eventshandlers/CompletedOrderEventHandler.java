package com.pavel.store.handler.eventshandlers;

import com.pavel.store.events.CompletedOrderEvent;

public class CompletedOrderEventHandler implements EventHandler<CompletedOrderEvent>{
    @Override
    public void handle(CompletedOrderEvent event) {

    }

    @Override
    public String getEventType() {
        return "COMPLETED_ORDER";
    }
}
