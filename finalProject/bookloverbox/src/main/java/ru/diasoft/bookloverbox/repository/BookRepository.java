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
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    // Поиск по автору
    List<Book> findByAuthor(User author);
    Page<Book> findByAuthor(User author, Pageable pageable);
    
    // Поиск по статусу
    List<Book> findByStatus(BookStatus status);
    Page<Book> findByStatus(BookStatus status, Pageable pageable);
    
    // Поиск по статусу с JOIN FETCH (предотвращает N+1 для author и genre)
    @Query("SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.author LEFT JOIN FETCH b.genre WHERE b.status = :status")
    List<Book> findByStatusWithAssociations(@Param("status") BookStatus status);
    
    // Пагинированные версии с явным countQuery (обход ограничения FETCH + Page)
    @Query(value = "SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.author LEFT JOIN FETCH b.genre WHERE b.status = :status ORDER BY b.publishedAt DESC",
           countQuery = "SELECT COUNT(DISTINCT b) FROM Book b WHERE b.status = :status")
    Page<Book> findByStatusOrderByPublishedAtDescWithFetch(@Param("status") BookStatus status, Pageable pageable);
    
    // Поиск опубликованных книг с ассоциациями (для convertToDto без N+1)
    @Query("SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.author LEFT JOIN FETCH b.genre WHERE b.id = :id")
    Optional<Book> findByIdWithAssociations(@Param("id") Long id);
    
    // Книги автора с ассоциациями (пагинированный с явным countQuery)
    @Query(value = "SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.author LEFT JOIN FETCH b.genre WHERE b.author = :author",
           countQuery = "SELECT COUNT(DISTINCT b) FROM Book b WHERE b.author = :author")
    Page<Book> findByAuthorWithFetch(@Param("author") User author, Pageable pageable);
    
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
    
    // Подсчет книг по автору
    long countByAuthor(User author);
    
    // Поиск книг по автору и статусу
    List<Book> findByAuthorAndStatus(User author, BookStatus status);
}
