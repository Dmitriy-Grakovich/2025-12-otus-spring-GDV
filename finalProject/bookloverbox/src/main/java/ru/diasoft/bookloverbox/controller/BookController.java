package ru.diasoft.bookloverbox.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.diasoft.bookloverbox.domain.Book;
import ru.diasoft.bookloverbox.dto.BookDto;
import ru.diasoft.bookloverbox.services.BookService;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "Books", description = "Управление книгами")
public class BookController {
    
    private final BookService bookService;
    
    @PostMapping
    @Operation(summary = "Создать новую книгу")
    public ResponseEntity<Book> createBook(@Valid @RequestBody BookDto dto,
                                           @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(bookService.createBook(dto, user.getUsername()));
    }
    
    @PostMapping("/{id}/moderation")
    @Operation(summary = "Отправить книгу на модерацию")
    public ResponseEntity<Book> submitToModeration(@PathVariable Long id,
                                                   @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(bookService.submitToModeration(id, user.getUsername()));
    }
    
    @GetMapping
    @Operation(summary = "Получить список опубликованных книг")
    public ResponseEntity<Page<BookDto>> getPublishedBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookService.getPublishedBooks(page, size));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Получить книгу по ID")
    public ResponseEntity<BookDto> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }
    
    @GetMapping("/my")
    @Operation(summary = "Получить мои книги")
    public ResponseEntity<Page<BookDto>> getMyBooks(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookService.getBooksByAuthor(user.getUsername(), page, size));
    }
    
    @GetMapping("/search")
    @Operation(summary = "Поиск книг по названию")
    public ResponseEntity<Page<BookDto>> searchBooks(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookService.searchBooks(title, page, size));
    }
}