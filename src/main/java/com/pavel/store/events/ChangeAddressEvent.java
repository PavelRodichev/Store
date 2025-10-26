package com.pavel.store.events;

import lombok.Data;

@Data
public class ChangeAddressEvent implements EventSource {
    private String event;
    private String newAddress;
    private String orderId;


    @Override
    public String getEvent() {
        return event;
    }
}
