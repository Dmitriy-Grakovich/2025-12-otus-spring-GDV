package ru.diasoft.spring.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.diasoft.spring.dao.CommentDao;
import ru.diasoft.spring.domain.Comment;
import ru.diasoft.spring.service.CommentService;

import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentDao commentDao;

    public List<Comment> getAllComments() {
        return commentDao.findAll();
    }

    public Optional<Comment> getCommentById(Long id) {
        return commentDao.findById(id);
    }

    @Transactional
    public Comment createComment(String description, String nickname) {
        Comment comment = new Comment();
        comment.setDescription(description);
        comment.setNickname(nickname);
        return commentDao.save(comment);
    }

    @Transactional
    public Comment updateComment(Long id, String description, String nickname) {
        Comment comment = commentDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found with id: " + id));
        comment.setDescription(description);
        comment.setNickname(nickname);
        commentDao.update(comment);
        return comment;
    }

    @Transactional
    public void deleteComment(Long id) {
        commentDao.deleteById(id);
    }

    public List<Comment> findCommentsByNickname(String nickname) {
        return commentDao.findByNickname(nickname);
    }

    public List<Comment> findCommentsByBookId(Long bookId) {
        // Этот метод нужно будет реализовать в DAO или здесь с использованием EntityManager
        // Пока оставим заглушку
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
