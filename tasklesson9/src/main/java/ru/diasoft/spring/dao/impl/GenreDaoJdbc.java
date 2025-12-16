package ru.diasoft.spring.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.diasoft.spring.dao.GenreDao;
import ru.diasoft.spring.domain.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GenreDaoJdbc implements GenreDao {
    
    private final NamedParameterJdbcTemplate jdbcTemplate;
    
    private static class GenreRowMapper implements RowMapper<Genre> {
        @Override
        public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Genre(
                rs.getLong("id"),
                rs.getString("name")
            );
        }
    }
    
    @Override
    public List<Genre> findAll() {
        String sql = "SELECT id, name FROM genre ORDER BY name";
        return jdbcTemplate.query(sql, new GenreRowMapper());
    }
    
    @Override
    public Optional<Genre> findById(Long id) {
        String sql = "SELECT id, name FROM genre WHERE id = :id";
        try {
            Genre genre = jdbcTemplate.queryForObject(
                sql,
                Map.of("id", id),
                new GenreRowMapper()
            );
            return Optional.ofNullable(genre);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    @Override
    public Genre save(Genre genre) {
        if (genre.getId() == null) {
            return insert(genre);
        } else {
            update(genre);
            return genre;
        }
    }
    
    private Genre insert(Genre genre) {
        String sql = "INSERT INTO genre (name) VALUES (:name)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", genre.getName());
        
        jdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});
        
        genre.setId(keyHolder.getKey().longValue());
        return genre;
    }
    
    @Override
    public void update(Genre genre) {
        String sql = "UPDATE genre SET name = :name WHERE id = :id";
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", genre.getName());
        params.addValue("id", genre.getId());
        
        jdbcTemplate.update(sql, params);
    }
    
    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM genre WHERE id = :id";
        jdbcTemplate.update(sql, Map.of("id", id));
    }
    
    @Override
    public Optional<Genre> findByName(String name) {
        String sql = "SELECT id, name FROM genre WHERE name = :name";
        
        try {
            Genre genre = jdbcTemplate.queryForObject(
                sql,
                Map.of("name", name),
                new GenreRowMapper()
            );
            return Optional.ofNullable(genre);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}