package com.pavel.store.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        property = "event",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreateOrderEvent.class, name = "CREATE_ORDER"),
        @JsonSubTypes.Type(value = ChangeAddressEvent.class, name = "CHANGE_ADDRESS"),
        @JsonSubTypes.Type(value = CancelledOrderEvent.class, name = "CANCELLED_ORDER"),
        @JsonSubTypes.Type(value = CompletedOrderEvent.class, name = "COMPLETED_ORDER"),
        @JsonSubTypes.Type(value = UserRegisteredEvent.class, name = "USER_REGISTERED")
})
public interface EventSource {

    String getEvent();
}
