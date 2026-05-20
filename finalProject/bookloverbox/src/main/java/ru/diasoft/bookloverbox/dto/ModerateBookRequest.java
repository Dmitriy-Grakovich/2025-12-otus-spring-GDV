package ru.diasoft.bookloverbox.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Запрос на модерацию книги")
public class ModerateBookRequest {
    
    @Schema(description = "Новое описание книги (опционально)")
    private String description;
    
    @Schema(description = "Одобрить книгу (true) или отклонить в черновики (false)")
    private boolean approved;
    
    @Schema(description = "Причина отклонения")
    private String rejectionReason;
}
