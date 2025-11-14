package by.pasha.emailservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration"
})
class EmailServiceApplicationTests {
    @MockBean
    private EmailService emailService; // Мокаем сервис отправки email

    @MockBean
    private JavaMailSender javaMailSender; // Мокаем mail sender

    @Test
    void contextLoads() {
    }

}
