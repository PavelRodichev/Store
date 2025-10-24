package com.pavel.store.service;

import com.pavel.store.controller.rest.session.SessionCurrency;
import com.pavel.store.handler.Enhancer.ExchangeRateProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@Service
@ConditionalOnProperty(name = "interaction.rate.stub", havingValue = "false")
@Slf4j
@RequiredArgsConstructor
public class RestCurrencyRateClient implements CurrencyRateClient {

    private final RestTemplate restTemplate;

    private final ExchangeRateProvider exchangeRateProvider;

    private final SessionCurrency sessionCurrency;

    @Value("${currency.service.url}")
    private String currencyServiceUrl;


    public BigDecimal getRate() {

        try {
            return getRateFromService();
        } catch (Exception e) {
            log.info("Service unavailable:{}", e.getMessage());
            return getRateFromFile();
        }
    }

    private BigDecimal getRateFromFile() {
        log.info("get rate from json file");
        Map<Object, Object> rate = exchangeRateProvider.getExchangeRate();
        if (!rate.isEmpty() && rate.containsKey(sessionCurrency.getCurrency())) {
            String currency = sessionCurrency.getCurrency();
            Object currentRate = rate.get(currency);
            if (currentRate instanceof Double) {
                return BigDecimal.valueOf((Double) currentRate);
            }
        }

        return BigDecimal.ONE;
    }

    private BigDecimal getRateFromService() {
        try {
            ResponseEntity<Map> responseEntity = restTemplate.getForEntity(currencyServiceUrl, Map.class);
            String currency = sessionCurrency.getCurrency();
            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                if (responseEntity.getBody().containsKey(currency)) {
                    Object rate = responseEntity.getBody().get(currency);

                    if (rate instanceof Double) {
                        return BigDecimal.valueOf((Double) rate);
                    }
                    log.info("Successfully got a rate from service: {}", rate);
                }

            }
        } catch (RuntimeException ex) {
            throw new RuntimeException("Service call failed: " + ex.getMessage());
        }
        return BigDecimal.ONE;
    }
}


