package com.pavel.store.service;

import com.pavel.store.handler.Enhancer.ExchangeRateProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeService {

    private final RestTemplate restTemplate;

    private final ExchangeRateProvider exchangeRateProvider;

    @Value("${currency.service.url}")
    private String currencyServiceUrl;

    @Cacheable(cacheNames = "exchangeRate", unless = "#result == null")
    public BigDecimal getRateFrom() {
        log.info("Cache empty - fetching fresh rate...");
        try {
            return getRateFromService();
        } catch (Exception e) {
            log.info("Service unavailable:{}", e.getMessage());
            return getRateFromFile();
        }
    }

    private BigDecimal getRateFromFile() {
        return exchangeRateProvider.getExchangeRate();
    }

    private BigDecimal getRateFromService() {
        try {
            ResponseEntity<Map> responseEntity = restTemplate.getForEntity(currencyServiceUrl, Map.class);
            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                Object rate = responseEntity.getBody().get("exchangeRate");

                if (rate instanceof Double) {
                    return BigDecimal.valueOf((Double) rate);
                }
                log.info("Successfully got a rate from service: {}", rate);
            }
            throw new RuntimeException("Service returned error status");
        } catch (RuntimeException ex) {
            throw new RuntimeException("Service call failed: " + ex.getMessage());
        }

    }

}