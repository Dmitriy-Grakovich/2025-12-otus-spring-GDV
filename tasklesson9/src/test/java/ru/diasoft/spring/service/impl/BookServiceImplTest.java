package ru.diasoft.spring.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.Commit;
import ru.diasoft.spring.dao.AuthorDao;
import ru.diasoft.spring.dao.BookDao;
import ru.diasoft.spring.dao.CommentDao;
import ru.diasoft.spring.dao.GenreDao;
import ru.diasoft.spring.domain.Author;
import ru.diasoft.spring.domain.Book;
import ru.diasoft.spring.domain.Comment;
import ru.diasoft.spring.domain.Genre;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тестирование BookServiceImpl")
class BookServiceImplTest {

    @Mock
    private BookDao bookDao;

    @Mock
    private AuthorDao authorDao;

    @Mock
    private GenreDao genreDao;

    @Mock
    private CommentDao commentDao;

    private BookServiceImpl bookService;

    private Author testAuthor;
    private Genre testGenre;
    private Book testBook;
    private List<Comment> comments;

    @BeforeEach
    void setUp() {
        bookService = new BookServiceImpl(bookDao, authorDao, genreDao, commentDao);

        testAuthor = new Author(1L, "Leo", "Tolstoy", 82);
        testGenre = new Genre(1L, "Novel");
        testBook = new Book(1L, "War and Peace", testAuthor, testGenre, comments);
    }

    @Test
    @DisplayName("Должен получить все книги")
    void shouldGetAllBooks() {
        // Given
        when(bookDao.findAll()).thenReturn(Collections.singletonList(testBook));

        // When
        List<Book> books = bookService.getAllBooks();

        // Then
        assertThat(books).hasSize(1);
        assertThat(books.get(0)).isEqualTo(testBook);
        verify(bookDao).findAll();
    }

    @Test
    @DisplayName("Должен получить книгу по ID")
    void shouldGetBookById() {
        // Given
        Long bookId = 1L;
        when(bookDao.findById(bookId)).thenReturn(Optional.of(testBook));

        // When
        Optional<Book> book = bookService.getBookById(bookId);

        // Then
        assertThat(book).isPresent();
        assertThat(book.get()).isEqualTo(testBook);
        verify(bookDao).findById(bookId);
    }

    @Test
    @DisplayName("Должен создать книгу с существующим автором и жанром")
    void shouldCreateBookWithExistingAuthorAndGenre() {
        // Given
        String title = "War and Peace";
        String authorFirstName = "Leo";
        String authorLastName = "Tolstoy";
        String genreName = "Novel";

        when(authorDao.findByFullName(authorFirstName, authorLastName))
                .thenReturn(Optional.of(testAuthor));
        when(genreDao.findByName(genreName))
                .thenReturn(Optional.of(testGenre));
        when(bookDao.save(any(Book.class))).thenReturn(testBook);

        // When
        Book createdBook = bookService.createBook(title, authorFirstName, authorLastName, genreName);

        // Then
        assertThat(createdBook).isEqualTo(testBook);
        verify(authorDao).findByFullName(authorFirstName, authorLastName);
        verify(genreDao).findByName(genreName);
        verify(bookDao).save(any(Book.class));
        verify(authorDao, never()).save(any(Author.class));
        verify(genreDao, never()).save(any(Genre.class));
    }

    @Test
    @DisplayName("Должен создать книгу с новым автором и существующим жанром")
    void shouldCreateBookWithNewAuthorAndExistingGenre() {
        // Given
        String title = "New Book";
        String authorFirstName = "New";
        String authorLastName = "Author";
        String genreName = "Novel";

        Author newAuthor = new Author(null, authorFirstName, authorLastName, null);
        Author savedAuthor = new Author(2L, authorFirstName, authorLastName, null);
        Book expectedBook = new Book(2L, title, savedAuthor, testGenre, comments);

        when(authorDao.findByFullName(authorFirstName, authorLastName))
                .thenReturn(Optional.empty());
        when(authorDao.save(any(Author.class))).thenReturn(savedAuthor);
        when(genreDao.findByName(genreName))
                .thenReturn(Optional.of(testGenre));
        when(bookDao.save(any(Book.class))).thenReturn(expectedBook);

        // When
        Book createdBook = bookService.createBook(title, authorFirstName, authorLastName, genreName);

        // Then
        assertThat(createdBook).isEqualTo(expectedBook);
        verify(authorDao).findByFullName(authorFirstName, authorLastName);
        verify(authorDao).save(any(Author.class));
        verify(genreDao).findByName(genreName);
        verify(genreDao, never()).save(any(Genre.class));
    }

    @Test
    @DisplayName("Должен создать книгу с существующим автором и новым жанром")
    void shouldCreateBookWithExistingAuthorAndNewGenre() {
        // Given
        String title = "New Book";
        String authorFirstName = "Leo";
        String authorLastName = "Tolstoy";
        String genreName = "New Genre";

        Genre newGenre = new Genre(null, genreName);
        Genre savedGenre = new Genre(2L, genreName);
        Book expectedBook = new Book(2L, title, testAuthor, savedGenre, comments);

        when(authorDao.findByFullName(authorFirstName, authorLastName))
                .thenReturn(Optional.of(testAuthor));
        when(genreDao.findByName(genreName))
                .thenReturn(Optional.empty());
        when(genreDao.save(any(Genre.class))).thenReturn(savedGenre);
        when(bookDao.save(any(Book.class))).thenReturn(expectedBook);

        // When
        Book createdBook = bookService.createBook(title, authorFirstName, authorLastName, genreName);

        // Then
        assertThat(createdBook).isEqualTo(expectedBook);
        verify(authorDao).findByFullName(authorFirstName, authorLastName);
        verify(authorDao, never()).save(any(Author.class));
        verify(genreDao).findByName(genreName);
        verify(genreDao).save(any(Genre.class));
    }

