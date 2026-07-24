package ru.diasoft.bookloverbox.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Schema(description = "Запрос на создание книги")
public class CreateBookRequest {
    
    @NotBlank(message = "Название обязательно")
    @Size(max = 200, message = "Название не должно превышать 200 символов")
    @Schema(description = "Название книги", example = "Мастер и Маргарита")
    private String title;
    
    @NotBlank(message = "Автор обязателен")
    @Size(max = 200, message = "Имя автора не должно превышать 200 символов")
    @Schema(description = "Автор книги", example = "Булгаков, Михаил Афанасьевич")
    private String authorName;
    
    @Schema(description = "Описание/аннотация книги")
    private String description;
    
    @NotNull(message = "Жанр обязателен")
    @Schema(description = "ID жанра", example = "1")
    private Long genreId;
    
    @Schema(description = "Теги через запятую", example = "мистика, сатира, классика")
    private String tags;
    
    @Schema(description = "Текст книги")
    private String content;
    
    @Schema(description = "Язык книги", example = "Русский")
    private String language = "Русский";
    
    @Schema(description = "Год издания", example = "1967")
    private Integer publicationYear;
    
    @Schema(description = "Издательство", example = "Художественная литература")
    private String publisher;
    
    @Schema(description = "Количество страниц", example = "384")
    private Integer pageCount;
    
    @Schema(description = "Возрастной рейтинг", example = "16+", allowableValues = {"0+", "6+", "12+", "16+", "18+"})
    private String ageRating = "0+";
    
    @Schema(description = "URL обложки", example = "https://example.com/cover.jpg")
    private String coverUrl;
    
    @Schema(description = "Правообладатель", example = "Общественное достояние")
    private String copyrightHolder;
    
    @Schema(description = "ISBN", example = "978-5-17-123456-7")
    private String isbn;
}
