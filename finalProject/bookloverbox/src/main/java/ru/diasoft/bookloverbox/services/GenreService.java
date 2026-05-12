package ru.diasoft.bookloverbox.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.diasoft.bookloverbox.domain.Genre;
import ru.diasoft.bookloverbox.dto.GenreDto;
import ru.diasoft.bookloverbox.repository.GenreRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreService {
    
    private final GenreRepository genreRepository;
    
    @Transactional
    public Genre createGenre(GenreDto dto) {
        if (genreRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Жанр с таким названием уже существует");
        }
        
        Genre genre = new Genre();
        genre.setName(dto.getName());
        genre.setDescription(dto.getDescription());
        
        return genreRepository.save(genre);
    }
    
    public List<GenreDto> getAllGenres() {
        return genreRepository.findAll().stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    public GenreDto getGenreById(Long id) {
        Genre genre = genreRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Жанр не найден"));
        return convertToDto(genre);
    }
    
    @Transactional
    public GenreDto updateGenre(Long id, GenreDto dto) {
        Genre genre = genreRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Жанр не найден"));
        
        genre.setName(dto.getName());
        genre.setDescription(dto.getDescription());
        
        return convertToDto(genreRepository.save(genre));
    }
    
    @Transactional
    public void deleteGenre(Long id) {
        Genre genre = genreRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Жанр не найден"));
        
        if (!genre.getBooks().isEmpty()) {
            throw new RuntimeException("Нельзя удалить жанр, у которого есть книги");
        }
        
        genreRepository.delete(genre);
    }
    
    private GenreDto convertToDto(Genre genre) {
        GenreDto dto = new GenreDto();
        dto.setId(genre.getId());
        dto.setName(genre.getName());
        dto.setDescription(genre.getDescription());
        dto.setBooksCount(genre.getBooks().size());
        return dto;
    }
}