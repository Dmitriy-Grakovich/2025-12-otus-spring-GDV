package ru.diasoft.spring.dao;


import ru.diasoft.spring.domain.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorDao {
    List<Author> findAll();
    Optional<Author> findById(Long id);
    Author save(Author author);
    void update(Author author);
    void deleteById(Long id);
    Optional<Author> findByFullName(String firstName, String lastName);
}