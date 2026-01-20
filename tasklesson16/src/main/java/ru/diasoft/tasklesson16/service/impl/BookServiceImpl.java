package ru.diasoft.tasklesson16.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.diasoft.tasklesson16.domain.Author;
import ru.diasoft.tasklesson16.domain.Book;
import ru.diasoft.tasklesson16.domain.Comment;
import ru.diasoft.tasklesson16.domain.Genre;
import ru.diasoft.tasklesson16.repository.AuthorRepository;
import ru.diasoft.tasklesson16.repository.BookRepository;
import ru.diasoft.tasklesson16.repository.CommentRepository;
import ru.diasoft.tasklesson16.repository.GenreRepository;
import ru.diasoft.tasklesson16.service.BookService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Реализация сервиса для работы с книгами
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAllWithDetails();
    }

    @Override
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findByIdWithDetails(id);
    }

    @Override
    @Transactional
    public Book createBook(String title, String authorFirstName, String authorLastName, String genreName) {
        // Находим или создаем автора
        // В конструкторе Author параметры идут в порядке: id, lastName, firstName, age
        Author author = authorRepository.findByFirstNameAndLastName(authorFirstName, authorLastName)
                .orElseGet(() -> {
                    Author newAuthor = new Author(null, authorLastName, authorFirstName, 0);
                    return authorRepository.save(newAuthor);
                });

        // Находим или создаем жанр
        Genre genre = genreRepository.findByName(genreName)
                .orElseGet(() -> {
                    Genre newGenre = new Genre(null, genreName);
                    return genreRepository.save(newGenre);
                });

        // Создаем и сохраняем книгу
        Book book = new Book(null, title, author, genre, null);
        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public Book updateBook(Long id, String title, String authorFirstName, String authorLastName, String genreName) {
        // Находим книгу
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Книга не найдена"));

        // Находим или создаем автора
        // В конструкторе Author параметры идут в порядке: id, lastName, firstName, age
        Author author = authorRepository.findByFirstNameAndLastName(authorFirstName, authorLastName)
                .orElseGet(() -> {
                    Author newAuthor = new Author(null, authorLastName, authorFirstName, 0);
                    return authorRepository.save(newAuthor);
                });

        // Находим или создаем жанр
        Genre genre = genreRepository.findByName(genreName)
                .orElseGet(() -> {
                    Genre newGenre = new Genre(null, genreName);
                    return genreRepository.save(newGenre);
                });

        // Обновляем данные книги
        book.setTitle(title);
        book.setAuthor(author);
        book.setGenre(genre);

        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        // Сначала удаляем все комментарии к книге
        commentRepository.deleteByBookId(id);
        // Затем удаляем саму книгу
        bookRepository.deleteById(id);
    }

    @Override
    public List<Book> findBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    @Override
    public List<Book> findBooksByAuthor(String firstName, String lastName) {
        return authorRepository.findByFirstNameAndLastName(firstName, lastName)
                .map(author -> bookRepository.findByAuthorId(author.getId()))
                .orElse(Collections.emptyList());
    }

    @Override
    public List<Book> findBooksByGenre(String genreName) {
        return bookRepository.findByGenre_NameContainingIgnoreCase(genreName);
    }

    @Override
    @Transactional
    public Book createBookWithObjects(String title, Author author, Genre genre) {
        // Проверяем существование автора
        Author existingAuthor = authorRepository.findByFirstNameAndLastName(
                author.getFirstName(), author.getLastName()
        ).orElseGet(() -> authorRepository.save(author));

        // Проверяем существование жанра
        Genre existingGenre = genreRepository.findByName(genre.getName())
                .orElseGet(() -> genreRepository.save(genre));

        // Создаем книгу
        Book book = new Book(null, title, existingAuthor, existingGenre, null);
        return bookRepository.save(book);
    }

    @Override
    public List<Comment> getBookComments(Long bookId) {
        return commentRepository.findByBookId(bookId);
    }

    @Override
    @Transactional
    public void removeCommentFromBook(Long bookId, Long commentId) {
        // Проверяем, что комментарий принадлежит книге
        commentRepository.findById(commentId)
                .filter(comment -> comment.getBook().getId().equals(bookId))
                .ifPresent(comment -> commentRepository.delete(comment));
    }

    @Override
    @Transactional
    public Comment addCommentToBook(Long bookId, String description, String nickname) {
        // Находим книгу
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Книга не найдена"));
        
        // Создаем и сохраняем комментарий
        Comment comment = new Comment(null, description, nickname, book);
        return commentRepository.save(comment);
    }
}