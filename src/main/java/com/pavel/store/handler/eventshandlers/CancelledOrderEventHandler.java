package com.pavel.store.handler.eventshandlers;

import com.pavel.store.events.CancelledOrderEvent;
import com.pavel.store.events.EventSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CancelledOrderEventHandler implements EventHandler {


    @Override
    public void handle(EventSource event) {
        CancelledOrderEvent orderEvent = (CancelledOrderEvent) event;
        log.info(" WORKING HANDLER: Order {} reason: {}", orderEvent.getOrderId(), orderEvent.getReason());
    }

    @Override
    public String getEventType() {
        return "CANCELLED_ORDER";
    }
}
