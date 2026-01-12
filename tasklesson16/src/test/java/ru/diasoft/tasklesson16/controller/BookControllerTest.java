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
import ru.diasoft.tasklesson16.controller.dto.BookDto;
import ru.diasoft.tasklesson16.controller.dto.GenreDto;
import ru.diasoft.tasklesson16.domain.Author;
import ru.diasoft.tasklesson16.domain.Book;
import ru.diasoft.tasklesson16.domain.Genre;
import ru.diasoft.tasklesson16.service.BookService;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@Import(TestConfig.class)
class BookControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    private final Author testAuthor = Author.builder()
            .id(1L)
            .firstName("Test")
            .lastName("Author")
            .age(40)
            .build();
            
    private final Genre testGenre = Genre.builder()
            .id(1L)
            .name("Test Genre")
            .build();
            
    private final Book testBook = Book.builder()
            .id(1L)
            .title("Test Book")
            .author(testAuthor)
            .genre(testGenre)
            .comments(List.of())
            .build();
    
    private final AuthorDto testAuthorDto = AuthorDto.builder()
            .id(1L)
            .firstName("Test")
            .lastName("Author")
            .age(40)
            .build();
            
    private final GenreDto testGenreDto = GenreDto.builder()
            .id(1L)
            .name("Test Genre")
            .build();
            
    private final BookDto testBookDto = BookDto.builder()
            .title("Test Book")
            .author(testAuthorDto)
            .genre(testGenreDto)
            .build();

    @Test
    void shouldGetAllBooks() throws Exception {
        when(bookService.getAllBooks()).thenReturn(List.of(testBook));
        
        mvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Test Book"));
                
        verify(bookService, times(1)).getAllBooks();
    }

    @Test
    void shouldGetBookById() throws Exception {
        when(bookService.getBookById(1L)).thenReturn(Optional.of(testBook));
        
        mvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Book"));
                
        verify(bookService, times(1)).getBookById(1L);
    }

    @Test
    void shouldReturnNotFoundWhenBookNotExists() throws Exception {
        when(bookService.getBookById(999L)).thenReturn(Optional.empty());
        
        mvc.perform(get("/api/books/999"))
                .andExpect(status().isNotFound());
                
        verify(bookService, times(1)).getBookById(999L);
    }

    @Test
    void shouldCreateBook() throws Exception {
        when(bookService.createBook("Test Book", "Test", "Author", "Test Genre"))
            .thenReturn(testBook);
        
        mvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(testBookDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.author.firstName").value("Test"))
                .andExpect(jsonPath("$.genre.name").value("Test Genre"));
                
        verify(bookService, times(1))
            .createBook("Test Book", "Test", "Author", "Test Genre");
    }

    @Test
    void shouldDeleteBook() throws Exception {
        doNothing().when(bookService).deleteBook(1L);
        
        mvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());
                
        verify(bookService, times(1)).deleteBook(1L);
    }
}
