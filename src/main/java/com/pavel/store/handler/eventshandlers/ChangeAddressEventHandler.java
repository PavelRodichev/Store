package com.pavel.store.handler.eventshandlers;

import com.pavel.store.events.ChangeAddressEvent;
import org.springframework.stereotype.Component;

@Component
public class ChangeAddressEventHandler implements EventHandler<ChangeAddressEvent> {


    @Override
    public void handle(ChangeAddressEvent event) {

    }

    @Override
    public String getEventType() {
        return "CHANGE_ADDRESS";
    }
}
