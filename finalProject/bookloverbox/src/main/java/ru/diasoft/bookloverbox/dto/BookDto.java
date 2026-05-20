package ru.diasoft.bookloverbox.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "Книга")
public class BookDto {
    
    private Long id;
    
    @NotBlank(message = "Название книги обязательно")
    @Size(min = 1, max = 200, message = "Название должно быть от 1 до 200 символов")
    @Schema(description = "Название книги", example = "Война и мир")
    private String title;
    
    @Size(max = 5000, message = "Описание не должно превышать 5000 символов")
    @Schema(description = "Описание книги")
    private String description;
    
    @Schema(description = "URL обложки", example = "https://example.com/cover.jpg")
    private String coverUrl;
    
    @Min(value = 0, message = "Цена не может быть отрицательной")
    @Schema(description = "Цена книги", example = "299.00")
    private BigDecimal price;
    
    @Schema(description = "Статус книги", example = "PUBLISHED")
    private String status;
    
    @Schema(description = "ID жанра")
    private Long genreId;
    
    @Schema(description = "Название жанра")
    private String genreName;
    
    @Schema(description = "Имя автора")
    private String authorName;
    
    @Schema(description = "Средний рейтинг", example = "4.5")
    private double averageRating;
    
    @Schema(description = "Количество отзывов", example = "42")
    private int reviewsCount;
    
    @Schema(description = "Количество просмотров", example = "1000")
    private Long viewsCount;
    
    @Schema(description = "Количество скачиваний", example = "150")
    private Long downloadsCount;
    
    @Schema(description = "Дата публикации")
    private LocalDateTime publishedAt;
}
