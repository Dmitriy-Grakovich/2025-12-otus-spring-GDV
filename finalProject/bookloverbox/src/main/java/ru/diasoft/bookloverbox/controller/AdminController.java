package ru.diasoft.bookloverbox.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.diasoft.bookloverbox.domain.User;
import ru.diasoft.bookloverbox.dto.UserStatsDto;
import ru.diasoft.bookloverbox.services.IAdminService;
import ru.diasoft.bookloverbox.services.IUserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Администрирование (только ADMIN)")
public class AdminController {
    
    private final IAdminService adminService;
    private final IUserService userService;
    
    @GetMapping("/users")
    @Operation(summary = "Получить всех пользователей")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }
    
    @GetMapping("/users/{userId}")
    @Operation(summary = "Получить пользователя по ID")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.getUserById(userId));
    }
    
    @PutMapping("/users/{userId}/activate")
    @Operation(summary = "Активировать/деактивировать пользователя")
    public ResponseEntity<Void> toggleUserActive(@PathVariable Long userId, @RequestParam boolean active) {
        adminService.setUserActive(userId, active);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/users/{userId}/roles/{roleName}")
    @Operation(summary = "Назначить роль пользователю")
    public ResponseEntity<Void> addRoleToUser(@PathVariable Long userId, @PathVariable String roleName) {
        userService.addRoleToUser(userId, "ROLE_" + roleName.toUpperCase());
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/users/{userId}/roles/{roleName}")
    @Operation(summary = "Удалить роль у пользователя")
    public ResponseEntity<Void> removeRoleFromUser(@PathVariable Long userId, @PathVariable String roleName) {
        userService.removeRoleFromUser(userId, "ROLE_" + roleName.toUpperCase());
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/stats")
    @Operation(summary = "Получить общую статистику")
    public ResponseEntity<UserStatsDto> getSystemStats() {
        return ResponseEntity.ok(adminService.getSystemStats());
    }
    
    @GetMapping("/stats/books-by-status")
    @Operation(summary = "Статистика книг по статусам")
    public ResponseEntity<Map<String, Long>> getBooksStatsByStatus() {
        return ResponseEntity.ok(adminService.getBooksCountByStatus());
    }
    
    @GetMapping("/stats/top-authors")
    @Operation(summary = "Топ авторов по количеству книг")
    public ResponseEntity<List<User>> getTopAuthors(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(adminService.getTopAuthors(limit));
    }
}
