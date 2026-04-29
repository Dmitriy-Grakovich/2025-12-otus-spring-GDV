package ru.diasoft.spring.dao;


import ru.diasoft.spring.domain.Book;

import java.util.List;
import java.util.Optional;

public interface BookDao {
    List<Book> findAll();
    Optional<Book> findById(Long id);
    Book save(Book book);
    void update(Book book);
    void deleteById(Long id);
    List<Book> findByTitle(String title);
    List<Book> findByAuthorId(Long authorId);
    List<Book> findByGenreId(Long genreId);

}