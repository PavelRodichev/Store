package com.pavel.store.handler.eventshandlers;

import com.pavel.store.events.EventSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Диспетчер для обработки разных евентов
@Component
public class EventHandlerDispatcher {

    private Map<String, EventHandler> mapHandlers;

    @Autowired
    public EventHandlerDispatcher(List<EventHandler> eventHandlerList) {
        System.out.println("=== EVENT HANDLER DISPATCHER INIT ===");
        System.out.println("Found handlers: " + eventHandlerList.size());


        eventHandlerList.forEach(handler -> {
            System.out.println("Handler class: " + handler.getClass().getSimpleName());
            System.out.println("Handler event type: " + handler.getEventType());
        });

        this.mapHandlers = eventHandlerList.stream()
                .collect(Collectors.toMap(EventHandler::getEventType, a -> a));

        System.out.println("Registered event types: " + mapHandlers.keySet());
        System.out.println("=====================================");
    }

    public void dispatch(EventSource eventSource) {
        String eventType = eventSource.getEvent();
        System.out.println("Dispatching event: " + eventType);

        EventHandler eventHandler = mapHandlers.get(eventType);
        if (eventHandler != null) {
            eventHandler.handle(eventSource);
        } else {
            System.out.println("❌ Available handlers: " + mapHandlers.keySet());
            throw new IllegalArgumentException("Handler for event: " + eventType + " not found");
        }
    }
}
