package com.pavel.store.handler.eventshandlers;

import com.pavel.store.events.CompletedOrderEvent;
import com.pavel.store.events.EventSource;
import com.pavel.store.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CompletedOrderEventHandler implements EventHandler {
    private final OrderService orderService;

    @Override
    public void handle(EventSource event) {
        if (!(event instanceof CompletedOrderEvent)) {
            log.warn("Invalid event type: {}", event.getClass().getSimpleName());
            return;
        }
        try {
            CompletedOrderEvent orderEvent = (CompletedOrderEvent) event;
            log.info(" WORKING HANDLER: Order {} for user {}", orderEvent.getOrderId(), orderEvent.getUserId());
            orderService.completedOrder(Long.parseLong(orderEvent.getOrderId()));
            log.info("Successfully changed order status for order {}", orderEvent.getOrderId());
        } catch (Exception e) {
            log.error("error processing CancelledOrderEvent");
        }
    }

    @Override
    public String getEventType() {
        return "COMPLETED_ORDER";
    }


}
