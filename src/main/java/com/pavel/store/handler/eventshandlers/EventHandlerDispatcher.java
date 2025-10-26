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
    private Map<String, EventHandler<EventSource>> mapHandlers;

    // Автоматически ищем бины EvenHandler и преобразуем в список для инициализации mapHandlers
    @Autowired
    public EventHandlerDispatcher(List<EventHandler<EventSource>> eventHandlerList) {
        this.mapHandlers = eventHandlerList.stream()
                .collect(Collectors.toMap(EventHandler::getEventType, a -> a));
    }

    // Метод для выбора хэндлера для определенного евента
    public void dispatch(EventSource eventSource) {
        EventHandler<EventSource> eventHandler = mapHandlers.get(eventSource.getEvent());
        if (eventHandler != null) {
            eventHandler.handle(eventSource);
        } else {
            throw new IllegalArgumentException("Handler for event: " + eventSource.getEvent() + "not found");
        }
    }
}
