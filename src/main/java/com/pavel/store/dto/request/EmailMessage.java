package com.pavel.store.dto.request;

import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
public class EmailMessage {

    private String id;
    private String to;
    private String subject;
    private String templateName;
    private Instant timestamp;
    private Map<String, Object> variables;

}
