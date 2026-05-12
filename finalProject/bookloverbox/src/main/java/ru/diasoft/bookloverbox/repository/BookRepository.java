package ru.diasoft.bookloverbox.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.diasoft.bookloverbox.domain.Book;


import ru.diasoft.bookloverbox.domain.BookStatus;
import ru.diasoft.bookloverbox.domain.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    // Поиск по автору
    List<Book> findByAuthor(User author);
    Page<Book> findByAuthor(User author, Pageable pageable);
    
    // Поиск по статусу
    List<Book> findByStatus(BookStatus status);
    Page<Book> findByStatus(BookStatus status, Pageable pageable);
    
    // Поиск по жанру
    Page<Book> findByGenreIdAndStatus(Long genreId, BookStatus status, Pageable pageable);
    
    // Поиск опубликованных книг
    Page<Book> findByStatusOrderByPublishedAtDesc(BookStatus status, Pageable pageable);
    
    // Поиск по названию (case-insensitive)
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')) AND b.status = 'PUBLISHED'")
    Page<Book> searchByTitle(@Param("title") String title, Pageable pageable);
    
    // Топ книг по просмотрам
    @Query("SELECT b FROM Book b WHERE b.status = 'PUBLISHED' ORDER BY b.viewsCount DESC")
    Page<Book> findTopBooksByViews(Pageable pageable);
    
    // Топ книг по рейтингу
    @Query("SELECT b FROM Book b WHERE b.status = 'PUBLISHED' ORDER BY SIZE(b.reviews) DESC")
    Page<Book> findTopBooksByReviews(Pageable pageable);
    
    // Книги на модерации
    @Query("SELECT b FROM Book b WHERE b.status = 'MODERATION' ORDER BY b.createdAt ASC")
    List<Book> findBooksForModeration();
    
    // Статистика по автору
    @Query("SELECT COUNT(b) FROM Book b WHERE b.author = :author AND b.status = 'PUBLISHED'")
    long countPublishedBooksByAuthor(@Param("author") User author);
    
    // Обновление статуса
    @Modifying
    @Query("UPDATE Book b SET b.status = :status, b.publishedAt = :publishedAt WHERE b.id = :id")
    void updateBookStatus(@Param("id") Long id, @Param("status") BookStatus status, @Param("publishedAt") LocalDateTime publishedAt);
}