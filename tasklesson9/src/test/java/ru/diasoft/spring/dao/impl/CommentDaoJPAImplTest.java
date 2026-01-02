package ru.diasoft.spring.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.diasoft.spring.domain.Comment;
import ru.diasoft.spring.domain.Book;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тестирование CommentDaoJPAImpl (мок-тесты)")
class CommentDaoJPAImplTest {

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<Comment> typedQuery;

    @Mock
    private Query query;

    private CommentDaoJPAImpl commentDao;

    @BeforeEach
    void setUp() {
        commentDao = new CommentDaoJPAImpl();
        
        // Используем reflection для установки EntityManager
        try {
            var field = CommentDaoJPAImpl.class.getDeclaredField("em");
            field.setAccessible(true);
            field.set(commentDao, em);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Должен найти все комментарии")
    void shouldFindAllComments() {
        // Given
        Comment comment1 = new Comment(1L, "Отличная книга!", "Читатель1", null);
        Comment comment2 = new Comment(2L, "Очень понравилось", "Читатель2", null);
        List<Comment> expectedComments = Arrays.asList(comment1, comment2);

        when(em.createQuery("select s from Comment s", Comment.class)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expectedComments);

        // When
        List<Comment> comments = commentDao.findAll();

        // Then
        assertThat(comments).hasSize(2);
        assertThat(comments).containsExactly(comment1, comment2);

        verify(em).createQuery("select s from Comment s", Comment.class);
        verify(typedQuery).getResultList();
    }

    @Test
    @DisplayName("Должен найти комментарий по ID")
    void shouldFindCommentById() {
        // Given
        Long commentId = 1L;
        Comment expectedComment = new Comment(commentId, "Отличная книга!", "Читатель1", null);
        
        when(em.find(Comment.class, commentId)).thenReturn(expectedComment);

        // When
        Optional<Comment> comment = commentDao.findById(commentId);

        // Then
        assertThat(comment).isPresent();
        assertThat(comment.get()).isEqualTo(expectedComment);
        
        verify(em).find(Comment.class, commentId);
    }

    @Test
    @DisplayName("Должен вернуть пустой Optional если комментарий не найден по ID")
    void shouldReturnEmptyOptionalWhenCommentNotFoundById() {
        // Given
        Long commentId = 999L;
        when(em.find(Comment.class, commentId)).thenReturn(null);

        // When
        Optional<Comment> comment = commentDao.findById(commentId);

        // Then
        assertThat(comment).isEmpty();
        
        verify(em).find(Comment.class, commentId);
    }

    @Test
    @DisplayName("Должен сохранить новый комментарий (id = null)")
    void shouldSaveNewCommentWhenIdIsNull() {
        // Given
        Comment commentToSave = new Comment(null, "Отличная книга!", "Читатель1", null);

        doAnswer(invocation -> {
            Comment comment = invocation.getArgument(0);
            // Эмулируем сохранение в БД
            return null;
        }).when(em).persist(any(Comment.class));

        // When
        Comment result = commentDao.save(commentToSave);

        // Then
        assertThat(result).isSameAs(commentToSave);
        
        verify(em).persist(commentToSave);
        verify(em).flush();
        verify(em, never()).merge(any(Comment.class));
    }

    @Test
    @DisplayName("Должен обновить существующий комментарий через merge (id != null)")
    void shouldUpdateExistingCommentViaMerge() {
        // Given
        Comment existingComment = new Comment(1L, "Старый текст", "Читатель1", null);
        Comment mergedComment = new Comment(1L, "Новый текст", "Читатель1", null);
        
        when(em.merge(existingComment)).thenReturn(mergedComment);

        // When
        Comment result = commentDao.save(existingComment);

        // Then
        assertThat(result).isSameAs(mergedComment);
        
        verify(em).merge(existingComment);
        verify(em, never()).persist(any(Comment.class));
        verify(em, never()).flush();
    }

    @Test
    @DisplayName("Должен обновить комментарий через JPQL update")
    void shouldUpdateCommentViaJPQL() {
        // Given
        Comment commentToUpdate = new Comment(1L, "Обновленный текст", "НовыйНик", null);
        
        when(em.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("description"), eq("Обновленный текст"))).thenReturn(query);
        when(query.setParameter(eq("nickname"), eq("НовыйНик"))).thenReturn(query);
        when(query.setParameter(eq("id"), eq(1L))).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);

        // When
        commentDao.update(commentToUpdate);

        // Then
        verify(em).createQuery("update Comment  s set s.description = :description, s.nickname = :nickname where s.id = :id");
        verify(query).setParameter("description", "Обновленный текст");
        verify(query).setParameter("nickname", "НовыйНик");
        verify(query).setParameter("id", 1L);
        verify(query).executeUpdate();
    }

