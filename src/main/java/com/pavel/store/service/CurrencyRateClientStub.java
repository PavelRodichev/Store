package com.pavel.store.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
@ConditionalOnProperty(name = "interaction.rate.stub", havingValue = "true")
public class CurrencyRateClientStub implements CurrencyRateClient {

    @Override
    public BigDecimal getRate() {
        log.info("Using Stub realization");
        return BigDecimal.valueOf(75);
    }

}
