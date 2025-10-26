package com.pavel.store.events;

import lombok.Data;

@Data
public class CompletedOrderEvent implements EventSource {
    private String event;
    private String userId;
    private String orderId;


    @Override
    public String getEvent() {
        return event;
    }
}
