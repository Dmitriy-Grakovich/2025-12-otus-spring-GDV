package ru.diasoft.spring.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.diasoft.spring.dao.AuthorDao;
import ru.diasoft.spring.dao.BookDao;
import ru.diasoft.spring.dao.GenreDao;
import ru.diasoft.spring.domain.Author;
import ru.diasoft.spring.domain.Book;
import ru.diasoft.spring.domain.Comment;
import ru.diasoft.spring.domain.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository

public class BookDaoJdbc implements BookDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final AuthorDao authorDao;
    private final GenreDao genreDao;

    public BookDaoJdbc(NamedParameterJdbcTemplate jdbcTemplate,@Qualifier("AuthorDaoJdbc") AuthorDao authorDao,@Qualifier("GenreDaoJdbc") GenreDao genreDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.authorDao = authorDao;
        this.genreDao = genreDao;
    }

    private class BookRowMapper implements RowMapper<Book> {
        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            Long authorId = rs.getLong("author_id");
            Long genreId = rs.getLong("genre_id");

            // Получаем Author и Genre через DAO
            Author author = authorId != 0 ? authorDao.findById(authorId).orElse(null) : null;
            Genre genre = genreId != 0 ? genreDao.findById(genreId).orElse(null) : null;
            List<Comment> comments = new ArrayList<>();

            return new Book(
                    rs.getLong("id"),
                    rs.getString("title"),
                    author,
                    genre,
                    comments
            );
        }
    }

    @Override
    public List<Book> findAll() {
        String sql = """
            SELECT b.id, b.title, b.author_id, b.genre_id, 
                   a.first_name as author_first_name, a.last_name as author_last_name, a.age as author_age,
                   g.name as genre_name
            FROM book b
            LEFT JOIN author a ON b.author_id = a.id
            LEFT JOIN genre g ON b.genre_id = g.id
            ORDER BY b.title
            """;

        return jdbcTemplate.query(sql, new BookRowMapper());
    }

    @Override
    public Optional<Book> findById(Long id) {
        String sql = """
            SELECT b.id, b.title, b.author_id, b.genre_id, 
                   a.first_name as author_first_name, a.last_name as author_last_name, a.age as author_age,
                   g.name as genre_name
            FROM book b
            LEFT JOIN author a ON b.author_id = a.id
            LEFT JOIN genre g ON b.genre_id = g.id
            WHERE b.id = :id
            """;

        try {
            Book book = jdbcTemplate.queryForObject(
                    sql,
                    Map.of("id", id),
                    new BookRowMapper()
            );
            return Optional.ofNullable(book);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == null) {
            return insert(book);
        } else {
            update(book);
            return book;
        }
    }

    private Book insert(Book book) {
        // Проверяем, что Author и Genre существуют
        if (book.getAuthor() == null || book.getAuthor().getId() == null) {
            throw new IllegalArgumentException("Book must have an author with ID");
        }
        if (book.getGenre() == null || book.getGenre().getId() == null) {
            throw new IllegalArgumentException("Book must have a genre with ID");
        }

        String sql = """
            INSERT INTO book (title, author_id, genre_id) 
            VALUES (:title, :author_id, :genre_id)
            """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("title", book.getTitle());
        params.addValue("author_id", book.getAuthor().getId());
        params.addValue("genre_id", book.getGenre().getId());

        jdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});

        book.setId(keyHolder.getKey().longValue());
        return book;
    }

    @Override
    public void update(Book book) {
        // Проверяем, что Author и Genre существуют
        if (book.getAuthor() == null || book.getAuthor().getId() == null) {
            throw new IllegalArgumentException("Book must have an author with ID");
        }
        if (book.getGenre() == null || book.getGenre().getId() == null) {
            throw new IllegalArgumentException("Book must have a genre with ID");
        }

        String sql = """
            UPDATE book 
            SET title = :title, author_id = :author_id, genre_id = :genre_id 
            WHERE id = :id
            """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("title", book.getTitle());
        params.addValue("author_id", book.getAuthor().getId());
        params.addValue("genre_id", book.getGenre().getId());
        params.addValue("id", book.getId());

        jdbcTemplate.update(sql, params);
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM book WHERE id = :id";
        jdbcTemplate.update(sql, Map.of("id", id));
    }

    @Override
    public List<Book> findByTitle(String title) {
        String sql = """
            SELECT b.id, b.title, b.author_id, b.genre_id, 
                   a.first_name as author_first_name, a.last_name as author_last_name, a.age as author_age,
                   g.name as genre_name
            FROM book b
            LEFT JOIN author a ON b.author_id = a.id
            LEFT JOIN genre g ON b.genre_id = g.id
            WHERE LOWER(b.title) LIKE LOWER(:title_pattern)
            ORDER BY b.title
            """;

        return jdbcTemplate.query(
                sql,
                Map.of("title_pattern", "%" + title + "%"),
                new BookRowMapper()
        );
    }

    @Override
    public List<Book> findByAuthorId(Long authorId) {
        String sql = """
            SELECT b.id, b.title, b.author_id, b.genre_id, 
                   a.first_name as author_first_name, a.last_name as author_last_name, a.age as author_age,
                   g.name as genre_name
            FROM book b
            LEFT JOIN author a ON b.author_id = a.id
            LEFT JOIN genre g ON b.genre_id = g.id
            WHERE b.author_id = :author_id
            ORDER BY b.title
            """;

        return jdbcTemplate.query(
                sql,
                Map.of("author_id", authorId),
                new BookRowMapper()
        );
    }

    @Override
    public List<Book> findByGenreId(Long genreId) {
        String sql = """
            SELECT b.id, b.title, b.author_id, b.genre_id, 
                   a.first_name as author_first_name, a.last_name as author_last_name, a.age as author_age,
                   g.name as genre_name
            FROM book b
            LEFT JOIN author a ON b.author_id = a.id
            LEFT JOIN genre g ON b.genre_id = g.id
            WHERE b.genre_id = :genre_id
            ORDER BY b.title
            """;

        return jdbcTemplate.query(
                sql,
                Map.of("genre_id", genreId),
                new BookRowMapper()
        );
    }
}