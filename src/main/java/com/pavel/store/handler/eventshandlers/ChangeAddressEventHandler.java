package com.pavel.store.handler.eventshandlers;

import com.pavel.store.events.ChangeAddressEvent;
import com.pavel.store.events.EventSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ChangeAddressEventHandler implements EventHandler {
    @Override
    public void handle(EventSource event) {
        ChangeAddressEvent orderEvent = (ChangeAddressEvent) event;
        log.info("WORKING HANDLER: Order {} new address {}", orderEvent.getOrderId(), orderEvent.getNewAddress());
    }

    @Override
    public String getEventType() {
        return "CHANGE_ADDRESS";
    }
}
