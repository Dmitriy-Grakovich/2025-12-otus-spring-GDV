package ru.diasoft.tasklesson16.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.diasoft.tasklesson16.domain.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    @EntityGraph(attributePaths = {"book"})
    @Query("SELECT c FROM Comment c WHERE c.id = :id")
    Optional<Comment> findByIdWithBook(@Param("id") Long id);
    
    @EntityGraph(attributePaths = {"book"})
    @Query("SELECT c FROM Comment c WHERE c.book.id = :bookId")
    List<Comment> findByBookId(@Param("bookId") Long bookId);
    
    @EntityGraph(attributePaths = {"book"})
    List<Comment> findAll();
    
    void deleteByBookId(Long bookId);
}
