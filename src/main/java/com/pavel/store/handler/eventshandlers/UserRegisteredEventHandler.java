package com.pavel.store.handler.eventshandlers;


import com.pavel.store.dto.request.EmailMessage;
import com.pavel.store.events.EventSource;
import com.pavel.store.events.UserRegisteredEvent;
import com.pavel.store.kafka.producer.KafkaEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserRegisteredEventHandler implements EventHandler {

    private final KafkaEventProducer kafkaEventProducer;

    @Override
    public void handle(EventSource event) {
        UserRegisteredEvent userRegisteredEvent = (UserRegisteredEvent) event;

        log.info("🎯 WORKING HANDLER: UserRegistration {} for user {}", event.getClass().getSimpleName(), userRegisteredEvent.getUsername());

        Map<String, Object> variables = new HashMap<>();
        variables.put("firstName", userRegisteredEvent.getFirstName());
        variables.put("lastName", userRegisteredEvent.getLastName());

        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setId(UUID.randomUUID().toString());
        emailMessage.setTo(userRegisteredEvent.getEmail());
        emailMessage.setSubject("Добро пожаловать в наш магазин!");
        emailMessage.setTimestamp(Instant.now());
        emailMessage.setVariables(variables);
        emailMessage.setTemplateName("welcome-email");

        kafkaEventProducer.sendEmailMessage(emailMessage);

    }

    @Override
    public String getEventType() {
        return "USER_REGISTERED";
    }
}
