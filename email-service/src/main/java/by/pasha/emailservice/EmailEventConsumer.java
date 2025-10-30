package by.pasha.emailservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailEventConsumer {

    private final EmailService emailService;
    private final ObjectMapper objectMapper;


    @KafkaListener(topics = "email-topic")
    public void consumeUserRegisteredEvent(String message) {
        log.info("consumer email give message for topic: {}", message);
        try {
            EmailMessage emailMessage = objectMapper.readValue(message, EmailMessage.class);

            log.info("""
                            ✅ Successfully parsed EmailMessage:
                            ID: {}
                            To: {}
                            Subject: {}
                            Template: {}
                            Variables: {}
                            """, emailMessage.getId(), emailMessage.getTo(),
                    emailMessage.getSubject(), emailMessage.getTemplateName(),
                    emailMessage.getVariables());

            emailService.sendHtmlEmail(emailMessage.getTo(), emailMessage.getSubject(),
                    emailMessage.getTemplateName(), emailMessage.getVariables());
            log.info("message input in sendHtmlEmail() method");
        } catch (Exception e) {
            log.error("❌ Failed to send welcome email to: {}", e.getMessage());
        }
    }


}
