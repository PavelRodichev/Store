package com.pavel.store.handler.eventshandlers;

import com.pavel.store.events.EventSource;

public interface EventHandler {

    void handle(EventSource event);

    String getEventType();

}
