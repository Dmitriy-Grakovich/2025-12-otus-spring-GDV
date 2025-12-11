package ru.diasoft.spring.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.diasoft.spring.dao.AuthorDao;
import ru.diasoft.spring.dao.BookDao;
import ru.diasoft.spring.dao.CommentDao;
import ru.diasoft.spring.dao.GenreDao;
import ru.diasoft.spring.domain.Author;
import ru.diasoft.spring.domain.Book;
import ru.diasoft.spring.domain.Comment;
import ru.diasoft.spring.domain.Genre;
import ru.diasoft.spring.service.BookService;

import java.util.List;
import java.util.Optional;

@Service

public class BookServiceImpl implements BookService {

    private final BookDao bookDao;

    private final AuthorDao authorDao;
    private final GenreDao genreDao;

    public BookServiceImpl(@Qualifier("BookDaoJPAImpl") BookDao bookDao, @Qualifier("AuthorDaoIJPAmpl") AuthorDao authorDao, @Qualifier("GenreDaoJPAImpl") GenreDao genreDao, CommentDao commentDao) {
        this.bookDao = bookDao;
        this.authorDao = authorDao;
        this.genreDao = genreDao;
    }

    @Override
    @Transactional
    public List<Book> getAllBooks() {
        return bookDao.findAll();
    }

    @Override
    @Transactional
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
    @Transactional
    public List<Book> findBooksByTitle(String title) {
        return bookDao.findByTitle(title);
    }

    @Override
    @Transactional
    public List<Book> findBooksByAuthor(String firstName, String lastName) {
        Optional<Author> author = authorDao.findByFullName(firstName, lastName);
        if (author.isEmpty()) {
            return List.of();
        }
        return bookDao.findByAuthorId(author.get().getId());
    }

    @Override
    @Transactional
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
    @Transactional
    @Override
    public Comment addCommentToBook(Long bookId, String description, String nickname) {
        Book book = bookDao.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with id: " + bookId));

        Comment comment = new Comment();
        comment.setDescription(description);
        comment.setNickname(nickname);
        comment.setBook(book);

        book.getComments().add(comment);
        bookDao.save(book);

        return comment;
    }

    @Transactional
    @Override
    public void removeCommentFromBook(Long bookId, Long commentId) {
        Book book = bookDao.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with id: " + bookId));

        Comment commentToRemove = book.getComments().stream()
                .filter(c -> c.getId().equals(commentId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Comment not found with id: " + commentId));

        book.getComments().remove(commentToRemove);
        bookDao.save(book);
    }

    @Override
    public List<Comment> getBookComments(Long bookId) {
        Book book = bookDao.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with id: " + bookId));
        return book.getComments();
    }
}