

package ru.diasoft.tasklesson16.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.diasoft.tasklesson16.controller.dto.BookDto;
import ru.diasoft.tasklesson16.controller.mapper.DtoMapper;
import ru.diasoft.tasklesson16.domain.Book;
import ru.diasoft.tasklesson16.service.BookService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final DtoMapper dtoMapper;

    @GetMapping
    public ResponseEntity<List<BookDto>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        List<BookDto> bookDtos = books.stream()
                .map(dtoMapper::toBookDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id)
                .map(dtoMapper::toBookDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BookDto> createBook(@RequestBody BookDto bookDto) {
        Book book = bookService.createBook(
                bookDto.getTitle(),
                bookDto.getAuthor().getFirstName(),
                bookDto.getAuthor().getLastName(),
                bookDto.getGenre().getName()
        );
        return ResponseEntity.ok(dtoMapper.toBookDto(book));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDto> updateBook(
            @PathVariable Long id,
            @RequestBody BookDto bookDto) {
        Book updatedBook = bookService.updateBook(
                id,
                bookDto.getTitle(),
                bookDto.getAuthor().getFirstName(),
                bookDto.getAuthor().getLastName(),
                bookDto.getGenre().getName()
        );
        return ResponseEntity.ok(dtoMapper.toBookDto(updatedBook));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
