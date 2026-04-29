package ru.diasoft.spring.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.diasoft.spring.domain.Author;
import ru.diasoft.spring.domain.Book;
import ru.diasoft.spring.domain.Comment;
import ru.diasoft.spring.domain.Genre;
import ru.diasoft.spring.service.AuthorService;
import ru.diasoft.spring.service.BookService;
import ru.diasoft.spring.service.CommentService;
import ru.diasoft.spring.service.GenreService;

import java.util.List;
import java.util.Optional;

@ShellComponent
@RequiredArgsConstructor
public class LibraryCommands {

    private final BookService bookService;
    private final AuthorService authorService;
    private final GenreService genreService;
    private final CommentService commentService;

    // Book Commands (CRUD - обязательные)

    @ShellMethod(value = "List all books", key = {"books", "list-books"})
    public String listBooks() {
        List<Book> books = bookService.getAllBooks();
        if (books.isEmpty()) {
            return "No books found in the library.";
        }

        StringBuilder sb = new StringBuilder("Books in library:\n");
        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);
            sb.append(i + 1)
                    .append(". ID: ").append(book.getId())
                    .append(", Title: '").append(book.getTitle()).append("'");

            if (book.getAuthor() != null) {
                sb.append(", Author: ").append(book.getAuthor().getFirstName())
                        .append(" ").append(book.getAuthor().getLastName());
            } else {
                sb.append(", Author: Unknown");
            }

            if (book.getGenre() != null) {
                sb.append(", Genre: ").append(book.getGenre().getName());
            } else {
                sb.append(", Genre: Unknown");
            }
            if (book.getComments() != null && !book.getComments().isEmpty()) {
                sb.append(", Comments: ").append(book.getComments().size()).append(" comment(s)");
            } else {
                sb.append(", Comments: None");
            }

