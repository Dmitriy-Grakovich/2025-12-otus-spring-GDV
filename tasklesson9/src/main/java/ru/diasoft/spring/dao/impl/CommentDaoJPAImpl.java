package ru.diasoft.spring.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import ru.diasoft.spring.dao.CommentDao;
import ru.diasoft.spring.domain.Comment;

import java.util.List;
import java.util.Optional;
@Repository

public class CommentDaoJPAImpl implements CommentDao {
    @PersistenceContext
    private EntityManager em;


    @Override
    public List<Comment> findAll() {
        TypedQuery<Comment> query = em.createQuery("select s from Comment s", Comment.class);
        return query.getResultList();
    }

    @Override
    public Optional<Comment> findById(Long id) {
        return Optional.ofNullable(em.find(Comment.class, id));
    }

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == 0) {
            em.persist(comment);
            em.flush();
            return comment;
        }
        return em.merge(comment);
    }

    @Override
    public void update(Comment comment) {
        Query query = em.createQuery("update Comment  s set s.description = :description, s.nickname = :nickname where s.id = :id");
        query.setParameter("description", comment.getDescription());
        query.setParameter("nickname", comment.getNickname());
        query.setParameter("id", comment.getId());
        query.executeUpdate();

    }

    @Override
    public void deleteById(Long id) {
        Query query = em.createQuery("delete  Comment  s where s.id = :id");
        query.setParameter("id", id);
        query.executeUpdate();
    }

    @Override
    public List<Comment> findByNickname(String nickname) {
        TypedQuery<Comment> query = em.createQuery("select s from Comment s where s.nickname = :nickname ", Comment.class);
        query.setParameter("nickname", nickname);
        return query.getResultList();
    }

    @Override
    public List<Comment> findByBookId(Long bookId) {
        TypedQuery<Comment> query = em.createQuery(
                "select c from Comment c join c.book b where b.id = :bookId", Comment.class
        );
        query.setParameter("bookId", bookId);
        return query.getResultList();
    }


}
