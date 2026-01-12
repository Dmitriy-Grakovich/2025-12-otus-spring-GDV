package ru.diasoft.tasklesson16.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.diasoft.tasklesson16.domain.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    
    @EntityGraph(attributePaths = {"author", "genre", "comments"})
    @Query("SELECT b FROM Book b")
    List<Book> findAllWithDetails();
    
    @EntityGraph(attributePaths = {"author", "genre", "comments"})
    @Query("SELECT b FROM Book b WHERE b.id = :id")
    Optional<Book> findByIdWithDetails(@Param("id") Long id);
    
    @EntityGraph(attributePaths = {"author", "genre"})
    Optional<Book> findByTitle(String title);
    
    @EntityGraph(attributePaths = {"author", "genre"})
    List<Book> findByTitleContainingIgnoreCase(String title);
    
    @EntityGraph(attributePaths = {"author", "genre"})
    List<Book> findByAuthor_LastNameContainingIgnoreCase(String authorName);
    
    @EntityGraph(attributePaths = {"author", "genre"})
    List<Book> findByGenre_NameContainingIgnoreCase(String genreName);

    @EntityGraph(attributePaths = {"author", "genre"})
    List<Book> findByAuthorId(Long id);
}
