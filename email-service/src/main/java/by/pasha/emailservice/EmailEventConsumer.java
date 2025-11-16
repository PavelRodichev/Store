package by.pasha.emailservice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailEventConsumer {

    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${app.kafka.topics.user-registered:user-registered-topic}")
    public void consumeUserRegisteredEvent(String message) {
        log.info("📨 Received raw message from Kafka: {}", message);

        try {
            // Парсим JSON в общую структуру
            JsonNode jsonNode = objectMapper.readTree(message);

            // Извлекаем поля ТОЧНО как в вашем EmailMessage
            String to = extractField(jsonNode, "to");
            String subject = extractField(jsonNode, "subject");
            String templateName = extractField(jsonNode, "templateName");

            // Парсим variables
            Map<String, Object> variables = extractVariables(jsonNode);

            log.info("""
                    ✅ Successfully parsed email data:
                    To: {}
                    Subject: {}
                    Template: {}
                    Variables: {}
                    """, to, subject, templateName, variables);

            // Отправляем email
            emailService.sendHtmlEmail(to, subject, templateName, variables);
            log.info("✉️ Email sent successfully to: {}", to);

        } catch (Exception e) {
            log.error("❌ Failed to process email message. Error: {}", e.getMessage(), e);
            log.error("Raw message that failed: {}", message);
        }
    }

    private String extractField(JsonNode jsonNode, String fieldName) {
        if (jsonNode.has(fieldName) && !jsonNode.get(fieldName).isNull()) {
            return jsonNode.get(fieldName).asText();
        }
        log.warn("⚠️ Field '{}' not found or is null", fieldName);
        return "";
    }

    private Map<String, Object> extractVariables(JsonNode jsonNode) {
        try {
            if (jsonNode.has("variables") && jsonNode.get("variables").isObject()) {
                return objectMapper.convertValue(
                        jsonNode.get("variables"),
                        new TypeReference<Map<String, Object>>() {}
                );
            }
        } catch (Exception e) {
            log.warn("⚠️ Could not parse variables: {}", e.getMessage());
        }
        return Map.of("firstName", "User", "lastName", ""); // значения по умолчанию
    }
}