package ru.diasoft.tasklesson16.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.diasoft.tasklesson16.domain.Comment;
import ru.diasoft.tasklesson16.repository.CommentRepository;
import ru.diasoft.tasklesson16.service.CommentService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    @Override
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    @Override
    public Optional<Comment> getCommentById(Long id) {
        return commentRepository.findByIdWithBook(id);
    }

    @Override
    @Transactional
    public Comment createComment(String description, String nickname) {
        Comment comment = new Comment();
        comment.setDescription(description);
        comment.setNickname(nickname);
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public Comment updateComment(Long id, String description, String nickname) {
        return commentRepository.findById(id)
                .map(comment -> {
                    comment.setDescription(description);
                    comment.setNickname(nickname);
                    return commentRepository.save(comment);
                })
                .orElseGet(() -> {
                    Comment newComment = new Comment();
                    newComment.setId(id);
                    newComment.setDescription(description);
                    newComment.setNickname(nickname);
                    return commentRepository.save(newComment);
                });
    }

    @Override
    @Transactional
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    @Override
    public List<Comment> findCommentsByNickname(String nickname) {
        // This method needs to be implemented in the repository if needed
        // For now, we'll filter in memory (not efficient for large datasets)
        return commentRepository.findAll().stream()
                .filter(comment -> nickname.equalsIgnoreCase(comment.getNickname()))
                .toList();
    }

    @Override
    public List<Comment> findCommentsByBookId(Long bookId) {
        return commentRepository.findByBookId(bookId);
    }
}
