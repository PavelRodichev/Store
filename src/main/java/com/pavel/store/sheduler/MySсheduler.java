package com.pavel.store.sheduler;

import com.pavel.store.entity.Product;
import com.pavel.store.repository.ProductRepository;
import com.pavel.store.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class MySсheduler {

    private final ProductService productService;

    private final ProductRepository productRepository;

    @Value("${app.scheduling.enabled}")
    private boolean enable;


    @Transactional
    @Scheduled(fixedRateString = "${app.scheduling.period}")
    public void increaseInThePriceOfTheProduct() {
        if (!enable) {
            return;
        }
        log.info("🔄 SCHEDULER STARTED - БЛОКИРУЮ ТОВАРЫ!");
        //  Блокируем все товары
        List<Product> products = productRepository.findAllWithPessimisticWrite();
        log.info("🔒 SCHEDULER: Заблокировал {} товаров", products.size());

        try {
            productService.increaseAllPrices(BigDecimal.valueOf(10));
            for (int i = 1; i <= 15; i++) {
                Thread.sleep(1000);
                log.info("🔒 SCHEDULER: Держу FOR UPDATE... {} сек", i);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.info("✅ SCHEDULER: ОТПУСТИЛ БЛОКИРОВКУ!");
    }
}



