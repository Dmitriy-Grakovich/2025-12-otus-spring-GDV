package ru.diasoft.bookloverbox.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
@Tag(name = "Batch Jobs", description = "Пакетная обработка данных")
public class BatchController {
    
    private final JobLauncher jobLauncher;
    private final Job exportBooksJob;
    
    @PostMapping("/export-books")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Экспорт книг в CSV")
    public ResponseEntity<String> exportBooks() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();
            
            jobLauncher.run(exportBooksJob, params);
            return ResponseEntity.ok("Экспорт книг запущен успешно");
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Ошибка при запуске экспорта: " + e.getMessage());
        }
    }
}
