package com.pavel.store.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Данные для входа")
public class UserLoginDto {

    @Schema(description = "Email пользователя", example = "user@example.com")
    @NotBlank(message = "Username or email is required")
    private String email;

    @Schema(description = "Пароль", example = "password123")
    @NotBlank(message = "Password is required")
    private String password;

}
