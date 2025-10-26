package com.pavel.store.events;

import lombok.Data;

import java.util.List;

@Data
public class CreateOrderEvent implements EventSource {

    private String event;
    private String orderId;
    private String userId;
    private List<String> items;
    private String address;

    @Override
    public String getEvent() {
        return event;
    }
}
