package by.pasha.emailservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;


@Configuration
public class ThymeleafConfig {

    @Bean
    @Primary
    public TemplateEngine emailTemplateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(emailTemplateResolver());

        System.out.println("=== EMAIL TEMPLATE ENGINE CREATED ===");
        System.out.println("Template resolvers: " + templateEngine.getTemplateResolvers());

        return templateEngine;
    }

    private ITemplateResolver emailTemplateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();


        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(false);
        templateResolver.setCheckExistence(true);


        System.out.println("=== TEMPLATE RESOLVER CONFIG ===");
        System.out.println("Prefix: '" + templateResolver.getPrefix() + "'");
        System.out.println("Suffix: '" + templateResolver.getSuffix() + "'");
        System.out.println("Full template path: " + templateResolver.getPrefix() + "welcome-email" + templateResolver.getSuffix());

        return templateResolver;
    }
}