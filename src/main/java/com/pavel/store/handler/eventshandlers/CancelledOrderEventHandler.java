package com.pavel.store.handler.eventshandlers;

import com.pavel.store.events.CancelledOrderEvent;
import org.springframework.stereotype.Component;

@Component
public class CancelledOrderEventHandler implements EventHandler<CancelledOrderEvent> {

    @Override
    public void handle(CancelledOrderEvent event) {

    }

    @Override
    public String getEventType() {
        return "CANCELLED_ORDER";
    }

}
