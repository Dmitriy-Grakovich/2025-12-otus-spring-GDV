package ru.diasoft.spring.dao.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.diasoft.spring.domain.Author;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тестирование AuthorDaoJdbc")
class AuthorDaoJdbcTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    private AuthorDaoJdbc authorDao;

    @BeforeEach
    void setUp() {
        authorDao = new AuthorDaoJdbc(jdbcTemplate);
    }

    @Test
    @DisplayName("Должен найти всех авторов")
    void shouldFindAllAuthors() {
        // Given
        Author expectedAuthor = new Author(1L, "Tolstoy", "Leo", 82);
        when(jdbcTemplate.query(anyString(), any(RowMapper.class)))
                .thenReturn(Collections.singletonList(expectedAuthor));

        // When
        List<Author> authors = authorDao.findAll();

        // Then
        assertThat(authors).hasSize(1);
        assertThat(authors.get(0)).isEqualTo(expectedAuthor);

        verify(jdbcTemplate).query(eq("SELECT id, last_name, first_name, age FROM author ORDER BY last_name, first_name"),
                any(RowMapper.class));
    }

    @Test
    @DisplayName("Должен найти автора по ID")
    void shouldFindAuthorById() {
        // Given
        Long authorId = 1L;
        Author expectedAuthor = new Author(authorId, "Tolstoy", "Leo", 82);
        when(jdbcTemplate.queryForObject(anyString(), any(Map.class), any(RowMapper.class)))
                .thenReturn(expectedAuthor);

        // When
        Optional<Author> author = authorDao.findById(authorId);

        // Then
        assertThat(author).isPresent();
        assertThat(author.get()).isEqualTo(expectedAuthor);
    }

    @Test
    @DisplayName("Должен вернуть пустой Optional если автор не найден по ID")
    void shouldReturnEmptyOptionalWhenAuthorNotFoundById() {
        // Given
        Long authorId = 999L;
        when(jdbcTemplate.queryForObject(anyString(), any(Map.class), any(RowMapper.class)))
                .thenThrow(new RuntimeException("Not found"));

        // When
        Optional<Author> author = authorDao.findById(authorId);

        // Then
        assertThat(author).isEmpty();
    }

    @Test
    @DisplayName("Должен сохранить нового автора")
    void shouldSaveNewAuthor() {
        // Given
        Author authorToSave = new Author(null, "Tolstoy", "Leo", 82);
        Author expectedAuthor = new Author(1L, "Tolstoy", "Leo", 82);

        when(jdbcTemplate.update(anyString(), any(MapSqlParameterSource.class), any(GeneratedKeyHolder.class), any(String[].class)))
                .thenAnswer(invocation -> {
                    GeneratedKeyHolder keyHolder = invocation.getArgument(2);
                    keyHolder.getKeyList().add(Collections.singletonMap("id", 1L));
                    return 1;
                });

        // When
        Author savedAuthor = authorDao.save(authorToSave);

        // Then
        assertThat(savedAuthor.getId()).isEqualTo(1L);
        assertThat(savedAuthor.getLastName()).isEqualTo("Tolstoy");
        assertThat(savedAuthor.getFirstName()).isEqualTo("Leo");
        assertThat(savedAuthor.getAge()).isEqualTo(82);
    }

    @Test
    @DisplayName("Должен обновить существующего автора")
    void shouldUpdateExistingAuthor() {
        // Given
        Author authorToUpdate = new Author(1L, "Tolstoy", "Leo", 83);
        when(jdbcTemplate.update(anyString(), any(MapSqlParameterSource.class))).thenReturn(1);

        // When
        authorDao.update(authorToUpdate);

        // Then
        verify(jdbcTemplate).update(anyString(), any(MapSqlParameterSource.class));
    }

    @Test
    @DisplayName("Должен удалить автора по ID")
    void shouldDeleteAuthorById() {
        // Given
        Long authorId = 1L;
        when(jdbcTemplate.update(anyString(), any(Map.class))).thenReturn(1);

        // When
        authorDao.deleteById(authorId);

        // Then
        verify(jdbcTemplate).update(eq("DELETE FROM author WHERE id = :id"), eq(Collections.singletonMap("id", authorId)));
    }

    @Test
    @DisplayName("Должен найти автора по имени и фамилии")
    void shouldFindAuthorByFullName() {
        // Given
        String firstName = "Leo";
        String lastName = "Tolstoy";
        Author expectedAuthor = new Author(1L, lastName, firstName, 82);

        when(jdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class)))
                .thenReturn(expectedAuthor);

        // When
        Optional<Author> author = authorDao.findByFullName(firstName, lastName);

        // Then
        assertThat(author).isPresent();
        assertThat(author.get()).isEqualTo(expectedAuthor);
    }

    @Test
    @DisplayName("RowMapper должен корректно маппить ResultSet в Author")
    void rowMapperShouldMapResultSetToAuthor() throws SQLException {
        // Given
        ResultSet rs = mock(ResultSet.class);
        when(rs.getLong("id")).thenReturn(1L);
        when(rs.getString("last_name")).thenReturn("Tolstoy");
        when(rs.getString("first_name")).thenReturn("Leo");
        when(rs.getInt("age")).thenReturn(82);
        when(rs.wasNull()).thenReturn(false);

        AuthorDaoJdbc.AuthorRowMapper rowMapper = new AuthorDaoJdbc.AuthorRowMapper();

        // When
        Author author = rowMapper.mapRow(rs, 1);

        // Then
        assertThat(author.getId()).isEqualTo(1L);
        assertThat(author.getLastName()).isEqualTo("Tolstoy");
        assertThat(author.getFirstName()).isEqualTo("Leo");
        assertThat(author.getAge()).isEqualTo(82);
    }

    @Test
    @DisplayName("RowMapper должен корректно обрабатывать null возраст")
    void rowMapperShouldHandleNullAge() throws SQLException {
        // Given
        ResultSet rs = mock(ResultSet.class);
        when(rs.getLong("id")).thenReturn(1L);
        when(rs.getString("last_name")).thenReturn("Tolstoy");
        when(rs.getString("first_name")).thenReturn("Leo");
        when(rs.getInt("age")).thenReturn(0);
        when(rs.wasNull()).thenReturn(true);

        AuthorDaoJdbc.AuthorRowMapper rowMapper = new AuthorDaoJdbc.AuthorRowMapper();

        // When
        Author author = rowMapper.mapRow(rs, 1);

        // Then
        assertThat(author.getAge()).isNull();
    }
    
    @Test
    @DisplayName("Должен вернуть пустой Optional если автор не найден по имени и фамилии")
    void shouldReturnEmptyOptionalWhenAuthorNotFoundByFullName() {
        // Given
        String firstName = "Non";
        String lastName = "Existing";
        
        when(jdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class)))
                .thenThrow(new RuntimeException("Not found"));

        // When
        Optional<Author> author = authorDao.findByFullName(firstName, lastName);

        // Then
        assertThat(author).isEmpty();
    }
}