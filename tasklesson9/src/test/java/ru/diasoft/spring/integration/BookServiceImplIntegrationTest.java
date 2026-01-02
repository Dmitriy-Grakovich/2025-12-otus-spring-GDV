package ru.diasoft.spring.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.diasoft.spring.domain.Author;
import ru.diasoft.spring.domain.Book;
import ru.diasoft.spring.domain.Genre;
import ru.diasoft.spring.service.AuthorService;
import ru.diasoft.spring.service.BookService;
import ru.diasoft.spring.service.GenreService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("Интеграционные тесты для BookServiceImpl")
class BookServiceImplIntegrationTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private GenreService genreService;

    @Test
    @Transactional
    @DisplayName("Должен найти все книги из тестовых данных")
    void shouldFindAllBooksFromTestData() {
        // When
        List<Book> books = bookService.getAllBooks();

        // Then
        assertThat(books).hasSize(3);
    }

    @Test
    @Transactional
    @DisplayName("Должен найти книгу по ID из тестовых данных")
    void shouldFindBookByIdFromTestData() {
        // When
        Optional<Book> book = bookService.getBookById(101L);

        // Then
        assertThat(book).isPresent();
        assertThat(book.get().getTitle()).isEqualTo("Test Book 2");
    }

    @Test
    @Transactional
    @DisplayName("Должен вернуть пустой Optional для несуществующего ID")
    void shouldReturnEmptyOptionalForNonExistingId() {
        // When
        Optional<Book> book = bookService.getBookById(999L);

        // Then
        assertThat(book).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("Должен создать новую книгу")
    void shouldCreateNewBook() {
        // Given
        String title = "New Integration Test Book";
        String authorFirstName = "Integration";
        String authorLastName = "TestAuthor";
        String genreName = "Integration Genre";

        // When
        Book createdBook = bookService.createBook(title, authorFirstName, authorLastName, genreName);

        // Then
        assertThat(createdBook.getId()).isNotNull();
        assertThat(createdBook.getTitle()).isEqualTo(title);
        assertThat(createdBook.getAuthor().getFirstName()).isEqualTo(authorFirstName);
        assertThat(createdBook.getAuthor().getLastName()).isEqualTo(authorLastName);
        assertThat(createdBook.getGenre().getName()).isEqualTo(genreName);
    }

    @Test
    @Transactional
    @DisplayName("Должен создать книгу с существующим автором и жанром")
    void shouldCreateBookWithExistingAuthorAndGenre() {
        // Given
        String title = "Another Test Book";
        String authorFirstName = "Test";
        String authorLastName = "Author1";
        String genreName = "Test Genre 1";

        // When
        Book createdBook = bookService.createBook(title, authorFirstName, authorLastName, genreName);

        // Then
        assertThat(createdBook.getId()).isNotNull();
        assertThat(createdBook.getTitle()).isEqualTo(title);
        assertThat(createdBook.getAuthor().getFirstName()).isEqualTo(authorFirstName);
        assertThat(createdBook.getAuthor().getLastName()).isEqualTo(authorLastName);
        assertThat(createdBook.getGenre().getName()).isEqualTo(genreName);
    }

    @Test
    @Transactional
    @DisplayName("Должен найти книги по точному названию")
    void shouldFindBooksByExactTitle() {
        // When
        List<Book> books = bookService.findBooksByTitle("Test Book 1");

        // Then
        assertThat(books).hasSize(1);
        assertThat(books.get(0).getTitle()).isEqualTo("Test Book 1");
    }

    @Test
    @Transactional
    @DisplayName("Должен вернуть пустой список при поиске несуществующего названия")
    void shouldReturnEmptyListForNonExistingTitle() {
        // When
        List<Book> books = bookService.findBooksByTitle("Non Existing Book");

        // Then
        assertThat(books).isEmpty();
    }


    @Test
    @Transactional
    @DisplayName("Должен вернуть пустой список при поиске книг несуществующего автора")
    void shouldReturnEmptyListForNonExistingAuthor() {
        // When
        List<Book> books = bookService.findBooksByAuthor("Non", "Existing");

        // Then
        assertThat(books).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("Должен найти книги по жанру")
    void shouldFindBooksByGenre() {
        // When
        List<Book> books = bookService.findBooksByGenre("Test Genre 1");

        // Then
        assertThat(books).hasSize(1);
        assertThat(books.get(0).getTitle()).isEqualTo("Test Book 1");
        assertThat(books.get(0).getGenre().getName()).isEqualTo("Test Genre 1");
    }

    @Test
    @Transactional
    @DisplayName("Должен вернуть пустой список при поиске книг несуществующего жанра")
    void shouldReturnEmptyListForNonExistingGenre() {
        // When
        List<Book> books = bookService.findBooksByGenre("Non Existing Genre");

        // Then
        assertThat(books).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("Должен обновить книгу")
    void shouldUpdateBook() {
        // Given - сначала создаем новую книгу для обновления
        Book bookToUpdate = bookService.createBook(
                "Book to Update",
                "Original",
                "Author",
                "Original Genre"
        );

        // When
        Book updatedBook = bookService.updateBook(
                bookToUpdate.getId(),
                "Updated Title",
                "Updated",
                "Author",
                "Updated Genre"
        );

        // Then
        assertThat(updatedBook.getId()).isEqualTo(bookToUpdate.getId());
        assertThat(updatedBook.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedBook.getAuthor().getFirstName()).isEqualTo("Updated");
        assertThat(updatedBook.getAuthor().getLastName()).isEqualTo("Author");
        assertThat(updatedBook.getGenre().getName()).isEqualTo("Updated Genre");
    }

    @Test

    @DisplayName("Должен удалить книгу")
    void shouldDeleteBook() {
        // Given - создаем книгу для удаления
        Book bookToDelete = bookService.createBook(
                "Book to Delete",
                "Delete",
                "Author",
                "Delete Genre"
        );

        Long bookId = bookToDelete.getId();
        assertThat(bookService.getBookById(bookId)).isPresent();

        // When
        bookService.deleteBook(bookId);

        // Then
        assertThat(bookService.getBookById(bookId)).isEmpty();
    }

    @Test

    @DisplayName("Должен создать книгу с готовыми объектами Author и Genre")
    void shouldCreateBookWithObjects() {
        // Given
        Author author = authorService.createAuthor("Object", "Author", 35);
        Genre genre = genreService.createGenre("Object Genre");

        // When
        Book createdBook = bookService.createBookWithObjects(
                "Book with Objects",
                author,
                genre
        );

        // Then
        assertThat(createdBook.getId()).isNotNull();
        assertThat(createdBook.getTitle()).isEqualTo("Book with Objects");
        assertThat(createdBook.getAuthor().getLastName()).isEqualTo("Author");
        assertThat(createdBook.getAuthor().getFirstName()).isEqualTo("Object");
        assertThat(createdBook.getAuthor().getAge()).isEqualTo(35);
        assertThat(createdBook.getGenre().getName()).isEqualTo("Object Genre");
    }

    @Test
    @Transactional
    @DisplayName("Должен обновить несуществующую книгу (создать новую)")
    void shouldCreateNewBookWhenUpdatingNonExisting() {
        // Given
        Long nonExistingId = 999L;

        // When
        Book createdBook = bookService.updateBook(
                nonExistingId,
                "New Book from Update",
                "New",
                "Author",
                "New Genre"
        );

        // Then
        assertThat(createdBook.getId()).isNotNull();
        assertThat(createdBook.getTitle()).isEqualTo("New Book from Update");
        assertThat(createdBook.getAuthor().getFirstName()).isEqualTo("New");
        assertThat(createdBook.getAuthor().getLastName()).isEqualTo("Author");
        assertThat(createdBook.getGenre().getName()).isEqualTo("New Genre");
    }

    @Test

    @DisplayName("Должен удалить существующую книгу из тестовых данных")
    void shouldDeleteExistingBookFromTestData() {
        // Given
        Long bookId = 100L;

        // Убедимся, что книга существует
        assertThat(bookService.getBookById(bookId)).isPresent();

        // When
        bookService.deleteBook(bookId);

        // Then
        assertThat(bookService.getBookById(bookId)).isEmpty();
    }
}