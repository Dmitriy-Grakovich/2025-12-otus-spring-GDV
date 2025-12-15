package ru.diasoft.spring.dao;

import ru.diasoft.spring.domain.Author;
import ru.diasoft.spring.domain.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentDao {
    List<Comment> findAll();
    Optional<Comment> findById(Long id);
    Comment save(Comment comment);
    void update(Comment comment);
    void deleteById(Long id);
    List<Comment> findByNickname(String nickname); // Исправлено: было findByNikeName
    List<Comment> findByBookId(Long bookId); // Добавляем новый метод
}
