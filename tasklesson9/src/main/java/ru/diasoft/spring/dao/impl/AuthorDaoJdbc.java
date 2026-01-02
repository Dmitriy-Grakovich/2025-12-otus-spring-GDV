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
import ru.diasoft.spring.domain.Author;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Qualifier("AuthorDaoJdbc")
public class AuthorDaoJdbc implements AuthorDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    static class AuthorRowMapper implements RowMapper<Author> {
        @Override
        public Author mapRow(ResultSet rs, int rowNum) throws SQLException {
            Integer age = rs.getInt("age");
            if (rs.wasNull()) {
                age = null;
            }
            return new Author(
                    rs.getLong("id"),
                    rs.getString("last_name"),
                    rs.getString("first_name"),
                    age
            );
        }
    }

    @Override
    public List<Author> findAll() {
        String sql = "SELECT id, last_name, first_name, age FROM author ORDER BY last_name, first_name";
        return jdbcTemplate.query(sql, new AuthorRowMapper());
    }

    @Override
    public Optional<Author> findById(Long id) {
        String sql = "SELECT id, last_name, first_name, age FROM author WHERE id = :id";
        try {
            Author author = jdbcTemplate.queryForObject(
                    sql,
                    Map.of("id", id),
                    new AuthorRowMapper()
            );
            return Optional.ofNullable(author);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Author save(Author author) {
        if (author.getId() == null) {
            return insert(author);
        } else {
            update(author);
            return author;
        }
    }

    private Author insert(Author author) {
        String sql = """
            INSERT INTO author (last_name, first_name, age) 
            VALUES (:last_name, :first_name, :age)
            """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("last_name", author.getLastName());
        params.addValue("first_name", author.getFirstName());
        params.addValue("age", author.getAge());

        jdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});

        author.setId(keyHolder.getKey().longValue());
        return author;
    }

    @Override
    public void update(Author author) {
        String sql = """
            UPDATE author 
            SET last_name = :last_name, first_name = :first_name, age = :age 
            WHERE id = :id
            """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("last_name", author.getLastName());
        params.addValue("first_name", author.getFirstName());
        params.addValue("age", author.getAge());
        params.addValue("id", author.getId());

        jdbcTemplate.update(sql, params);
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM author WHERE id = :id";
        jdbcTemplate.update(sql, Map.of("id", id));
    }

    @Override
    public Optional<Author> findByFullName(String firstName, String lastName) {
        String sql = """
            SELECT id, last_name, first_name, age 
            FROM author 
            WHERE first_name = :first_name AND last_name = :last_name
            """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("first_name", firstName);
        params.addValue("last_name", lastName);

        try {
            Author author = jdbcTemplate.queryForObject(sql, params, new AuthorRowMapper());
            return Optional.ofNullable(author);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}