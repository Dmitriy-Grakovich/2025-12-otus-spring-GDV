package ru.diasoft.bookloverbox.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Schema(description = "Пользователь")
public class UserDto {
    
    @Schema(description = "ID пользователя")
    private Long id;
    
    @Schema(description = "Email", example = "user@example.com")
    private String email;
    
    @Schema(description = "Полное имя")
    private String fullName;
    
    @Schema(description = "Активен ли пользователь")
    private boolean isActive;
    
    @Schema(description = "Роли пользователя")
    private Set<String> roles;
    
    @Schema(description = "Дата регистрации")
    private LocalDateTime createdAt;
}
