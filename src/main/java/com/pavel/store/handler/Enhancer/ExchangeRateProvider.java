package com.pavel.store.handler.Enhancer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

@Service
public class ExchangeRateProvider {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<Object, Object> getExchangeRate() {
        try (InputStream is = new ClassPathResource("exchange-rate.json").getInputStream()) {
            Map<Object, Object> rate = objectMapper.readValue(is, Map.class);
            return rate;
        } catch (IOException ex) {
            throw new RuntimeException("Не удалось загрузить exchange-rate.json", ex);
        }

    }


}

