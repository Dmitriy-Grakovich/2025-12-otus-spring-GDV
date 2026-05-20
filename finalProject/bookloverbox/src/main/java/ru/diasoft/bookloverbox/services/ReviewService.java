package ru.diasoft.bookloverbox.services;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.diasoft.bookloverbox.domain.Book;
import ru.diasoft.bookloverbox.domain.BookStatus;
import ru.diasoft.bookloverbox.domain.Review;
import ru.diasoft.bookloverbox.domain.User;
import ru.diasoft.bookloverbox.dto.ReviewDto;
import ru.diasoft.bookloverbox.repository.BookRepository;
import ru.diasoft.bookloverbox.repository.ReviewRepository;
import ru.diasoft.bookloverbox.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    @Qualifier("errorChannel")
    private final MessageChannel newReviewChannel;
    
    @Transactional
    public Review createReview(Long bookId, ReviewDto dto, String userEmail) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("Книга не найдена"));
        
        if (book.getStatus() != BookStatus.PUBLISHED) {
            throw new RuntimeException("Нельзя оставить отзыв на неопубликованную книгу");
        }
        
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        
        if (reviewRepository.existsByBookAndUser(book, user)) {
            throw new RuntimeException("Вы уже оставляли отзыв на эту книгу");
        }
        
        if (dto.getRating() < 1 || dto.getRating() > 5) {
            throw new RuntimeException("Рейтинг должен быть от 1 до 5");
        }
        
        Review review = new Review();
        review.setBook(book);
        review.setUser(user);
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setCreatedAt(LocalDateTime.now());
        
        Review savedReview = reviewRepository.save(review);
        
        // Отправка асинхронного события о новом отзыве
        newReviewChannel.send(MessageBuilder.withPayload(savedReview).build());
        
        return savedReview;
    }
    
    @Transactional
    public Review updateReview(Long reviewId, ReviewDto dto, String userEmail) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Отзыв не найден"));
        
        if (!review.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("Вы можете редактировать только свои отзывы");
        }
        
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setUpdatedAt(LocalDateTime.now());
        
        return reviewRepository.save(review);
    }
    
    @Transactional
    public void deleteReview(Long reviewId, String userEmail) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Отзыв не найден"));
        
        boolean isAdmin = userEmail.equals("admin@bookloverbox.com"); // Упрощенно
        if (!review.getUser().getEmail().equals(userEmail) && !isAdmin) {
            throw new RuntimeException("Вы можете удалять только свои отзывы");
        }
        
        reviewRepository.delete(review);
    }
    
    public Page<ReviewDto> getReviewsByBook(Long bookId, int page, int size) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("Книга не найдена"));
        
        Pageable pageable = PageRequest.of(page, size);
        return reviewRepository.findByBook(book, pageable)
            .map(this::convertToDto);
    }
    
    public Page<ReviewDto> getReviewsByUser(String userEmail, int page, int size) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        
        Pageable pageable = PageRequest.of(page, size);
        return reviewRepository.findByUser(user, pageable)
            .map(this::convertToDto);
    }
    
    public double getBookAverageRating(Long bookId) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("Книга не найдена"));
        
        Double avg = reviewRepository.getAverageRatingByBook(book);
        return avg != null ? avg : 0.0;
    }
    
    public ReviewDto convertToDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());
        dto.setBookId(review.getBook().getId());
        dto.setBookTitle(review.getBook().getTitle());
        dto.setUserId(review.getUser().getId());
        dto.setUserName(review.getUser().getFullName());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUpdatedAt(review.getUpdatedAt());
        return dto;
    }
}
