package ru.diasoft.tasklesson16.controller.mapper;

import org.springframework.stereotype.Component;
import ru.diasoft.tasklesson16.controller.dto.*;
import ru.diasoft.tasklesson16.domain.Author;
import ru.diasoft.tasklesson16.domain.Book;
import ru.diasoft.tasklesson16.domain.Comment;
import ru.diasoft.tasklesson16.domain.Genre;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DtoMapper {

    // Author mappings
    public AuthorDto toAuthorDto(Author author) {
        if (author == null) {
            return null;
        }
        return AuthorDto.builder()
                .id(author.getId())
                .firstName(author.getFirstName())
                .lastName(author.getLastName())
                .age(author.getAge())
                .build();
    }

    public Author toAuthor(AuthorDto dto) {
        if (dto == null) {
            return null;
        }
        return Author.builder()
                .id(dto.getId())
                .lastName(dto.getLastName())
                .firstName(dto.getFirstName())
                .build();
    }

    public List<AuthorDto> toAuthorDtos(List<Author> authors) {
        if (authors == null) {
            return null;
        }
        return authors.stream()
                .map(this::toAuthorDto)
                .collect(Collectors.toList());
    }

    // Genre mappings
    public GenreDto toGenreDto(Genre genre) {
        if (genre == null) {
            return null;
        }
        return GenreDto.builder()
                .id(genre.getId())
                .name(genre.getName())
                .build();
    }

    public Genre toGenre(GenreDto dto) {
        if (dto == null) {
            return null;
        }
        return Genre.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }

    public List<GenreDto> toGenreDtos(List<Genre> genres) {
        if (genres == null) {
            return null;
        }
        return genres.stream()
                .map(this::toGenreDto)
                .collect(Collectors.toList());
    }

    // Comment mappings
    public CommentDto toCommentDto(Comment comment) {
        if (comment == null) {
            return null;
        }
        return CommentDto.builder()
                .id(comment.getId())
                .description(comment.getDescription())
                .nickname(comment.getNickname())
                .bookId(comment.getBook() != null ? comment.getBook().getId() : null)
                .build();
    }

    public List<CommentDto> toCommentDtos(List<Comment> comments) {
        if (comments == null) {
            return null;
        }
        return comments.stream()
                .map(this::toCommentDto)
                .collect(Collectors.toList());
    }

    // Book mappings
    public BookDto toBookDto(Book book) {
        if (book == null) {
            return null;
        }
        return BookDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(toAuthorDto(book.getAuthor()))
                .genre(toGenreDto(book.getGenre()))
                .comments(toCommentDtos(book.getComments()))
                .build();
    }

    public List<BookDto> toBookDtos(List<Book> books) {
        if (books == null) {
            return null;
        }
        return books.stream()
                .map(this::toBookDto)
                .collect(Collectors.toList());
    }
}
