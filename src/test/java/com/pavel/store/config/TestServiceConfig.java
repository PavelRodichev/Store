package com.pavel.store.config;

import com.pavel.store.service.IdempotencyService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

@TestConfiguration
@Profile("test")
public class TestServiceConfig {

    @Bean
    @Primary
    public IdempotencyService testIdempotencyService() {
        return new IdempotencyService(null) {
            @Override
            public Boolean hasExistKey(String key) {
                return false; // Всегда false для тестов
            }

            @Override
            public Long getOrderIdByKey(String key) {
                return null;
            }

            @Override
            public Boolean saveKeyWithOrderId(String key, Long orderId) {
                return true; // Всегда успешно для тестов
            }
        };
    }

    @Bean
    @Primary
    public RestTemplate testRestTemplate() {
        return new RestTemplate();
    }
}