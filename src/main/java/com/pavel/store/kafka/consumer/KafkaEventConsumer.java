package com.pavel.store.kafka.consumer;


import com.fasterxml.jackson.databind.ObjectMapper;

import com.pavel.store.events.EventSource;
import com.pavel.store.handler.eventshandlers.EventHandlerDispatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaEventConsumer {
    private final ObjectMapper objectMapper;
    private final EventHandlerDispatcher eventHandlerDispatcher;


    @KafkaListener(topics = "events", groupId = "${kafka.consumer.group-id:default-group}", containerFactory = "kafkaListenerContainerFactory")
    public void consume(String message) {
        System.out.println("📨 RAW MESSAGE: " + message);
        try {
            EventSource event = objectMapper.readValue(message, EventSource.class);
            System.out.println("✅ Event type: " + event.getEvent());
            eventHandlerDispatcher.dispatch(event);
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
}
