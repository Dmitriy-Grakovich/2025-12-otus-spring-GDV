package ru.diasoft.spring.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.diasoft.spring.dao.GenreDao;
import ru.diasoft.spring.domain.Genre;

import java.util.List;
import java.util.Optional;
@Repository
@Qualifier("GenreDaoJPAImpl")
public class GenreDaoJPAImpl implements GenreDao {
    @PersistenceContext
    private EntityManager em;
    @Override
    public List<Genre> findAll() {
        TypedQuery<Genre> query = em.createQuery("select s from Genre s", Genre.class);
        return query.getResultList();
    }

    @Override
    public Optional<Genre> findById(Long id) {
        return Optional.ofNullable(em.find(Genre.class, id));
    }

    @Override
    public Genre save(Genre genre) {
       if(genre.getId()==null){
           em.persist(genre);
           em.flush();
           return genre;
       }
       return em.merge(genre);
    }

    @Override
    public void update(Genre genre) {
        Query query = em.createQuery("update Genre  s set s.name = :name where s.id = :id");
        query.setParameter("name", genre.getName());
        query.setParameter("id", genre.getId());
        query.executeUpdate();
    }

    @Override
    public void deleteById(Long id) {
        Query query = em.createQuery("delete  Genre  s where s.id = :id");
        query.setParameter("id", id);
        query.executeUpdate();
    }

    @Override
    public Optional<Genre> findByName(String name) {
        try {
            TypedQuery<Genre> query = em.createQuery("select s from Genre s where s.name = :name", Genre.class);
            query.setParameter("name", name);
            return Optional.ofNullable(query.getSingleResult());
        } catch (jakarta.persistence.NoResultException e) {
            return Optional.empty();
        }
    }
}
