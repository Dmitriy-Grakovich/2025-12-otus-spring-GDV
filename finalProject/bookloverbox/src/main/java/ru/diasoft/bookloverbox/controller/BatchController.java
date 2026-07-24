package ru.diasoft.bookloverbox.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Batch Jobs", description = "Пакетная обработка данных")
public class BatchController {
    
    private final JobLauncher jobLauncher;
    private final Job exportBooksJob;
    
    // Флаг для предотвращения параллельных запусков экспорта
    private volatile boolean exportRunning = false;
    
    @PostMapping("/export-books")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Экспорт книг в CSV")
    public ResponseEntity<Map<String, Object>> exportBooks() {
        // Проверка: не запущен ли уже экспорт
        if (exportRunning) {
            log.warn("Экспорт уже выполняется - отклоняем повторный запрос");
            return ResponseEntity.status(409).body(Map.of(
                "error", "Экспорт уже запущен. Подождите завершения текущего задания."
            ));
        }
        
        try {
            exportRunning = true;
            log.info("Запуск экспорта книг...");
            
            JobParameters params = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();
            
            // Запускаем джоб и получаем JobExecution с идентификатором
            JobExecution execution = jobLauncher.run(exportBooksJob, params);
            
            log.info("Экспорт завершен успешно. JobId: {}, Статус: {}", 
                     execution.getId(), execution.getStatus());
            
            // Возвращаем jobId и статус для отслеживания
            return ResponseEntity.ok(Map.of(
                "jobId", execution.getId(),
                "status", execution.getStatus().name(),
                "message", "Экспорт книг завершен успешно",
                "exitCode", execution.getExitStatus().getExitCode()
            ));
        } catch (Exception e) {
            log.error("Ошибка при запуске экспорта книг: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                "error", "Ошибка при запуске экспорта: " + e.getMessage()
            ));
        } finally {
            // Сбрасываем флаг после завершения (успех или ошибка)
            exportRunning = false;
        }
    }
}
