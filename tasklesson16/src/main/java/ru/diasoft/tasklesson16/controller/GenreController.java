package ru.diasoft.tasklesson16.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.diasoft.tasklesson16.controller.dto.GenreDto;
import ru.diasoft.tasklesson16.controller.mapper.DtoMapper;
import ru.diasoft.tasklesson16.domain.Genre;
import ru.diasoft.tasklesson16.service.GenreService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;
    private final DtoMapper dtoMapper;

    @GetMapping
    public ResponseEntity<List<GenreDto>> getAllGenres() {
        return ResponseEntity.ok(genreService.getAllGenres().stream()
                .map(dtoMapper::toGenreDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenreDto> getGenreById(@PathVariable Long id) {
        return genreService.getGenreById(id)
                .map(dtoMapper::toGenreDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<GenreDto> getGenreByName(@PathVariable String name) {
        return genreService.findGenreByName(name)
                .map(dtoMapper::toGenreDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<GenreDto> createGenre(@RequestParam String name) {
        Genre genre = genreService.createGenre(name);
        return ResponseEntity.ok(dtoMapper.toGenreDto(genre));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenreDto> updateGenre(
            @PathVariable Long id,
            @RequestParam String name) {
        Genre updatedGenre = genreService.updateGenre(id, name);
        return ResponseEntity.ok(dtoMapper.toGenreDto(updatedGenre));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenre(@PathVariable Long id) {
        genreService.deleteGenre(id);
        return ResponseEntity.noContent().build();
    }
}
