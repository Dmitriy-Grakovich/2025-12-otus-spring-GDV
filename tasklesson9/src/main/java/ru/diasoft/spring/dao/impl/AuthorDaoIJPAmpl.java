package ru.diasoft.spring.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.diasoft.spring.dao.AuthorDao;
import ru.diasoft.spring.domain.Author;

import java.util.List;
import java.util.Optional;
@Repository
@Qualifier("AuthorDaoIJPAmpl")
public class AuthorDaoIJPAmpl implements AuthorDao {
    @PersistenceContext
    private EntityManager em;



    @Override
    public List<Author> findAll() {
        TypedQuery<Author> query = em.createQuery("select s from Author s", Author.class);
        return query.getResultList();
    }

    @Override
    public Optional<Author> findById(Long id) {

        return Optional.ofNullable(em.find(Author.class, id));

    }

    @Override
    public Author save(Author author) {
        if (author.getId() == null) {
            em.persist(author);
            em.flush();
            return author;
        }
        return em.merge(author);
    }



    @Override
    public void update(Author author) {
        Query query = em.createQuery("update Author  s set s.lastName = :lastName, s.firstName = :firstName, s.age = :age where s.id = :id");
        query.setParameter("lastName", author.getLastName());
        query.setParameter("firstName", author.getFirstName());
        query.setParameter("age", author.getAge());
        query.setParameter("id", author.getId());
        query.executeUpdate();
    }

    @Override
    public void deleteById(Long id) {
        Query query = em.createQuery("delete  Author  s where s.id = :id");
        query.setParameter("id", id);
        query.executeUpdate();
    }

    @Override
    public Optional<Author> findByFullName(String firstName, String lastName) {
        try {
            TypedQuery<Author> query = em.createQuery(
                    "select s from Author s where s.lastName = :lastName and s.firstName = :firstName",
                    Author.class
            );
            query.setParameter("lastName", lastName);
            query.setParameter("firstName", firstName);
            return Optional.ofNullable(query.getSingleResult());
        } catch (jakarta.persistence.NoResultException e) {
            return Optional.empty();
        } catch (jakarta.persistence.NonUniqueResultException e) {
            // Если найдено несколько авторов, возвращаем первого
            TypedQuery<Author> query = em.createQuery(
                    "select s from Author s where s.lastName = :lastName and s.firstName = :firstName",
                    Author.class
            );
            query.setParameter("lastName", lastName);
            query.setParameter("firstName", firstName);
            List<Author> authors = query.getResultList();
            return authors.isEmpty() ? Optional.empty() : Optional.of(authors.get(0));
        }
    }
}
