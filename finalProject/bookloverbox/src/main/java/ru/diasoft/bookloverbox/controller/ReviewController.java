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
import ru.diasoft.bookloverbox.domain.Review;
import ru.diasoft.bookloverbox.dto.ReviewDto;
import ru.diasoft.bookloverbox.services.ReviewService;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Отзывы на книги")
public class ReviewController {
    
    private final ReviewService reviewService;
    
    @PostMapping("/books/{bookId}")
    @Operation(summary = "Оставить отзыв")
    public ResponseEntity<Review> createReview(@PathVariable Long bookId,
                                               @Valid @RequestBody ReviewDto dto,
                                               @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(reviewService.createReview(bookId, dto, user.getUsername()));
    }
    
    @PutMapping("/{reviewId}")
    @Operation(summary = "Редактировать отзыв")
    public ResponseEntity<Review> updateReview(@PathVariable Long reviewId,
                                               @Valid @RequestBody ReviewDto dto,
                                               @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(reviewService.updateReview(reviewId, dto, user.getUsername()));
    }
    
    @DeleteMapping("/{reviewId}")
    @Operation(summary = "Удалить отзыв")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId,
                                            @AuthenticationPrincipal UserDetails user) {
        reviewService.deleteReview(reviewId, user.getUsername());
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/books/{bookId}")
    @Operation(summary = "Получить отзывы на книгу")
    public ResponseEntity<Page<ReviewDto>> getReviewsByBook(@PathVariable Long bookId,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(reviewService.getReviewsByBook(bookId, page, size));
    }
    
    @GetMapping("/my")
    @Operation(summary = "Получить мои отзывы")
    public ResponseEntity<Page<ReviewDto>> getMyReviews(@AuthenticationPrincipal UserDetails user,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(reviewService.getReviewsByUser(user.getUsername(), page, size));
    }
}