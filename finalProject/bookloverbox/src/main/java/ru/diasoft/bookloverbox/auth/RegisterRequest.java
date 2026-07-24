package ru.diasoft.bookloverbox.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Запрос на регистрацию")
public class RegisterRequest {
    
    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    @Schema(description = "Email пользователя", example = "author@example.com")
    private String email;
    
    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, message = "Пароль должен содержать минимум 6 символов")
    @Schema(description = "Пароль", example = "password123")
    private String password;
    
    @NotBlank(message = "Имя обязательно")
    @Size(min = 2, max = 100, message = "Имя должно быть от 2 до 100 символов")
    @Schema(description = "Полное имя", example = "Иван Петров")
    private String fullName;
    
    @Schema(description = "Хочет ли пользователь быть автором", example = "true")
    private boolean wantsToBeAuthor = false;
}
