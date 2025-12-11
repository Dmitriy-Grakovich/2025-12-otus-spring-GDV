package ru.diasoft.spring.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.diasoft.spring.dao.BookDao;
import ru.diasoft.spring.domain.Book;

import java.util.List;
import java.util.Optional;
@Repository
@Qualifier("BookDaoJPAImpl")
public class BookDaoJPAImpl implements BookDao {
    @PersistenceContext
    private EntityManager em;
    @Override
    public List<Book> findAll() {
        TypedQuery<Book> query = em.createQuery("select s from Book s", Book.class);
        return query.getResultList();
    }

    @Override
    public Optional<Book> findById(Long id) {
        return Optional.ofNullable(em.find(Book.class, id));
    }

    @Override
    public Book save(Book book) {
        if(book.getId()==0){
            em.persist(book);
            em.flush();
            return book;
        }
        return em.merge(book);
    }

    @Override
    public void update(Book book) {
        Query query = em.createQuery("update Book  s set s.title = :title where s.id = :id");
        query.setParameter("title", book.getTitle());
        query.setParameter("id", book.getId());
        query.executeUpdate();
    }

    @Override
    public void deleteById(Long id) {
        Query query = em.createQuery("delete  Book  s where s.id = :id");
        query.setParameter("id", id);
        query.executeUpdate();
    }

    @Override
    public List<Book> findByTitle(String title) {
        TypedQuery<Book> query = em.createQuery("select s from Book s where s.title = :title", Book.class);
        query.setParameter("title", title);
        return query.getResultList();
    }

    @Override
    public List<Book> findByAuthorId(Long authorId) {
        TypedQuery<Book> query = em.createQuery("select s from Book s where s.author = :authorId", Book.class);
        query.setParameter("authorId", authorId);
        return query.getResultList();
    }

    @Override
    public List<Book> findByGenreId(Long genreId) {
        TypedQuery<Book> query = em.createQuery("select s from Book s where s.genre = :genreId", Book.class);
        query.setParameter("genreId", genreId);
        return query.getResultList();
    }
}
