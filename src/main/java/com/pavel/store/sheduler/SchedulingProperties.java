package com.pavel.store.sheduler;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "app.scheduling")
@Data
@Validated
public class SchedulingProperties {

    @NotNull
    private boolean enabled = false;

    @Pattern(regexp = "\\d+", message = "Period must be a number")
    private String period = "30000";

}
