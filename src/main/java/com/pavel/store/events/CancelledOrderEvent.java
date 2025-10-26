package com.pavel.store.events;

import lombok.Data;

@Data
public class CancelledOrderEvent implements EventSource {

    private String event;
    private String orderId;
    private String reason;

    @Override
    public String getEvent() {
        return event;
    }
}
