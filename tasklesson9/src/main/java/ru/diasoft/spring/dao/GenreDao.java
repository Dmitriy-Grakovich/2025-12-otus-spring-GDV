package ru.diasoft.spring.dao;

import ru.diasoft.spring.domain.Genre;
import java.util.List;
import java.util.Optional;

public interface GenreDao {
    List<Genre> findAll();
    Optional<Genre> findById(Long id);
    Genre save(Genre genre);
    void update(Genre genre);
    void deleteById(Long id);
    Optional<Genre> findByName(String name);
}