package ru.diasoft.bookloverbox.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Жанр книги")
public class GenreDto {
    
    private Long id;
    
    @NotBlank(message = "Название жанра обязательно")
    @Size(min = 2, max = 50, message = "Название жанра должно быть от 2 до 50 символов")
    @Schema(description = "Название жанра", example = "Фантастика")
    private String name;
    
    @Size(max = 255, message = "Описание не должно превышать 255 символов")
    @Schema(description = "Описание жанра", example = "Научная фантастика, космос, технологии")
    private String description;
    
    @Schema(description = "Количество книг в жанре")
    private int booksCount;
}