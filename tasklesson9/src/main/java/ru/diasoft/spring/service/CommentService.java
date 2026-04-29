package ru.diasoft.spring.service;

import ru.diasoft.spring.domain.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    List<Comment> getAllComments();
    Optional<Comment> getCommentById(Long id);
    Comment createComment(String description, String nickname);
    Comment updateComment(Long id, String description, String nickname);
    void deleteComment(Long id);
    List<Comment> findCommentsByNickname(String nickname);
    List<Comment> findCommentsByBookId(Long bookId);

}
