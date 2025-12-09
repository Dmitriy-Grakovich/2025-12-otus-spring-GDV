package ru.diasoft.spring.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.diasoft.spring.domain.Author;
import ru.diasoft.spring.domain.Book;
import ru.diasoft.spring.domain.Genre;
import ru.diasoft.spring.service.impl.AuthorServiceImpl;
import ru.diasoft.spring.service.impl.BookServiceImpl;
import ru.diasoft.spring.service.impl.GenreServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Интеграционные тесты для BookServiceImpl с Liquibase")
class BookServiceImplIntegrationTest {

    @Autowired
    private BookServiceImpl bookService;
    @Autowired
    private AuthorServiceImpl authorService;
    @Autowired
    private GenreServiceImpl genreService;

    @Test
    @DisplayName("Должен найти все книги из тестовых данных")
    void shouldFindAllBooksFromTestData() {
        // When
        List<Book> books = bookService.getAllBooks();

        // Then
        assertThat(books).isNotEmpty();
        assertThat(books).hasSize(3);

        // Проверяем первую книгу
        Book firstBook = books.get(0);
        assertThat(firstBook.getId()).isEqualTo(100L);
        assertThat(firstBook.getTitle()).isEqualTo("Test Book 1");
        assertThat(firstBook.getAuthor()).isNotNull();
        assertThat(firstBook.getAuthor().getId()).isEqualTo(100L);
        assertThat(firstBook.getAuthor().getFirstName()).isEqualTo("Test");
        assertThat(firstBook.getAuthor().getLastName()).isEqualTo("Author1");
        assertThat(firstBook.getAuthor().getAge()).isEqualTo(30);
        assertThat(firstBook.getGenre()).isNotNull();
        assertThat(firstBook.getGenre().getId()).isEqualTo(100L);
        assertThat(firstBook.getGenre().getName()).isEqualTo("Test Genre 1");
    }

    @Test
    @DisplayName("Должен найти книгу по ID из тестовых данных")
    void shouldFindBookByIdFromTestData() {
        // When
        Optional<Book> book = bookService.getBookById(101L);

        // Then
        assertThat(book).isPresent();
        assertThat(book.get().getTitle()).isEqualTo("Test Book 2");
        assertThat(book.get().getAuthor().getLastName()).isEqualTo("Author2");
        assertThat(book.get().getGenre().getName()).isEqualTo("Test Genre 2");
    }

    @Test
    @DisplayName("Должен вернуть пустой Optional для несуществующего ID")
    void shouldReturnEmptyOptionalForNonExistingId() {
        // When
        Optional<Book> book = bookService.getBookById(999L);

        // Then
        assertThat(book).isEmpty();
    }

    @Test
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

        // Проверяем, что книга сохранена в БД
        Optional<Book> foundBook = bookService.getBookById(createdBook.getId());
        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getTitle()).isEqualTo(title);
    }

    @Test
    @DisplayName("Должен создать книгу с существующим автором и жанром")
    void shouldCreateBookWithExistingAuthorAndGenre() {
        // Given - используем существующие тестовые данные
        String title = "Another Test Book";
        String authorFirstName = "Test"; // Существующий автор
        String authorLastName = "Author1"; // Существующий автор
        String genreName = "Test Genre 1"; // Существующий жанр

        // When
        Book createdBook = bookService.createBook(title, authorFirstName, authorLastName, genreName);

        // Then
        assertThat(createdBook.getId()).isNotNull();
        assertThat(createdBook.getTitle()).isEqualTo(title);
        assertThat(createdBook.getAuthor().getFirstName()).isEqualTo(authorFirstName);
        assertThat(createdBook.getAuthor().getLastName()).isEqualTo(authorLastName);
        assertThat(createdBook.getAuthor().getAge()).isEqualTo(30); // Возраст из тестовых данных
        assertThat(createdBook.getGenre().getName()).isEqualTo(genreName);
    }

    @Test
    @DisplayName("Должен найти книги по названию")
    void shouldFindBooksByTitle() {
        // When
        List<Book> books = bookService.findBooksByTitle("Test Book");

        // Then
        assertThat(books).hasSize(3);
        books.forEach(book ->
                assertThat(book.getTitle()).contains("Test Book")
        );
    }

    @Test
    @DisplayName("Должен найти книги по автору")
    void shouldFindBooksByAuthor() {
        // When
        List<Book> books = bookService.findBooksByAuthor("Test", "Author1");

        // Then
        assertThat(books).hasSize(1);
        assertThat(books.get(0).getTitle()).isEqualTo("Test Book 1");
        assertThat(books.get(0).getAuthor().getLastName()).isEqualTo("Author1");
    }

    @Test
    @DisplayName("Должен вернуть пустой список при поиске книг несуществующего автора")
    void shouldReturnEmptyListForNonExistingAuthor() {
        // When
        List<Book> books = bookService.findBooksByAuthor("Non", "Existing");

        // Then
        assertThat(books).isEmpty();
    }

    @Test
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

        // Проверяем, что изменения сохранены в БД
        Optional<Book> foundBook = bookService.getBookById(bookToUpdate.getId());
        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getTitle()).isEqualTo("Updated Title");
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
        // Given - сначала сохраняем автора и жанр
        Author author = authorService.createAuthor("Object", "Author", 35);
        Genre genre = genreService.createGenre("Object Genre");

        // When - создаем книгу с помощью метода createBookWithObjects
        Book createdBook = bookService.createBookWithObjects(
                "Book with Objects",
                author,
                genre
        );

        // Then
        assertThat(createdBook.getId()).isNotNull();
        assertThat(createdBook.getTitle()).isEqualTo("Book with Objects");
        assertThat(createdBook.getAuthor().getLastName()).isEqualTo("Author"); // Обратите внимание: lastName
        assertThat(createdBook.getAuthor().getFirstName()).isEqualTo("Object"); // firstName
        assertThat(createdBook.getAuthor().getAge()).isEqualTo(35);
        assertThat(createdBook.getGenre().getName()).isEqualTo("Object Genre");
    }
}