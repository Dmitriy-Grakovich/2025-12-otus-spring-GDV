package ru.diasoft.spring.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.diasoft.spring.domain.Author;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тестирование AuthorDaoIJPAImpl")
class AuthorDaoIJPAImplTest {

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<Author> typedQuery;

    @Mock
    private Query query;

    private AuthorDaoIJPAmpl authorDao;

    @BeforeEach
    void setUp() {
        authorDao = new AuthorDaoIJPAmpl();
        
        // Используем reflection для установки EntityManager в приватное поле
        // Так как @PersistenceContext инжектит через setter или прямо в поле
        try {
            var field = AuthorDaoIJPAmpl.class.getDeclaredField("em");
            field.setAccessible(true);
            field.set(authorDao, em);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Должен найти всех авторов")
    void shouldFindAllAuthors() {
        // Given
        Author author1 = new Author(1L, "Tolstoy", "Leo", 82);
        Author author2 = new Author(2L, "Dostoevsky", "Fyodor", 59);
        List<Author> expectedAuthors = Arrays.asList(author1, author2);

        when(em.createQuery(anyString(), eq(Author.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expectedAuthors);

        // When
        List<Author> authors = authorDao.findAll();

        // Then
        assertThat(authors).hasSize(2);
        assertThat(authors).containsExactly(author1, author2);

        verify(em).createQuery("select s from Author s", Author.class);
        verify(typedQuery).getResultList();
    }

    @Test
    @DisplayName("Должен найти автора по ID")
    void shouldFindAuthorById() {
        // Given
        Long authorId = 1L;
        Author expectedAuthor = new Author(authorId, "Tolstoy", "Leo", 82);
        
        when(em.find(Author.class, authorId)).thenReturn(expectedAuthor);

        // When
        Optional<Author> author = authorDao.findById(authorId);

        // Then
        assertThat(author).isPresent();
        assertThat(author.get()).isEqualTo(expectedAuthor);
        
        verify(em).find(Author.class, authorId);
    }

    @Test
    @DisplayName("Должен вернуть пустой Optional если автор не найден по ID")
    void shouldReturnEmptyOptionalWhenAuthorNotFoundById() {
        // Given
        Long authorId = 999L;
        when(em.find(Author.class, authorId)).thenReturn(null);

        // When
        Optional<Author> author = authorDao.findById(authorId);

        // Then
        assertThat(author).isEmpty();
        
        verify(em).find(Author.class, authorId);
    }


    @Test
    @DisplayName("Должен обновить существующего автора через merge (id != 0)")
    void shouldUpdateExistingAuthorViaMerge() {
        // Given
        Author existingAuthor = new Author(1L, "Tolstoy", "Leo", 82);
        Author mergedAuthor = new Author(1L, "Tolstoy", "Leo", 83);
        
        when(em.merge(existingAuthor)).thenReturn(mergedAuthor);

        // When
        Author result = authorDao.save(existingAuthor);

        // Then
        assertThat(result).isSameAs(mergedAuthor);
        
        verify(em).merge(existingAuthor);
        verify(em, never()).persist(any(Author.class));
        verify(em, never()).flush();
    }

    @Test
    @DisplayName("Должен обновить автора через JPQL update")
    void shouldUpdateAuthorViaJPQL() {
        // Given
        Author authorToUpdate = new Author(1L, "Tolstoy", "Leo", 83);
        
        when(em.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("lastName"), eq("Tolstoy"))).thenReturn(query);
        when(query.setParameter(eq("firstName"), eq("Leo"))).thenReturn(query);
        when(query.setParameter(eq("age"), eq(83))).thenReturn(query);
        when(query.setParameter(eq("id"), eq(1L))).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);

        // When
        authorDao.update(authorToUpdate);

        // Then
        verify(em).createQuery("update Author  s set s.lastName = :lastName, s.firstName = :firstName, s.age = :age where s.id = :id");
        verify(query).setParameter("lastName", "Tolstoy");
        verify(query).setParameter("firstName", "Leo");
        verify(query).setParameter("age", 83);
        verify(query).setParameter("id", 1L);
        verify(query).executeUpdate();
    }

    @Test
    @DisplayName("Должен удалить автора по ID")
    void shouldDeleteAuthorById() {
        // Given
        Long authorId = 1L;
        
        when(em.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("id"), eq(authorId))).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);

        // When
        authorDao.deleteById(authorId);

        // Then
        verify(em).createQuery("delete  Author  s where s.id = :id");
        verify(query).setParameter("id", authorId);
        verify(query).executeUpdate();
    }








    @Test
    @DisplayName("Должен корректно работать с нулевым возрастом")
    void shouldHandleZeroAge() {
        // Given
        Long authorId = 1L;
        Author authorWithZeroAge = new Author(authorId, "Tolstoy", "Leo", 0);
        
        when(em.find(Author.class, authorId)).thenReturn(authorWithZeroAge);

        // When
        Optional<Author> author = authorDao.findById(authorId);

        // Then
        assertThat(author).isPresent();
        assertThat(author.get().getAge()).isEqualTo(0);
    }

    @Test
    @DisplayName("Должен корректно работать с автором без возраста (null)")
    void shouldHandleNullAge() {
        // Given
        Long authorId = 1L;
        Author authorWithNullAge = new Author(authorId, "Tolstoy", "Leo", null);
        
        when(em.find(Author.class, authorId)).thenReturn(authorWithNullAge);

        // When
        Optional<Author> author = authorDao.findById(authorId);

        // Then
        assertThat(author).isPresent();
        assertThat(author.get().getAge()).isNull();
    }
}