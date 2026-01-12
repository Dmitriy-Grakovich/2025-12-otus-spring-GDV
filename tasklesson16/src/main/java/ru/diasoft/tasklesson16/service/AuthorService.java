package ru.diasoft.tasklesson16.service;

import ru.diasoft.tasklesson16.domain.Author;
import java.util.List;
import java.util.Optional;

public interface AuthorService {
    List<Author> getAllAuthors();
    Optional<Author> getAuthorById(Long id);
    Author createAuthor(String firstName, String lastName, Integer age);
    Optional<Author> updateAuthor(Long id, String firstName, String lastName, Integer age);
    void deleteAuthor(Long id);
    Optional<Author> findAuthorByFullName(String firstName, String lastName);
}