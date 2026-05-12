package ru.diasoft.bookloverbox.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "Отзыв на книгу")
public class ReviewDto {
    
    private Long id;
    
    @Schema(description = "ID книги")
    private Long bookId;
    
    @Schema(description = "Название книги")
    private String bookTitle;
    
    @Schema(description = "ID пользователя")
    private Long userId;
    
    @Schema(description = "Имя пользователя")
    private String userName;
    
    @NotNull(message = "Рейтинг обязателен")
    @Min(value = 1, message = "Минимальный рейтинг - 1")
    @Max(value = 5, message = "Максимальный рейтинг - 5")
    @Schema(description = "Рейтинг от 1 до 5", example = "5")
    private Integer rating;
    
    @Schema(description = "Текст отзыва", example = "Отличная книга!")
    private String comment;
    
    @Schema(description = "Дата создания")
    private LocalDateTime createdAt;
    
    @Schema(description = "Дата обновления")
    private LocalDateTime updatedAt;
}