package ru.diasoft.spring.service;

import ru.diasoft.spring.domain.Author;
import ru.diasoft.spring.domain.Book;
import ru.diasoft.spring.domain.Comment;
import ru.diasoft.spring.domain.Genre;

import java.util.List;
import java.util.Optional;

public interface BookService {
    List<Book> getAllBooks();
    Optional<Book> getBookById(Long id);
    Book createBook(String title, String authorFirstName, String authorLastName, String genreName);
    Book updateBook(Long id, String title, String authorFirstName, String authorLastName, String genreName);
    void deleteBook(Long id);
    List<Book> findBooksByTitle(String title);
    List<Book> findBooksByAuthor(String firstName, String lastName);
    List<Book> findBooksByGenre(String genreName);
    Book createBookWithObjects(String title, Author author, Genre genre);
    List<Comment> getBookComments(Long bookId);
    void removeCommentFromBook(Long bookId, Long commentId);
    Comment addCommentToBook(Long bookId, String description, String nickname);

}