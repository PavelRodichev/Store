package com.pavel.store.handler.Enhancer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;

@Service
public class ExchangeRateProvider {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public BigDecimal getExchangeRate() {
        try (InputStream is = new ClassPathResource("exchange-rate.json").getInputStream()) {
            ExchangeRate rate = objectMapper.readValue(is, ExchangeRate.class);
            return rate.getExchangeRate();
        } catch (IOException ex) {
            new RuntimeException("Не удалось загрузить exchange-rate.json", ex);
            return BigDecimal.ONE;
        }
    }

    private static class ExchangeRate {
        private BigDecimal exchangeRate;

        public BigDecimal getExchangeRate() {
            return exchangeRate;
        }

        public void setExchangeRate(BigDecimal exchangeRate) {
            this.exchangeRate = exchangeRate;
        }
    }
}
