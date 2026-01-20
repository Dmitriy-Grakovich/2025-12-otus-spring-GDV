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
import ru.diasoft.tasklesson16.controller.dto.AuthorDto;
import ru.diasoft.tasklesson16.domain.Author;
import ru.diasoft.tasklesson16.service.AuthorService;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthorController.class)
@Import(TestConfig.class)
class AuthorControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthorService authorService;

    @Autowired
    private ObjectMapper objectMapper;

    private final Author testAuthor = Author.builder()
            .id(1L)
            .firstName("Test")
            .lastName("Author")
            .age(40)
            .build();
            
    private final AuthorDto testAuthorDto = AuthorDto.builder()
            .id(1L)
            .firstName("Test")
            .lastName("Author")
            .age(40)
            .build();

    @Test
    void shouldGetAllAuthors() throws Exception {
        when(authorService.getAllAuthors()).thenReturn(List.of(testAuthor));
        
        mvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName").value("Test"))
                .andExpect(jsonPath("$[0].lastName").value("Author"));
                
        verify(authorService, times(1)).getAllAuthors();
    }

    @Test
    void shouldGetAuthorById() throws Exception {
        when(authorService.getAuthorById(1L)).thenReturn(Optional.of(testAuthor));
        
        mvc.perform(get("/api/authors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Test"));
                
        verify(authorService, times(1)).getAuthorById(1L);
    }

    @Test
    void shouldReturnNotFoundWhenAuthorNotExists() throws Exception {
        when(authorService.getAuthorById(999L)).thenReturn(Optional.empty());
        
        mvc.perform(get("/api/authors/999"))
                .andExpect(status().isNotFound());
                
        verify(authorService, times(1)).getAuthorById(999L);
    }

    @Test
    void shouldCreateAuthor() throws Exception {
        when(authorService.createAuthor("Test", "Author", 40)).thenReturn(testAuthor);
        
        mvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(testAuthorDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("Author"));
                
        verify(authorService, times(1)).createAuthor("Test", "Author", 40);
    }

    @Test
    void shouldUpdateAuthor() throws Exception {
        Author updatedAuthor = Author.builder()
                .id(1L)
                .firstName("Updated")
                .lastName("Author")
                .age(40)
                .build();
        AuthorDto updatedDto = AuthorDto.builder()
                .firstName("Updated")
                .lastName("Author")
                .age(40)
                .build();
                
        when(authorService.updateAuthor(1L, "Updated", "Author", 40)).thenReturn(Optional.of(updatedAuthor));
        
        mvc.perform(put("/api/authors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"));
                
        verify(authorService, times(1)).updateAuthor(1L, "Updated", "Author", 40);
    }

    @Test
    void shouldDeleteAuthor() throws Exception {
        doNothing().when(authorService).deleteAuthor(1L);
        
        mvc.perform(delete("/api/authors/1"))
                .andExpect(status().isNoContent());
                
        verify(authorService, times(1)).deleteAuthor(1L);
    }
}
