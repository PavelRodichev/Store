package com.pavel.store.events;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class UserRegisteredEvent implements EventSource {

    private String event;
    private String username;
    private String email;
    private String firstName;
    private String lastName;

    public String getEvent() {
        return event;
    }

}
