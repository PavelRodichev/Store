package by.pasha.emailservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;

@SpringBootTest
class EmailServiceApplicationTests {
    @MockBean
    private EmailService emailService; // Мокаем сервис отправки email

    @MockBean
    private JavaMailSender javaMailSender; // Мокаем mail sender

    @Test
    void contextLoads() {
    }

}
