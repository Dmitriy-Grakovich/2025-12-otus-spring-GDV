package ru.diasoft.bookloverbox.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Set;

@Data
@AllArgsConstructor
@Schema(description = "Ответ с JWT токенами")
public class AuthResponse {
    
    @Schema(description = "Access token", example = "eyJhbGciOiJIUzI1NiIs...")
    private String accessToken;
    
    @Schema(description = "Refresh token", example = "eyJhbGciOiJIUzI1NiIs...")
    private String refreshToken;
    
    @Schema(description = "Email пользователя", example = "user@example.com")
    private String email;
    
    @Schema(description = "Роли пользователя", example = "[\"ROLE_READER\", \"ROLE_AUTHOR\"]")
    private Set<String> roles;
}