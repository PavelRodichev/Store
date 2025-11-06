package com.pavel.store.kafka.consumer;


import com.fasterxml.jackson.databind.ObjectMapper;

import com.pavel.store.events.EventSource;
import com.pavel.store.handler.eventshandlers.EventHandlerDispatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaEventConsumer {
    private final ObjectMapper objectMapper;
    private final EventHandlerDispatcher eventHandlerDispatcher;


    @KafkaListener(topics = "events", groupId = "${kafka.consumer.group-id:default-group}", containerFactory = "kafkaListenerContainerFactory")
    public void consume(EventSource event) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        try {
            SecurityContextHolder.setContext(context);

            log.info("📨 Received event type: {}", event.getEvent());
            log.info("✅ Event details: {}", event);

            eventHandlerDispatcher.dispatch(event);

        } catch (Exception e) {
            log.error("❌ Error processing event: {}", e.getMessage(), e);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