    @Test
    @DisplayName("Должен удалить комментарий по ID")
    void shouldDeleteCommentById() {
        // Given
        Long commentId = 1L;
        
        when(em.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("id"), eq(commentId))).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);

        // When
        commentDao.deleteById(commentId);

        // Then
        verify(em).createQuery("delete  Comment  s where s.id = :id");
        verify(query).setParameter("id", commentId);
        verify(query).executeUpdate();
    }

    @Test
    @DisplayName("Должен найти комментарии по nickname")
    void shouldFindCommentsByNickname() {
        // Given
        String nickname = "Читатель1";
        Comment comment1 = new Comment(1L, "Первый комментарий", nickname, null);
        Comment comment2 = new Comment(2L, "Второй комментарий", nickname, null);
        List<Comment> expectedComments = Arrays.asList(comment1, comment2);
        
        when(em.createQuery("select s from Comment s where s.nickname = :nickname", Comment.class))
                .thenReturn(typedQuery);
        when(typedQuery.setParameter("nickname", nickname)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expectedComments);

        // When
        List<Comment> comments = commentDao.findByNickname(nickname);

        // Then
        assertThat(comments).hasSize(2);
        assertThat(comments).containsExactly(comment1, comment2);
        
        verify(em).createQuery("select s from Comment s where s.nickname = :nickname", Comment.class);
        verify(typedQuery).setParameter("nickname", nickname);
        verify(typedQuery).getResultList();
    }

    @Test
    @DisplayName("Должен вернуть пустой список если комментарии не найдены по nickname")
    void shouldReturnEmptyListWhenNoCommentsFoundByNickname() {
        // Given
        String nickname = "Несуществующий";
        
        when(em.createQuery("select s from Comment s where s.nickname = :nickname", Comment.class))
                .thenReturn(typedQuery);
        when(typedQuery.setParameter("nickname", nickname)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList());

        // When
        List<Comment> comments = commentDao.findByNickname(nickname);

        // Then
        assertThat(comments).isEmpty();
    }

    @Test
    @DisplayName("Должен найти комментарии по bookId")
    void shouldFindCommentsByBookId() {
        // Given
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        
        Comment comment1 = new Comment(1L, "Комментарий к книге 1", "Читатель1", book);
        Comment comment2 = new Comment(2L, "Еще комментарий к книге 1", "Читатель2", book);
        List<Comment> expectedComments = Arrays.asList(comment1, comment2);
        
        when(em.createQuery(
                "select c from Comment c join c.book b where b.id = :bookId", Comment.class))
                .thenReturn(typedQuery);
        when(typedQuery.setParameter("bookId", bookId)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expectedComments);

        // When
        List<Comment> comments = commentDao.findByBookId(bookId);

        // Then
        assertThat(comments).hasSize(2);
        assertThat(comments).containsExactly(comment1, comment2);
        
        verify(em).createQuery(
                "select c from Comment c join c.book b where b.id = :bookId", Comment.class);
        verify(typedQuery).setParameter("bookId", bookId);
        verify(typedQuery).getResultList();
    }

    @Test
    @DisplayName("Должен корректно обрабатывать null nickname в findByNickname")
    void shouldHandleNullNicknameInFindByNickname() {
        // Given
        when(em.createQuery("select s from Comment s where s.nickname = :nickname", Comment.class))
                .thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("nickname"), isNull())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList());

        // When
        List<Comment> comments = commentDao.findByNickname(null);

        // Then
        assertThat(comments).isEmpty();
    }

    @Test
    @DisplayName("Должен корректно обрабатывать null description при update")
    void shouldHandleNullDescriptionInUpdate() {
        // Given
        Comment commentToUpdate = new Comment(1L, null, "Никнейм", null);
        
        when(em.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("description"), isNull())).thenReturn(query);
        when(query.setParameter(eq("nickname"), eq("Никнейм"))).thenReturn(query);
        when(query.setParameter(eq("id"), eq(1L))).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);

        // When
        commentDao.update(commentToUpdate);

        // Then
        verify(query).setParameter("description", null);
        verify(query).setParameter("nickname", "Никнейм");
        verify(query).setParameter("id", 1L);
    }
}