            sb.append("\n");
        }
        return sb.toString();
    }

    @ShellMethod(value = "Get book by ID", key = {"get-book", "book"})
    public String getBook(@ShellOption Long id) {
        Optional<Book> bookOpt = bookService.getBookById(id);
        if (bookOpt.isEmpty()) {
            return "Book not found with id: " + id;
        }

        Book book = bookOpt.get();
        StringBuilder sb = new StringBuilder();
        sb.append("Book ID: ").append(book.getId()).append("\n")
                .append("Title: ").append(book.getTitle()).append("\n");

        if (book.getAuthor() != null) {
            sb.append("Author: ").append(book.getAuthor().getFirstName())
                    .append(" ").append(book.getAuthor().getLastName());
            if (book.getAuthor().getAge() != null) {
                sb.append(" (Age: ").append(book.getAuthor().getAge()).append(")");
            }
            sb.append("\n");
        } else {
            sb.append("Author: Unknown\n");
        }

        if (book.getGenre() != null) {
            sb.append("Genre: ").append(book.getGenre().getName()).append("\n");
        } else {
            sb.append("Genre: Unknown\n");
        }

        if (book.getComments() != null && !book.getComments().isEmpty()) {
            sb.append("Comments:\n");
            for (Comment comment : book.getComments()) {
                sb.append("  - ").append(comment.getNickname())
                        .append(": ").append(comment.getDescription()).append("\n");
            }
        } else {
            sb.append("Comments: None\n");
        }

        return sb.toString();
    }

    @ShellMethod(value = "Create a new book", key = {"create-book", "add-book"})
    public String createBook(
            @ShellOption String title,
            @ShellOption String authorFirstName,
            @ShellOption String authorLastName,
            @ShellOption String genre) {

        try {
            Book book = bookService.createBook(title, authorFirstName, authorLastName, genre);
            return String.format(
                    "Book created successfully:\nID: %d\nTitle: %s\nAuthor: %s %s\nGenre: %s",
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor() != null ? book.getAuthor().getFirstName() : authorFirstName,
                    book.getAuthor() != null ? book.getAuthor().getLastName() : authorLastName,
                    book.getGenre() != null ? book.getGenre().getName() : genre
            );
        } catch (Exception e) {
            return "Error creating book: " + e.getMessage();
        }
    }

    @ShellMethod(value = "Update a book", key = {"update-book", "edit-book"})
    public String updateBook(
            @ShellOption Long id,
            @ShellOption String title,
            @ShellOption String authorFirstName,
            @ShellOption String authorLastName,
            @ShellOption String genre) {

        try {
            Book book = bookService.updateBook(id, title, authorFirstName, authorLastName, genre);
            return String.format(
                    "Book updated successfully:\nID: %d\nTitle: %s\nAuthor: %s %s\nGenre: %s",
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor() != null ? book.getAuthor().getFirstName() : authorFirstName,
                    book.getAuthor() != null ? book.getAuthor().getLastName() : authorLastName,
                    book.getGenre() != null ? book.getGenre().getName() : genre
            );
        } catch (IllegalArgumentException e) {
            return "Error: " + e.getMessage();
        }
    }

    @ShellMethod(value = "Delete a book", key = {"delete-book", "remove-book"})
    public String deleteBook(@ShellOption Long id) {
        try {
            bookService.deleteBook(id);
            return "Book deleted successfully with id: " + id;
        } catch (Exception e) {
            return "Error deleting book: " + e.getMessage();
        }
    }

    @ShellMethod(value = "Search books by title", key = {"search-books", "find-books"})
    public String searchBooks(@ShellOption String title) {
        List<Book> books = bookService.findBooksByTitle(title);
        if (books.isEmpty()) {
            return "No books found with title containing: " + title;
        }

        StringBuilder sb = new StringBuilder("Found books:\n");
        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);
            sb.append(i + 1)
                    .append(". ID: ").append(book.getId())
                    .append(", Title: '").append(book.getTitle()).append("'");

            if (book.getAuthor() != null) {
                sb.append(", Author: ").append(book.getAuthor().getFirstName())
                        .append(" ").append(book.getAuthor().getLastName());
            }

            if (book.getGenre() != null) {
                sb.append(", Genre: ").append(book.getGenre().getName());
            }

            sb.append("\n");
        }
        return sb.toString();
    }

    // Author Commands

    @ShellMethod(value = "List all authors", key = {"authors", "list-authors"})
    public String listAuthors() {
        List<Author> authors = authorService.getAllAuthors();
        if (authors.isEmpty()) {
            return "No authors found.";
        }

        StringBuilder sb = new StringBuilder("Authors:\n");
        for (int i = 0; i < authors.size(); i++) {
            Author author = authors.get(i);
            sb.append(i + 1)
              .append(". ID: ").append(author.getId())
              .append(", Name: ").append(author.getFirstName()).append(" ").append(author.getLastName())
              .append(", Age: ").append(author.getAge() != null ? author.getAge() : "N/A")
              .append("\n");
        }
        return sb.toString();
    }

    @ShellMethod(value = "Create a new author", key = {"create-author", "add-author"})
    public String createAuthor(
            @ShellOption String firstName,
            @ShellOption String lastName,
            @ShellOption( defaultValue = "0") Integer age) {

        Author author = authorService.createAuthor(firstName, lastName, age > 0 ? age : null);
        return String.format(
            "Author created successfully:\nID: %d\nName: %s %s\nAge: %s",
            author.getId(), author.getFirstName(), author.getLastName(),
            author.getAge() != null ? author.getAge() : "N/A"
        );
    }

    // Genre Commands

    @ShellMethod(value = "List all genres", key = {"genres", "list-genres"})
    public String listGenres() {
        List<Genre> genres = genreService.getAllGenres();
        if (genres.isEmpty()) {
            return "No genres found.";
        }

        StringBuilder sb = new StringBuilder("Genres:\n");
        for (int i = 0; i < genres.size(); i++) {
            Genre genre = genres.get(i);
            sb.append(i + 1)
              .append(". ID: ").append(genre.getId())
              .append(", Name: ").append(genre.getName())
              .append("\n");
        }
        return sb.toString();
    }

    @ShellMethod(value = "Create a new genre", key = {"create-genre", "add-genre"})
    public String createGenre(@ShellOption String name) {
        Genre genre = genreService.createGenre(name);
        return String.format("Genre created successfully:\nID: %d\nName: %s", genre.getId(), genre.getName());
    }
    @ShellMethod(value = "List all comments", key = {"comments", "list-comments"})
    public String listComments() {
        List<Comment> comments = commentService.getAllComments();
        if (comments.isEmpty()) {
            return "No comments found.";
        }

        StringBuilder sb = new StringBuilder("Comments:\n");
        for (int i = 0; i < comments.size(); i++) {
            Comment comment = comments.get(i);
            sb.append(i + 1)
                    .append(". ID: ").append(comment.getId())
                    .append(", Nickname: '").append(comment.getNickname()).append("'")
                    .append(", Comment: ").append(comment.getDescription())
                    .append(", Book: ").append(
                            comment.getBook() != null ?
                                    comment.getBook().getTitle() : "No book"
                    )
                    .append("\n");
        }
        return sb.toString();
    }

    @ShellMethod(value = "Add comment to book", key = {"add-comment", "create-comment"})
    public String addComment(
            @ShellOption Long bookId,
            @ShellOption String nickname,
            @ShellOption String description) {

        try {
            Comment comment = bookService.addCommentToBook(bookId, description, nickname);
            return String.format(
                    "Comment added successfully:\nID: %d\nNickname: %s\nComment: %s\nBook: %s",
                    comment.getId(),
                    comment.getNickname(),
                    comment.getDescription(),
                    comment.getBook() != null ? comment.getBook().getTitle() : "Unknown"
            );
        } catch (Exception e) {
            return "Error adding comment: " + e.getMessage();
        }
    }

    @ShellMethod(value = "Remove comment from book", key = {"remove-comment", "delete-comment"})
    public String removeComment(
            @ShellOption Long bookId,
            @ShellOption Long commentId) {

        try {
            bookService.removeCommentFromBook(bookId, commentId);
            return "Comment removed successfully";
        } catch (Exception e) {
            return "Error removing comment: " + e.getMessage();
        }
    }

    @ShellMethod(value = "Get comments for book", key = {"book-comments", "list-book-comments"})
    public String getBookComments(@ShellOption Long bookId) {
        try {
            List<Comment> comments = bookService.getBookComments(bookId);
            if (comments.isEmpty()) {
                return "No comments found for this book.";
            }

            StringBuilder sb = new StringBuilder("Comments for book ID " + bookId + ":\n");
            for (int i = 0; i < comments.size(); i++) {
                Comment comment = comments.get(i);
                sb.append(i + 1)
                        .append(". ID: ").append(comment.getId())
                        .append(", Nickname: '").append(comment.getNickname()).append("'")
                        .append(", Comment: ").append(comment.getDescription())
                        .append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @ShellMethod(value = "Search comments by nickname", key = {"search-comments", "find-comments"})
    public String searchComments(@ShellOption String nickname) {
        List<Comment> comments = commentService.findCommentsByNickname(nickname);
        if (comments.isEmpty()) {
            return "No comments found for nickname: " + nickname;
        }

        StringBuilder sb = new StringBuilder("Found comments:\n");
        for (int i = 0; i < comments.size(); i++) {
            Comment comment = comments.get(i);
            sb.append(i + 1)
                    .append(". ID: ").append(comment.getId())
                    .append(", Nickname: '").append(comment.getNickname()).append("'")
                    .append(", Comment: ").append(comment.getDescription())
                    .append(", Book: ").append(
                            comment.getBook() != null ?
                                    comment.getBook().getTitle() : "No book"
                    )
                    .append("\n");
        }
        return sb.toString();
    }
}