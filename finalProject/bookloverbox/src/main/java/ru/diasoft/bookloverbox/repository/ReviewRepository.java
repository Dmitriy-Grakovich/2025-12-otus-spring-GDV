package ru.diasoft.bookloverbox.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.diasoft.bookloverbox.domain.Book;
import ru.diasoft.bookloverbox.domain.Review;
import ru.diasoft.bookloverbox.domain.User;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    Page<Review> findByBook(Book book, Pageable pageable);
    
    Page<Review> findByUser(User user, Pageable pageable);
    
    Optional<Review> findByBookAndUser(Book book, User user);
    
    boolean existsByBookAndUser(Book book, User user);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.book = :book")
    Double getAverageRatingByBook(@Param("book") Book book);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.book = :book")
    long countReviewsByBook(@Param("book") Book book);
    
    @Modifying
    @Query("DELETE FROM Review r WHERE r.book = :book")
    void deleteAllByBook(@Param("book") Book book);
}