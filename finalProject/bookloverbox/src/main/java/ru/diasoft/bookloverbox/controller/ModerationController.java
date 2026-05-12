package ru.diasoft.bookloverbox.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.diasoft.bookloverbox.domain.Book;
import ru.diasoft.bookloverbox.services.BookService;

@RestController
@RequestMapping("/api/moderator")
@RequiredArgsConstructor
@Tag(name = "Moderation", description = "Модерация книг")
public class ModerationController {
    
    private final BookService bookService;
    
    @PostMapping("/books/{id}/approve")
    @Operation(summary = "Одобрить книгу")
    public ResponseEntity<Book> approveBook(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.moderateBook(id, true, null));
    }
    
    @PostMapping("/books/{id}/reject")
    @Operation(summary = "Отклонить книгу")
    public ResponseEntity<Book> rejectBook(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.moderateBook(id, false, null));
    }
}