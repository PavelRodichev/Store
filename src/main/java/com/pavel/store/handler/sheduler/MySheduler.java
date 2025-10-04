package com.pavel.store.handler.sheduler;

import com.pavel.store.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
@RequiredArgsConstructor
public class MySheduler {

    private final ProductService productService;

    @Value("${app.scheduling.enabled}")
    private boolean enable;

    @Scheduled(fixedRateString = "${app.scheduling.period}")
    public void increaseInThePriceOfTheProduct() {
        if (enable) {
            log.info("Task executed every 30 seconds");
            productService.increaseAllPrices(BigDecimal.valueOf(10));
        }
    }
}
