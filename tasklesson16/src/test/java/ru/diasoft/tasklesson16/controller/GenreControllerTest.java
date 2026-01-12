package ru.diasoft.tasklesson16.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.diasoft.tasklesson16.config.TestConfig;
import ru.diasoft.tasklesson16.controller.dto.GenreDto;
import ru.diasoft.tasklesson16.domain.Genre;
import ru.diasoft.tasklesson16.service.GenreService;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GenreController.class)
@Import(TestConfig.class)
class GenreControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private GenreService genreService;

    @Autowired
    private ObjectMapper objectMapper;

    private final Genre testGenre = new Genre(1L, "Test Genre");
    private final GenreDto testGenreDto = GenreDto.builder()
            .name("Test Genre")
            .build();

    @Test
    void shouldGetAllGenres() throws Exception {
        when(genreService.getAllGenres()).thenReturn(List.of(testGenre));

        mvc.perform(get("/api/genres"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Test Genre"));

        verify(genreService, times(1)).getAllGenres();
    }

    @Test
    void shouldGetGenreById() throws Exception {
        when(genreService.getGenreById(1L)).thenReturn(Optional.of(testGenre));

        mvc.perform(get("/api/genres/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Genre"));

        verify(genreService, times(1)).getGenreById(1L);
    }

    @Test
    void shouldReturnNotFoundWhenGenreNotExists() throws Exception {
        when(genreService.getGenreById(999L)).thenReturn(Optional.empty());

        mvc.perform(get("/api/genres/999"))
                .andExpect(status().isNotFound());

        verify(genreService, times(1)).getGenreById(999L);
    }

    @Test
    void shouldCreateGenre() throws Exception {
        when(genreService.createGenre("Test Genre")).thenReturn(testGenre);

        mvc.perform(post("/api/genres")
                        .param("name", "Test Genre")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Genre"));

        verify(genreService, times(1)).createGenre("Test Genre");
    }

    @Test
    void shouldDeleteGenre() throws Exception {
        doNothing().when(genreService).deleteGenre(1L);

        mvc.perform(delete("/api/genres/1"))
                .andExpect(status().isNoContent());

        verify(genreService, times(1)).deleteGenre(1L);
    }
}