    @Test
    @DisplayName("Должен обновить книгу")
    void shouldUpdateBook() {
        // Given
        Long bookId = 1L;
        String newTitle = "Updated Title";
        String newAuthorFirstName = "Fyodor";
        String newAuthorLastName = "Dostoevsky";
        String newGenreName = "Philosophy";

        Author newAuthor = new Author(2L, newAuthorFirstName, newAuthorLastName, 59);
        Genre newGenre = new Genre(2L, newGenreName);
        Book updatedBook = new Book(bookId, newTitle, newAuthor, newGenre, comments);

        when(bookDao.findById(bookId)).thenReturn(Optional.of(testBook));
        when(authorDao.findByFullName(newAuthorFirstName, newAuthorLastName))
                .thenReturn(Optional.of(newAuthor));
        when(genreDao.findByName(newGenreName))
                .thenReturn(Optional.of(newGenre));

        // When
        Book result = bookService.updateBook(bookId, newTitle, newAuthorFirstName, newAuthorLastName, newGenreName);

        // Then
        assertThat(result.getId()).isEqualTo(bookId);
        assertThat(result.getTitle()).isEqualTo(newTitle);
        assertThat(result.getAuthor()).isEqualTo(newAuthor);
        assertThat(result.getGenre()).isEqualTo(newGenre);
        verify(bookDao).update(any(Book.class));
    }

    @Test
    @DisplayName("Должен бросить исключение при обновлении несуществующей книги")
    void shouldThrowExceptionWhenUpdatingNonExistingBook() {
        // Given
        Long nonExistingBookId = 999L;
        when(bookDao.findById(nonExistingBookId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> bookService.updateBook(nonExistingBookId, "Title", "Author", "Last", "Genre"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Book not found");
    }

    @Test
    @DisplayName("Должен удалить книгу")
    void shouldDeleteBook() {
        // Given
        Long bookId = 1L;

        // When
        bookService.deleteBook(bookId);

        // Then
        verify(bookDao).deleteById(bookId);
    }

    @Test
    @DisplayName("Должен найти книги по названию")
    void shouldFindBooksByTitle() {
        // Given
        String title = "War";
        when(bookDao.findByTitle(title)).thenReturn(Collections.singletonList(testBook));

        // When
        List<Book> books = bookService.findBooksByTitle(title);

        // Then
        assertThat(books).hasSize(1);
        assertThat(books.get(0)).isEqualTo(testBook);
        verify(bookDao).findByTitle(title);
    }

    @Test
    @DisplayName("Должен найти книги по автору")
    void shouldFindBooksByAuthor() {
        // Given
        String firstName = "Leo";
        String lastName = "Tolstoy";
        when(authorDao.findByFullName(firstName, lastName)).thenReturn(Optional.of(testAuthor));
        when(bookDao.findByAuthorId(testAuthor.getId())).thenReturn(Collections.singletonList(testBook));

        // When
        List<Book> books = bookService.findBooksByAuthor(firstName, lastName);

        // Then
        assertThat(books).hasSize(1);
        assertThat(books.get(0)).isEqualTo(testBook);
        verify(authorDao).findByFullName(firstName, lastName);
        verify(bookDao).findByAuthorId(testAuthor.getId());
    }

    @Test
    @DisplayName("Должен вернуть пустой список если автор не найден при поиске книг")
    void shouldReturnEmptyListWhenAuthorNotFoundForBookSearch() {
        // Given
        String firstName = "Non";
        String lastName = "Existing";
        when(authorDao.findByFullName(firstName, lastName)).thenReturn(Optional.empty());

        // When
        List<Book> books = bookService.findBooksByAuthor(firstName, lastName);

        // Then
        assertThat(books).isEmpty();
        verify(authorDao).findByFullName(firstName, lastName);
        verify(bookDao, never()).findByAuthorId(any());
    }

    @Test
    @DisplayName("Должен найти книги по жанру")
    void shouldFindBooksByGenre() {
        // Given
        String genreName = "Novel";
        when(genreDao.findByName(genreName)).thenReturn(Optional.of(testGenre));
        when(bookDao.findByGenreId(testGenre.getId())).thenReturn(Collections.singletonList(testBook));

        // When
        List<Book> books = bookService.findBooksByGenre(genreName);

        // Then
        assertThat(books).hasSize(1);
        assertThat(books.get(0)).isEqualTo(testBook);
        verify(genreDao).findByName(genreName);
        verify(bookDao).findByGenreId(testGenre.getId());
    }

    @Test
    @DisplayName("Должен создать книгу с готовыми объектами")
    void shouldCreateBookWithObjects() {
        // Given
        String title = "New Book";
        when(bookDao.save(any(Book.class))).thenReturn(testBook);

        // When
        Book createdBook = bookService.createBookWithObjects(title, testAuthor, testGenre);

        // Then
        assertThat(createdBook).isEqualTo(testBook);
        verify(bookDao).save(any(Book.class));
    }
}