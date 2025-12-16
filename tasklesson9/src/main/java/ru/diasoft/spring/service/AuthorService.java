package ru.diasoft.spring.service;

import ru.diasoft.spring.domain.Author;
import java.util.List;
import java.util.Optional;

public interface AuthorService {
    List<Author> getAllAuthors();
    Optional<Author> getAuthorById(Long id);
    Author createAuthor(String firstName, String lastName, Integer age);
    Author updateAuthor(Long id, String firstName, String lastName, Integer age);
    void deleteAuthor(Long id);
    Optional<Author> findAuthorByFullName(String firstName, String lastName);
}