package ru.diasoft.tasklesson16.service;

import ru.diasoft.tasklesson16.domain.Genre;
import java.util.List;
import java.util.Optional;

public interface GenreService {
    List<Genre> getAllGenres();
    Optional<Genre> getGenreById(Long id);
    Genre createGenre(String name);
    Genre updateGenre(Long id, String name);
    void deleteGenre(Long id);
    Optional<Genre> findGenreByName(String name);
}