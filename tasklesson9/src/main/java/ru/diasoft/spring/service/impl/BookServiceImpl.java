package ru.diasoft.spring.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.diasoft.spring.dao.AuthorDao;
import ru.diasoft.spring.dao.BookDao;
import ru.diasoft.spring.dao.GenreDao;
import ru.diasoft.spring.domain.Author;
import ru.diasoft.spring.domain.Book;
import ru.diasoft.spring.domain.Genre;
import ru.diasoft.spring.service.BookService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookDao bookDao;
    private final AuthorDao authorDao;
    private final GenreDao genreDao;

    @Override
    public List<Book> getAllBooks() {
        return bookDao.findAll();
    }

    @Override
    public Optional<Book> getBookById(Long id) {
        return bookDao.findById(id);
    }

    @Override
    @Transactional
    public Book createBook(String title, String authorFirstName, String authorLastName, String genreName) {
        // Find or create author
        Author author = authorDao.findByFullName(authorFirstName, authorLastName)
                .orElseGet(() -> {
                    Author newAuthor = new Author();
                    newAuthor.setFirstName(authorFirstName);
                    newAuthor.setLastName(authorLastName);
                    newAuthor.setAge(null);
                    return authorDao.save(newAuthor);
                });

        // Find or create genre
        Genre genre = genreDao.findByName(genreName)
                .orElseGet(() -> {
                    Genre newGenre = new Genre();
                    newGenre.setName(genreName);
                    return genreDao.save(newGenre);
                });

        // Create book with Author and Genre objects
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setGenre(genre);

        return bookDao.save(book);
    }

    @Override
    @Transactional
    public Book updateBook(Long id, String title, String authorFirstName, String authorLastName, String genreName) {
        Book book = bookDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with id: " + id));

        // Find or create author
        Author author = authorDao.findByFullName(authorFirstName, authorLastName)
                .orElseGet(() -> {
                    Author newAuthor = new Author();
                    newAuthor.setFirstName(authorFirstName);
                    newAuthor.setLastName(authorLastName);
                    newAuthor.setAge(null);
                    return authorDao.save(newAuthor);
                });

        // Find or create genre
        Genre genre = genreDao.findByName(genreName)
                .orElseGet(() -> {
                    Genre newGenre = new Genre();
                    newGenre.setName(genreName);
                    return genreDao.save(newGenre);
                });

        // Update book
        book.setTitle(title);
        book.setAuthor(author);
        book.setGenre(genre);

        bookDao.update(book);
        return book;
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        bookDao.deleteById(id);
    }

    @Override
    public List<Book> findBooksByTitle(String title) {
        return bookDao.findByTitle(title);
    }

    @Override
    public List<Book> findBooksByAuthor(String firstName, String lastName) {
        Optional<Author> author = authorDao.findByFullName(firstName, lastName);
        if (author.isEmpty()) {
            return List.of();
        }
        return bookDao.findByAuthorId(author.get().getId());
    }

    @Override
    public List<Book> findBooksByGenre(String genreName) {
        Optional<Genre> genre = genreDao.findByName(genreName);
        if (genre.isEmpty()) {
            return List.of();
        }
        return bookDao.findByGenreId(genre.get().getId());
    }

    // Новый метод для создания книги с готовыми объектами
    @Override
    @Transactional
    public Book createBookWithObjects(String title, Author author, Genre genre) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setGenre(genre);
        return bookDao.save(book);
    }
}