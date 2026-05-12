package ru.diasoft.bookloverbox.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Статистика системы")
public class UserStatsDto {
    
    @Schema(description = "Всего пользователей")
    private long totalUsers;
    
    @Schema(description = "Активных пользователей")
    private long activeUsers;
    
    @Schema(description = "Всего книг")
    private long totalBooks;
    
    @Schema(description = "Опубликованных книг")
    private long publishedBooks;
    
    @Schema(description = "Книг на модерации")
    private long booksOnModeration;
}