package ru.diasoft.bookloverbox.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.diasoft.bookloverbox.domain.Genre;
import ru.diasoft.bookloverbox.dto.GenreDto;
import ru.diasoft.bookloverbox.services.GenreService;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
@Tag(name = "Genres", description = "Управление жанрами")
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    @Operation(summary = "Получить все жанры")
    public ResponseEntity<List<GenreDto>> getAllGenres() {
        return ResponseEntity.ok(genreService.getAllGenres());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить жанр по ID")
    public ResponseEntity<GenreDto> getGenreById(@PathVariable Long id) {
        return ResponseEntity.ok(genreService.getGenreById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Создать жанр (только ADMIN)")
    public ResponseEntity<Genre> createGenre(@Valid @RequestBody GenreDto dto) {
        return ResponseEntity.ok(genreService.createGenre(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Обновить жанр (только ADMIN)")
    public ResponseEntity<GenreDto> updateGenre(@PathVariable Long id, @Valid @RequestBody GenreDto dto) {
        return ResponseEntity.ok(genreService.updateGenre(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить жанр (только ADMIN)")
    public ResponseEntity<Void> deleteGenre(@PathVariable Long id) {
        genreService.deleteGenre(id);
        return ResponseEntity.ok().build();
    }
}

