package com.pavel.store.handler.eventshandlers;

import com.pavel.store.events.CreateOrderEvent;
import org.springframework.stereotype.Component;

@Component
public class CreateOrderEventHandler implements EventHandler<CreateOrderEvent> {


    @Override
    public void handle(CreateOrderEvent event) {

    }

    @Override
    public String getEventType() {
        return "CREATE_ORDER";
    }
}
