package ru.diasoft.tasklesson16.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;
import ru.diasoft.tasklesson16.domain.Author;
import ru.diasoft.tasklesson16.domain.Book;
import ru.diasoft.tasklesson16.domain.Genre;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
@DisplayName("Тестирование BookRepository")
class BookRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private CommentRepository commentRepository;

    private Author testAuthor;
    private Genre testGenre;
    private Book testBook;

    @BeforeEach
    void setUp() {
        // Clear data before each test
        commentRepository.deleteAllInBatch();
        bookRepository.deleteAllInBatch();
        authorRepository.deleteAllInBatch();
        genreRepository.deleteAllInBatch();

        // Create test data with proper relationships
        testAuthor = new Author();
        testAuthor.setFirstName("Test");
        testAuthor.setLastName("Author");
        testAuthor.setAge(30);

        testGenre = new Genre();
        testGenre.setName("Test Genre");

        // Save author and genre first
        testAuthor = authorRepository.save(testAuthor);
        testGenre = genreRepository.save(testGenre);

        // Create book with saved author and genre
        testBook = new Book();
        testBook.setTitle("Test Book");
        testBook.setAuthor(testAuthor);
        testBook.setGenre(testGenre);
        testBook = bookRepository.save(testBook);

        // Flush and clear the persistence context to ensure all operations are completed
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("Должен найти книгу по названию")
    void shouldFindBookByTitle() {
        // When
        Optional<Book> foundBook = bookRepository.findByTitle("Test Book");
        
        // Then
        assertThat(foundBook)
                .isPresent()
                .get()
                .extracting(Book::getTitle)
                .isEqualTo("Test Book");
    }



    @Test
    @DisplayName("Должен найти все книги с деталями")
    void shouldFindAllBooksWithDetails() {
        // Given
        Author author2 = authorRepository.save(new Author(null, "Another", "Author", 35));
        Genre genre2 = genreRepository.save(new Genre(null, "Another Genre"));
        Book book2 = bookRepository.save(new Book(null, "Another Book", author2, genre2, null));
        
        // When
        List<Book> books = bookRepository.findAllWithDetails();
        
        // Then
        assertThat(books)
                .hasSize(2)
                .allSatisfy(book -> {
                    assertThat(book.getAuthor()).isNotNull();
                    assertThat(book.getGenre()).isNotNull();
                })
                .extracting(Book::getTitle)
                .containsExactlyInAnyOrder("Test Book", "Another Book");
    }

    @Test
    @DisplayName("Должен найти книги по названию (поиск с учетом регистра)")
    void shouldFindBooksByTitleContainingIgnoreCase() {
        // Given
        // Get managed instances of testAuthor and testGenre
        Author managedAuthor = authorRepository.findById(testAuthor.getId())
                .orElseThrow(() -> new IllegalStateException("Test author not found"));
        Genre managedGenre = genreRepository.findById(testGenre.getId())
                .orElseThrow(() -> new IllegalStateException("Test genre not found"));

        // Create and save new book with managed entities
        Book newBook = new Book();
        newBook.setTitle("Another Test Book");
        newBook.setAuthor(managedAuthor);
        newBook.setGenre(managedGenre);
        bookRepository.saveAndFlush(newBook);

        // When
        List<Book> books = bookRepository.findByTitleContainingIgnoreCase("test");

        // Then
        assertThat(books)
                .hasSize(2)
                .extracting(Book::getTitle)
                .containsExactlyInAnyOrder("Test Book", "Another Test Book");
    }

    @Test
    @DisplayName("Должен найти книги по фамилии автора")
    void shouldFindBooksByAuthorName() {
        // Given
        // Get managed instances of testGenre
        Genre managedGenre = genreRepository.findById(testGenre.getId())
                .orElseThrow(() -> new IllegalStateException("Test genre not found"));

        // Create and save new author with a unique last name
        Author author2 = new Author();
        author2.setFirstName("Another");
        author2.setLastName("UniqueWriter");  // Changed to a more specific last name
        author2.setAge(40);
        Author savedAuthor = authorRepository.saveAndFlush(author2);

        // Create and save new book with managed genre and author
        Book newBook = new Book();
        newBook.setTitle("Author's Book");
        newBook.setAuthor(savedAuthor);
        newBook.setGenre(managedGenre);
        bookRepository.saveAndFlush(newBook);

        // When - search for the unique last name
        List<Book> books = bookRepository.findByAuthor_LastNameContainingIgnoreCase("uniquewr");

        // Then
        assertThat(books)
                .hasSize(1)
                .first()
                .extracting(Book::getTitle)
                .isEqualTo("Author's Book");
    }

    @Test
    @DisplayName("Должен найти книги по жанру")
    void shouldFindBooksByGenre() {
        // Given
        // Ensure we're working with managed entities
        Author managedAuthor = authorRepository.findById(testAuthor.getId())
                .orElseThrow(() -> new IllegalStateException("Test author not found"));
        
        Genre anotherGenre = new Genre();
        anotherGenre.setName("Another Genre");
        Genre savedGenre = genreRepository.saveAndFlush(anotherGenre);
        
        Book newBook = new Book();
        newBook.setTitle("Different Genre Book");
        newBook.setAuthor(managedAuthor);
        newBook.setGenre(savedGenre);
        bookRepository.saveAndFlush(newBook);
        
        // When
        List<Book> books = bookRepository.findByGenre_NameContainingIgnoreCase("test");
        
        // Then
        assertThat(books)
                .hasSize(1)
                .first()
                .extracting(Book::getTitle)
                .isEqualTo("Test Book");
    }
}
