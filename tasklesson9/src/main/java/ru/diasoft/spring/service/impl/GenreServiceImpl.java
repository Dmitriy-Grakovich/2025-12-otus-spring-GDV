package ru.diasoft.spring.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.diasoft.spring.domain.Genre;
import ru.diasoft.spring.repository.GenreRepository;
import ru.diasoft.spring.service.GenreService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GenreServiceImpl implements GenreService {
    
    private final GenreRepository genreRepository;

    @Override
    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }
    
    @Override
    public Optional<Genre> getGenreById(Long id) {
        return genreRepository.findById(id);
    }
    
    @Override
    @Transactional
    public Genre createGenre(String name) {
        Genre genre = new Genre();
        genre.setName(name);
        return genreRepository.save(genre);
    }
    
    @Override
    @Transactional
    public Genre updateGenre(Long id, String name) {
        return genreRepository.findById(id)
            .map(genre -> {
                genre.setName(name);
                return genreRepository.save(genre);
            })
            .orElseGet(() -> {
                Genre newGenre = new Genre();
                newGenre.setId(id);
                newGenre.setName(name);
                return genreRepository.save(newGenre);
            });
    }
    
    @Override
    @Transactional
    public void deleteGenre(Long id) {
        genreRepository.deleteById(id);
    }
    
    @Override
    public Optional<Genre> findGenreByName(String name) {
        return genreRepository.findByName(name);
    }
}