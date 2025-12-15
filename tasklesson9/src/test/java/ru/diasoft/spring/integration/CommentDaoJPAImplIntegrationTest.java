package ru.diasoft.spring.integration;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.diasoft.spring.dao.impl.CommentDaoJPAImpl;
import ru.diasoft.spring.domain.Author;
import ru.diasoft.spring.domain.Book;
import ru.diasoft.spring.domain.Comment;
import ru.diasoft.spring.domain.Genre;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(CommentDaoJPAImpl.class)
@DisplayName("Интеграционные тесты CommentDaoJPAImpl с H2")
class CommentDaoJPAImplIntegrationTest {

    @Autowired
    private CommentDaoJPAImpl commentDao;

    @Autowired
    private TestEntityManager testEntityManager;

    private Book createTestBook() {
        // Генерируем уникальный суффикс для каждого вызова
        String uniqueSuffix = String.valueOf(System.currentTimeMillis());

        Author author = new Author(null, "Tolstoy_" + uniqueSuffix, "Leo", 82);
        testEntityManager.persist(author);

        Genre genre = new Genre(null, "Novel_" + uniqueSuffix); // Уникальное имя
        testEntityManager.persist(genre);

        Book book = new Book();
        book.setTitle("War and Peace_" + uniqueSuffix);
        book.setAuthor(author);
        book.setGenre(genre);
        testEntityManager.persist(book);

        return book;
    }

    @Test
    @Transactional
    @DisplayName("Должен сохранить и найти новый комментарий")
    void shouldSaveAndFindNewComment() {
        // Given
        Book book = createTestBook();
        Comment newComment = new Comment(null, "Отличная книга!", "Читатель1", book);

        // When
        Comment savedComment = commentDao.save(newComment);

        // Then
        assertThat(savedComment.getId()).isNotNull();

        Optional<Comment> foundComment = commentDao.findById(savedComment.getId());
        assertThat(foundComment)
                .isPresent()
                .get()
                .satisfies(comment -> {
                    assertThat(comment.getDescription()).isEqualTo("Отличная книга!");
                    assertThat(comment.getNickname()).isEqualTo("Читатель1");
                    assertThat(comment.getBook()).isNotNull();
                    assertThat(comment.getBook().getId()).isEqualTo(book.getId());
                });
    }

    @Test
    @Transactional
    @DisplayName("Должен найти все комментарии")
    void shouldFindAllComments() {
        // Given
        Book book = createTestBook();

        Comment comment1 = new Comment(null, "Первый комментарий", "Читатель1", book);
        Comment comment2 = new Comment(null, "Второй комментарий", "Читатель2", book);

        testEntityManager.persist(comment1);
        testEntityManager.persist(comment2);
        testEntityManager.flush();

        // When
        List<Comment> comments = commentDao.findAll();

        // Then
        assertThat(comments).hasSize(2);
        assertThat(comments)
                .extracting(Comment::getNickname)
                .containsExactlyInAnyOrder("Читатель1", "Читатель2");
    }

    @Test
    @DisplayName("Должен обновить комментарий")
    @Transactional
    void shouldUpdateComment() {
        // Given
        Book book = createTestBook();
        Comment comment = new Comment(null, "Старый текст", "СтарыйНик", book);
        testEntityManager.persist(comment);
        testEntityManager.flush();

        comment.setDescription("Новый текст");
        comment.setNickname("НовыйНик");

        // When
        commentDao.update(comment);
        testEntityManager.clear(); // Очищаем контекст

        // Then
        Comment updatedComment = testEntityManager.find(Comment.class, comment.getId());
        assertThat(updatedComment.getDescription()).isEqualTo("Новый текст");
        assertThat(updatedComment.getNickname()).isEqualTo("НовыйНик");
    }

    @Test
    @DisplayName("Должен удалить комментарий по ID")
    @Transactional
    void shouldDeleteCommentById() {
        // Given
        Book book = createTestBook();
        Comment comment = new Comment(null, "Комментарий для удаления", "Удаляемый", book);
        testEntityManager.persist(comment);
        testEntityManager.flush();
        Long commentId = comment.getId();

        // Проверяем, что комментарий сохранен
        Comment savedComment = testEntityManager.find(Comment.class, commentId);
        assertThat(savedComment).isNotNull();

        // When
        commentDao.deleteById(commentId);
        testEntityManager.flush(); // Форсируем выполнение DELETE
        testEntityManager.clear(); // ОЧИЩАЕМ КЕШ! Это ключевой момент

        // Then - ищем в БД после очистки кеша
        Comment deletedComment = testEntityManager.find(Comment.class, commentId);
        assertThat(deletedComment).isNull();
    }

    @Test
    @Transactional
    @DisplayName("Должен найти комментарии по nickname")
    void shouldFindCommentsByNickname() {
        // Given
        Book book = createTestBook();
        String nickname = "ПостоянныйЧитатель";

        Comment comment1 = new Comment(null, "Комментарий 1", nickname, book);
        Comment comment2 = new Comment(null, "Комментарий 2", nickname, book);
        Comment comment3 = new Comment(null, "Комментарий 3", "ДругойЧитатель", book);

        testEntityManager.persist(comment1);
        testEntityManager.persist(comment2);
        testEntityManager.persist(comment3);
        testEntityManager.flush();

        // When
        List<Comment> comments = commentDao.findByNickname(nickname);

        // Then
        assertThat(comments).hasSize(2);
        assertThat(comments)
                .extracting(Comment::getNickname)
                .containsOnly(nickname);
    }

    @Test
    @Transactional
    @DisplayName("Должен найти комментарии по bookId")
    void shouldFindCommentsByBookId() {
        // Given
        Book book1 = createTestBook();
        Book book2 = createTestBook();
        book2.setTitle("Another Book");
        testEntityManager.persist(book2);

        Comment comment1 = new Comment(null, "Комментарий к книге 1", "Читатель1", book1);
        Comment comment2 = new Comment(null, "Еще комментарий к книге 1", "Читатель2", book1);
        Comment comment3 = new Comment(null, "Комментарий к книге 2", "Читатель3", book2);

        testEntityManager.persist(comment1);
        testEntityManager.persist(comment2);
        testEntityManager.persist(comment3);
        testEntityManager.flush();

        // When
        List<Comment> comments = commentDao.findByBookId(book1.getId());

        // Then
        assertThat(comments).hasSize(2);
        assertThat(comments)
                .extracting(c -> c.getBook().getId())
                .containsOnly(book1.getId());
    }

    @Test
    @Transactional
    @DisplayName("Должен вернуть пустой список при поиске комментариев несуществующего bookId")
    void shouldReturnEmptyListForNonExistingBookId() {
        // When
        List<Comment> comments = commentDao.findByBookId(999L);

        // Then
        assertThat(comments).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("Должен вернуть пустой список при поиске комментариев несуществующего nickname")
    void shouldReturnEmptyListForNonExistingNickname() {
        // When
        List<Comment> comments = commentDao.findByNickname("НесуществующийНик");

        // Then
        assertThat(comments).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("Должен корректно сохранять комментарий с null description")
    void shouldSaveCommentWithNullDescription() {
        // Given
        Book book = createTestBook();
        Comment comment = new Comment(null, null, "Читатель", book);

        // When
        Comment savedComment = commentDao.save(comment);

        // Then
        assertThat(savedComment.getId()).isNotNull();
        assertThat(savedComment.getDescription()).isNull();
        assertThat(savedComment.getNickname()).isEqualTo("Читатель");
    }
}