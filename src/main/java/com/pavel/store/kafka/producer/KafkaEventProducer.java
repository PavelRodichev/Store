package com.pavel.store.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pavel.store.dto.request.EmailMessage;
import com.pavel.store.events.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendEmailMessage(EmailMessage emailMessage) {

        try {
            log.info("Working Kafka Producer...");
            String messageJson = objectMapper.writeValueAsString(emailMessage);
            kafkaTemplate.send("email-topic", emailMessage.getId(), messageJson);

            log.info("message send to topic");
        } catch (Exception e) {
            log.error("Error sending email message to Kafka", e);
        }

    }

}
