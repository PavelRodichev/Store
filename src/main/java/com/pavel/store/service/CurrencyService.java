package com.pavel.store.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyService {


    private final CurrencyRateClient currencyRateClient;


    public BigDecimal getRate() {
        log.info("CurrencyService is working...");
        return currencyRateClient.getRate();
    }

}