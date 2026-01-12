package ru.diasoft.tasklesson16.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.diasoft.tasklesson16.controller.dto.AuthorDto;
import ru.diasoft.tasklesson16.controller.mapper.DtoMapper;
import ru.diasoft.tasklesson16.domain.Author;
import ru.diasoft.tasklesson16.service.AuthorService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorService authorService;
    private final DtoMapper dtoMapper;

    public AuthorController(AuthorService authorService, DtoMapper dtoMapper) {
        this.authorService = authorService;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping
    public ResponseEntity<List<AuthorDto>> getAllAuthors() {
        List<Author> authors = authorService.getAllAuthors();
        List<AuthorDto> authorDtos = authors.stream()
                .map(dtoMapper::toAuthorDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(authorDtos);
    }
    @GetMapping("/{id}")
    public ResponseEntity<AuthorDto> getAuthorById(@PathVariable Long id) {
        return authorService.getAuthorById(id)
                .map(dtoMapper::toAuthorDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AuthorDto> createAuthor(@RequestBody AuthorDto authorDto) {
        Author author = authorService.createAuthor(
                authorDto.getFirstName(),
                authorDto.getLastName(),
                authorDto.getAge()
        );
        return ResponseEntity.ok(dtoMapper.toAuthorDto(author));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<AuthorDto> updateAuthor(
            @PathVariable Long id,
            @RequestBody AuthorDto authorDto
    ) {
        return authorService.updateAuthor(
                id,
                authorDto.getFirstName(),
                authorDto.getLastName(),
                authorDto.getAge()
        )
        .map(dtoMapper::toAuthorDto)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }

}
