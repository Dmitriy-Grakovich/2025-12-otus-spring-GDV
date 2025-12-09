package ru.diasoft.spring.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.diasoft.spring.dao.GenreDao;
import ru.diasoft.spring.domain.Genre;
import ru.diasoft.spring.service.GenreService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    
    private final GenreDao genreDao;
    
    @Override
    public List<Genre> getAllGenres() {
        return genreDao.findAll();
    }
    
    @Override
    public Optional<Genre> getGenreById(Long id) {
        return genreDao.findById(id);
    }
    
    @Override
    @Transactional
    public Genre createGenre(String name) {
        Genre genre = new Genre();
        genre.setName(name);
        return genreDao.save(genre);
    }
    
    @Override
    @Transactional
    public Genre updateGenre(Long id, String name) {
        Genre genre = genreDao.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Genre not found with id: " + id));
        
        genre.setName(name);
        genreDao.update(genre);
        return genre;
    }
    
    @Override
    @Transactional
    public void deleteGenre(Long id) {
        genreDao.deleteById(id);
    }
    
    @Override
    public Optional<Genre> findGenreByName(String name) {
        return genreDao.findByName(name);
    }
}