package ru.diasoft.tasklesson16.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.diasoft.tasklesson16.domain.Author;
import ru.diasoft.tasklesson16.domain.Book;
import ru.diasoft.tasklesson16.domain.Comment;
import ru.diasoft.tasklesson16.domain.Genre;
import ru.diasoft.tasklesson16.repository.AuthorRepository;
import ru.diasoft.tasklesson16.repository.BookRepository;
import ru.diasoft.tasklesson16.repository.CommentRepository;
import ru.diasoft.tasklesson16.repository.GenreRepository;
import ru.diasoft.tasklesson16.service.impl.BookServiceImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тестирование BookServiceImpl")
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Author testAuthor;
    private Genre testGenre;
    private Book testBook;
    private List<Comment> comments;

    @BeforeEach
    void setUp() {
        testAuthor = new Author(1L, "Leo", "Tolstoy", 82);
        testGenre = new Genre(1L, "Novel");
        testBook = new Book(1L, "War and Peace", testAuthor, testGenre, comments);
    }

    @Test
    @DisplayName("Должен получить все книги с деталями")
    void shouldGetAllBooksWithDetails() {
        // Given
        when(bookRepository.findAllWithDetails()).thenReturn(Collections.singletonList(testBook));

        // When
        List<Book> books = bookService.getAllBooks();

        // Then
        assertThat(books)
            .hasSize(1)
            .containsExactly(testBook);
        verify(bookRepository).findAllWithDetails();
    }

    @Test
    @DisplayName("Должен найти книгу по ID с деталями")
    void shouldGetBookByIdWithDetails() {
        // Given
        Long bookId = 1L;
        when(bookRepository.findByIdWithDetails(bookId)).thenReturn(Optional.of(testBook));

        // When
        Optional<Book> book = bookService.getBookById(bookId);

        // Then
        assertThat(book)
            .isPresent()
            .contains(testBook);
        verify(bookRepository).findByIdWithDetails(bookId);
    }

    @Test
    @DisplayName("Должен создать книгу с существующим автором и жанром")
    void shouldCreateBookWithExistingAuthorAndGenre() {
        // Given
        String title = "War and Peace";
        String authorFirstName = "Leo";
        String authorLastName = "Tolstoy";
        String genreName = "Novel";

        when(authorRepository.findByFirstNameAndLastName(authorFirstName, authorLastName))
            .thenReturn(Optional.of(testAuthor));
        when(genreRepository.findByName(genreName))
            .thenReturn(Optional.of(testGenre));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // When
        Book createdBook = bookService.createBook(title, authorFirstName, authorLastName, genreName);

        // Then
        assertThat(createdBook).isEqualTo(testBook);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    @DisplayName("Должен удалить книгу и её комментарии")
    void shouldDeleteBookAndItsComments() {
        // Given
        Long bookId = 1L;
        doNothing().when(commentRepository).deleteByBookId(bookId);
        doNothing().when(bookRepository).deleteById(bookId);

        // When
        bookService.deleteBook(bookId);

        // Then
        verify(commentRepository).deleteByBookId(bookId);
        verify(bookRepository).deleteById(bookId);
    }

    @Test
    @DisplayName("Должен найти книги по названию")
    void shouldFindBooksByTitle() {
        // Given
        String title = "War";
        when(bookRepository.findByTitleContainingIgnoreCase(title))
            .thenReturn(Collections.singletonList(testBook));

        // When
        List<Book> books = bookService.findBooksByTitle(title);

        // Then
        assertThat(books)
            .hasSize(1)
            .containsExactly(testBook);
        verify(bookRepository).findByTitleContainingIgnoreCase(title);
    }

    @Test
    @DisplayName("Должен найти книги по автору")
    void shouldFindBooksByAuthor() {
        // Given
        String authorFirstName = "Leo";
        String authorLastName = "Tolstoy";
        when(authorRepository.findByFirstNameAndLastName(authorFirstName, authorLastName))
            .thenReturn(Optional.of(testAuthor));
        when(bookRepository.findByAuthorId(testAuthor.getId()))
            .thenReturn(Collections.singletonList(testBook));

        // When
        List<Book> books = bookService.findBooksByAuthor(authorFirstName, authorLastName);

        // Then
        assertThat(books)
            .hasSize(1)
            .containsExactly(testBook);
        verify(authorRepository).findByFirstNameAndLastName(authorFirstName, authorLastName);
        verify(bookRepository).findByAuthorId(testAuthor.getId());
    }



}