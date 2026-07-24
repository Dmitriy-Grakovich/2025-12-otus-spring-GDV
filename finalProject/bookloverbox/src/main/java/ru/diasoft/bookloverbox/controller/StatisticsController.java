package ru.diasoft.bookloverbox.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.diasoft.bookloverbox.dto.UserStatsDto;
import ru.diasoft.bookloverbox.services.IAdminService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Tag(name = "Statistics", description = "Статистика портала")
public class StatisticsController {
    
    private final IAdminService adminService;
    
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Статистика пользователей")
    public ResponseEntity<List<UserStatsDto>> getUsersStatistics() {
        return ResponseEntity.ok(adminService.getUsersStatistics());
    }
    
    @GetMapping("/overview")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @Operation(summary = "Общая статистика портала")
    public ResponseEntity<Map<String, Object>> getOverviewStatistics() {
        return ResponseEntity.ok(adminService.getOverviewStatistics());
    }
}
