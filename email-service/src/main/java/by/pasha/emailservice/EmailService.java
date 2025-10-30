package by.pasha.emailservice;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;


    private final TemplateEngine templateEngine;

    // Отправка HTML писем с Thymeleaf
    public void sendHtmlEmail(String to, String subject, String templateName,
                              Map<String, Object> variables) {

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariables(variables);
            String htmlContent = templateEngine.process(templateName, context);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);// true указывает на HTML контент
            helper.setFrom("noreply@yourstore.com");

            javaMailSender.send(message);
            log.info("HTML email sent to:" + to);
        } catch (Exception e) {
            log.info("error send email {}", e.getMessage());
        }
    }
}
