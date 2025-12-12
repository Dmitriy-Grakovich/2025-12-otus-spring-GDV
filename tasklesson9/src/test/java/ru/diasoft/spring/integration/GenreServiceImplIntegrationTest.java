package ru.diasoft.spring.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.diasoft.spring.domain.Genre;
import ru.diasoft.spring.service.impl.GenreServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Интеграционные тесты для GenreServiceImpl")
class GenreServiceImplIntegrationTest {
    
    @Autowired
    private GenreServiceImpl genreService;
    

    
    @Test
    @DisplayName("Должен найти жанр по ID")
    void shouldFindGenreById() {
        // When
        Optional<Genre> genre = genreService.getGenreById(101L);
        
        // Then
        assertThat(genre).isPresent();
        assertThat(genre.get().getName()).isEqualTo("Test Genre 2");
    }
    
    @Test

    @DisplayName("Должен создать новый жанр")
    void shouldCreateNewGenre() {
        // Given
        String name = "Integration Test Genre";
        
        // When
        Genre createdGenre = genreService.createGenre(name);
        
        // Then
        assertThat(createdGenre.getId()).isNotNull();
        assertThat(createdGenre.getName()).isEqualTo(name);
        
        // Проверяем, что жанр сохранен в БД
        Optional<Genre> foundGenre = genreService.getGenreById(createdGenre.getId());
        assertThat(foundGenre).isPresent();
        assertThat(foundGenre.get().getName()).isEqualTo(name);
    }
    
    @Test
    @DisplayName("Должен найти жанр по имени")
    void shouldFindGenreByName() {
        // Given - сначала создаем жанр
        genreService.createGenre("Find Me Genre");
        
        // When
        Optional<Genre> genre = genreService.findGenreByName("Find Me Genre");
        
        // Then
        assertThat(genre).isPresent();
        assertThat(genre.get().getName()).isEqualTo("Find Me Genre");
    }
}