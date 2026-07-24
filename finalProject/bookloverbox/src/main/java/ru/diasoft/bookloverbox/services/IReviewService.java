package ru.diasoft.bookloverbox.services;

import org.springframework.data.domain.Page;
import ru.diasoft.bookloverbox.domain.Review;
import ru.diasoft.bookloverbox.dto.ReviewDto;

public interface IReviewService {
    
    Review createReview(Long bookId, ReviewDto dto, String userEmail);
    
    Review updateReview(Long reviewId, ReviewDto dto, String userEmail);
    
    void deleteReview(Long reviewId, String userEmail);
    
    Page<ReviewDto> getReviewsByBook(Long bookId, int page, int size);
    
    Page<ReviewDto> getReviewsByUser(String userEmail, int page, int size);
    
    double getBookAverageRating(Long bookId);
    
    ReviewDto convertToDto(Review review);
}
