package ru.diasoft.bookloverbox.services;

import ru.diasoft.bookloverbox.domain.Genre;
import ru.diasoft.bookloverbox.dto.GenreDto;

import java.util.List;

public interface IGenreService {
    
    Genre createGenre(GenreDto dto);
    
    List<GenreDto> getAllGenres();
    
    GenreDto getGenreById(Long id);
    
    GenreDto updateGenre(Long id, GenreDto dto);
    
    void deleteGenre(Long id);
}
