package com.pavel.store.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ с JWT токеном")
public class JwtAuthenticationResponse {
    @Schema(description = "JWT токен доступа", example = "eyJhbGciOiJIUzUxMiJ9...")
    private String accessToken;
    @Schema(description = "Тип токена", example = "Bearer")
    private String tokenType = "Bearer";


    public JwtAuthenticationResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
