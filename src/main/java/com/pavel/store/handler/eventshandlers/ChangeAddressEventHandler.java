package com.pavel.store.handler.eventshandlers;

import com.pavel.store.events.ChangeAddressEvent;
import com.pavel.store.events.CompletedOrderEvent;
import com.pavel.store.events.EventSource;
import com.pavel.store.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChangeAddressEventHandler implements EventHandler {

    private final OrderService orderService;

    @Override
    public void handle(EventSource event) {
        if (!(event instanceof ChangeAddressEvent)) {
            log.warn("Invalid event type: {}", event.getClass().getSimpleName());
            return;
        }

        try {
            ChangeAddressEvent orderEvent = (ChangeAddressEvent) event;

            log.info("WORKING HANDLER: Order {} new address {}", orderEvent.getOrderId(), orderEvent.getNewAddress());
            Long orderId = Long.parseLong(orderEvent.getOrderId());
            String newAddress = orderEvent.getNewAddress();
            orderService.changeAddress(newAddress, orderId);
            log.info("Successfully changed address for order {}", orderEvent.getOrderId());
        } catch (Exception e) {
            log.error("error processing CancelledOrderEvent");
        }
    }

    @Override
    public String getEventType() {
        return "CHANGE_ADDRESS";
    }

}
