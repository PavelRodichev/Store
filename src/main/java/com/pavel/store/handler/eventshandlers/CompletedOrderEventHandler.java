package com.pavel.store.handler.eventshandlers;

import com.pavel.store.events.CancelledOrderEvent;
import com.pavel.store.events.CompletedOrderEvent;
import com.pavel.store.events.EventSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CompletedOrderEventHandler implements EventHandler {


    @Override
    public void handle(EventSource event) {
        CompletedOrderEvent orderEvent = (CompletedOrderEvent) event;
        log.info(" WORKING HANDLER: Order {} for user {}", orderEvent.getOrderId(), orderEvent.getUserId());
    }

    @Override
    public String getEventType() {
        return "COMPLETED_ORDER";
    }


}
