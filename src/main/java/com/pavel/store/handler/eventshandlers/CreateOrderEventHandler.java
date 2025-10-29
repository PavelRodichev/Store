package com.pavel.store.handler.eventshandlers;

import com.pavel.store.events.CreateOrderEvent;
import com.pavel.store.events.EventSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CreateOrderEventHandler implements EventHandler {


    @Override
    public void handle(EventSource event) {
        CreateOrderEvent orderEvent = (CreateOrderEvent) event;
        log.info("🎯 WORKING HANDLER: Order {} for user {}", orderEvent.getOrderId(), orderEvent.getUserId());
        log.info("📦 Items: {}, Address: {}", orderEvent.getItems(), orderEvent.getAddress());
    }

    @Override
    public String getEventType() {
        return "CREATE_ORDER";
    }
}
