package ru.diasoft.bookloverbox.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.diasoft.bookloverbox.domain.Book;
import ru.diasoft.bookloverbox.dto.BookDto;
import ru.diasoft.bookloverbox.dto.ModerateBookRequest;
import ru.diasoft.bookloverbox.services.BookService;

@RestController
@RequestMapping("/api/moderator")
@RequiredArgsConstructor
@Tag(name = "Moderation", description = "Модерация книг")
public class ModerationController {
    
    private final BookService bookService;
    
    @GetMapping("/books/pending")
    @Operation(summary = "Получить книги на модерации")
    public ResponseEntity<Page<BookDto>> getPendingBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookService.getPendingBooks(page, size));
    }
    
    @PostMapping("/books/{id}/moderate")
    @Operation(summary = "Модерировать книгу (одобрить в библиотеку или вернуть в черновики)")
    public ResponseEntity<BookDto> moderateBook(@PathVariable Long id,
                                                @Valid @RequestBody ModerateBookRequest request) {
        Book book = bookService.moderateBookWithEdit(
            id, 
            request.getDescription(), 
            request.isApproved(), 
            request.getRejectionReason()
        );
        return ResponseEntity.ok(bookService.convertToDto(book));
    }
    
    @PostMapping("/books/{id}/approve")
    @Operation(summary = "Быстрое одобрение книги")
    public ResponseEntity<BookDto> approveBook(@PathVariable Long id) {
        Book book = bookService.moderateBook(id, true, null);
        return ResponseEntity.ok(bookService.convertToDto(book));
    }
    
    @PostMapping("/books/{id}/reject")
    @Operation(summary = "Быстрое отклонение книги в черновики")
    public ResponseEntity<BookDto> rejectBook(@PathVariable Long id) {
        Book book = bookService.moderateBook(id, false, null);
        return ResponseEntity.ok(bookService.convertToDto(book));
    }
}
