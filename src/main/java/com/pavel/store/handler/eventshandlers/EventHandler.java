package com.pavel.store.handler.eventshandlers;

import com.pavel.store.events.EventSource;

public interface EventHandler<T extends EventSource> {
    void handle(T event);

    String getEventType();

}
