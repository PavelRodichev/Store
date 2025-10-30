package by.pasha.emailservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailMessage {
    private String id;
    private String to;
    private String subject;
    private String templateName;
    private Map<String, Object> variables;
    private Instant timestamp;

}
